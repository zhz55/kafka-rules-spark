import java.text.SimpleDateFormat

import ctitc.seagoing.SEAGOING.VehiclePosition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.kudu.spark.kudu._
import rules.PositionRules

/**
  * Created by Kasim on 2017/7/7.
  */
object KuduMain {

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

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("KafkaTest").setMaster("yarn")
    val ssc = new StreamingContext(conf, Seconds(10))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kf01:9092,kf02:9092,kf03:9092,kf04:9092,kf05:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[org.apache.kafka.common.serialization.ByteArrayDeserializer],
      "group.id" -> "scala_kafka_test",
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

    val kuduContext = new KuduContext("nn01")

    stream.foreachRDD(rdd => {
      if(!rdd.isEmpty()) {
        val tableArray = positionRules.tableArray()
        val correctRdd = rdd.filter(record => {
          !positionRules.positionJudge(VehiclePosition.parseFrom(record.value())).toString.contains("1") &&
            !{if(VehiclePosition.parseFrom(record.value()).accessCode
              == positionRules.repeatFilter(record.partition())) false else true}}).
          map(record => {
            val positionRecord = VehiclePosition.parseFrom(record.value())
            TableStructureVehiclePosition(
              positionRecord.vehicleNo.trim(), positionRecord.getPlateColor,
              // Date->UnixTime
              new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(positionRecord.gnss.positionTime).getTime / 1000, positionRecord.accessCode,
              positionRecord.city, positionRecord.getCurAccessCode,
              positionRecord.getTrans, positionRecord.updateTime,
              positionRecord.gnss.getEncrypt, positionRecord.gnss.lon, positionRecord.gnss.lat,
              positionRecord.gnss.getVec1, positionRecord.gnss.getVec2, positionRecord.gnss.getVec3,
              positionRecord.gnss.getDirection, positionRecord.gnss.getAltitude,
              positionRecord.gnss.getState, positionRecord.gnss.getAlarm,
              positionRecord.getReserved, positionRules.positionJudge(positionRecord).toString, 0)
          })

        val correctNowRdd = correctRdd.filter(record => {
          !positionRules.correctCrossTableFlag(record.positiontime * 1000)
        })

        val correctAcrossRdd = correctRdd.filter(record => {
          positionRules.correctCrossTableFlag(record.positiontime * 1000)
        })

        val errorRdd = rdd.filter(record => {
          positionRules.positionJudge(VehiclePosition.parseFrom(record.value())).toString.contains("1") &&
            !{if(VehiclePosition.parseFrom(record.value()).accessCode
              == positionRules.repeatFilter(record.partition())) false else true}}).
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

        val errorNowRdd = errorRdd.filter(record => {
          !positionRules.errorCrossTableFlag(record.positiontime * 1000)
        })

        val errorAcrossRdd = errorRdd.filter(record => {
          positionRules.errorCrossTableFlag(record.positiontime * 1000)
        })

        val sparkSession = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
        import sparkSession.implicits._

        try {
          kuduContext.insertIgnoreRows(
            correctNowRdd.toDF(), tableArray(0))

          kuduContext.insertIgnoreRows(
            errorNowRdd.toDF(), tableArray(1))

          kuduContext.insertIgnoreRows(
            correctAcrossRdd.toDF(), tableArray(2))

          kuduContext.insertIgnoreRows(
            errorAcrossRdd.toDF(), tableArray(3))
        } catch {
          case e:Exception => {println("insert kudu error")}
        }
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
