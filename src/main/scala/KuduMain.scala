import java.text.{ParseException, SimpleDateFormat}

import ctitc.seagoing.SEAGOING.VehiclePosition
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.kudu.spark.kudu._
import org.apache.spark.streaming.dstream.InputDStream
import rules._

/**
  * Created by Kasim on 2017/7/7.
  */
object KuduMain {
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
    val ssc = new StreamingContext(conf, Seconds(if(args.length == 1) args(0).toLong else 10l))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kf01:9092,kf02:9092,kf03:9092,kf04:9092,kf05:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[org.apache.kafka.common.serialization.ByteArrayDeserializer],
      "group.id" -> "kafka_insert_kudu",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("HYPT_POSITION","LWLK_POSITION")

    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )

    val positionRules = new PositionRules

    val kuduContext = ssc.sparkContext.broadcast(new KuduContext("nn01"))
    val sparkSession = SparkSession.builder().config(conf).getOrCreate()
    import sparkSession.implicits._
    stream.foreachRDD(rdd => {
      val tableArray = positionRules.tableArray()

      try {
        val noRepeatedRdd = rdd.filter(record => !{
          if(!record.value().isEmpty) {
            if(VehiclePosition.parseFrom(record.value()).accessCode
              == positionRules.repeatFilter(record.partition())) false else true
          } else true}).
          map(record => {
            val positionRecord = VehiclePosition.parseFrom(record.value())
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
