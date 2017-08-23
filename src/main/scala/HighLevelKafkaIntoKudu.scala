import java.text.{ParseException, SimpleDateFormat}
import ctitc.seagoing.SEAGOING.VehiclePosition
import kafka.serializer._
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import rules.PositionRules

/**
  * Created by Kasim on 2017/8/23.
  */
object HighLevelKafkaIntoKudu {
  val path = "hdfs://nameservice1/checkpoint/insertkudu/"

  case class TableStructureVehiclePosition(vehicleno : String, platecolor : Int,
                                           positiontime : Long, accesscode : Int,
                                           city : Int, curaccesscode : Int,
                                           trans : Int, updatetime : Long,
                                           encrypt : Int, lon : Int, lat : Int,
                                           vec1 : Int, vec2 : Int, vec3 : Int,
                                           direction : Int, altitude : Int,
                                           state : Long, alarm : Long,
                                           reserved : String, errorcode : String,
                                           roadcode : Int)
  def toCreateStreamingContext(args : Array[String]) : StreamingContext = {
    val conf = new SparkConf().setAppName("InsertKudu").setMaster("yarn")
    val ssc = new StreamingContext(conf, Seconds(if (!args(0).isEmpty) args(0).toLong else 10l))

    val kafkaParams = Map[String, String](
      "group.id" -> "kafka_insert_kudu",
      "zookeeper.connect" -> "dn01:2181,dn02:2181,dn03:2181,dn04:2181,dn05:2181"
    )

    val topics = Array("HYPT_POSITION","LWLK_POSITION").map((_,2)).toMap


    val streams = (1 to (if (!args(1).isEmpty) args(1).toInt else 8)) map {_ =>
      KafkaUtils.createStream[String,Array[Byte],StringDecoder,DefaultDecoder](
        ssc,
        kafkaParams,
        topics,
        StorageLevel.MEMORY_AND_DISK_SER
      )
    }

    val positionRules = new PositionRules

    val kuduContext = ssc.sparkContext.broadcast(new KuduContext("nn01"))
    val sparkSession = SparkSession.builder().config(conf).getOrCreate()
    import sparkSession.implicits._

    ssc.union(streams).foreachRDD(rdd => {
      val tableArray = positionRules.tableArray()
      try {
        val noRepeatedRdd = rdd.
          filter(record => !{
            if(!record._2.isEmpty) {
              if(VehiclePosition.parseFrom(record._2).accessCode
                == ((VehiclePosition.parseFrom(record._2).curAccessCode.getOrElse(0)/10000)* 10000)) false else true
            } else true}).
          map(record => {
            val positionRecord = VehiclePosition.parseFrom(record._2)
            TableStructureVehiclePosition(
              positionRecord.vehicleNo.trim(), positionRecord.getPlateColor,
              // Date->UnixTime
              {
                if ("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$".r.pattern.matcher(positionRecord.gnss.positionTime).matches())
                  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(positionRecord.gnss.positionTime).getTime / 1000
                else 0
              }, positionRecord.accessCode,
              positionRecord.city, positionRecord.getCurAccessCode,
              positionRecord.getTrans, positionRecord.updateTime,
              positionRecord.gnss.getEncrypt, positionRecord.gnss.lon, positionRecord.gnss.lat,
              positionRecord.gnss.getVec1, positionRecord.gnss.getVec2, positionRecord.gnss.getVec3,
              positionRecord.gnss.getDirection, positionRecord.gnss.getAltitude,
              positionRecord.gnss.getState, positionRecord.gnss.getAlarm,
              positionRecord.getReserved, positionRules.positionJudge(positionRecord).toString, 0)
          })

        kuduContext.value.insertIgnoreRows(
          noRepeatedRdd.filter(record => {
            !positionRules.crossTableFlag(record.positiontime * 1000)
          }).toDF(), tableArray(0))

        kuduContext.value.insertIgnoreRows(
          noRepeatedRdd.filter(record => {
            positionRules.crossTableFlag(record.positiontime * 1000)
          }).toDF(), tableArray(1))

      } catch {
        case e:Exception => {println(e.getMessage)}
        case e:ParseException => {println("time parse error")}
      }

    })

    ssc
  }

  def main(args: Array[String]): Unit = {

    val ssc = StreamingContext.getOrCreate(path, () => toCreateStreamingContext(args))

    ssc.start()

    ssc.awaitTermination()

    ssc.stop(stopSparkContext = true, stopGracefully = true)
  }
}
