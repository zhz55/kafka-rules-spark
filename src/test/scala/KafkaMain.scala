/**
  * Created by Kasim on 2017/7/5.
  */
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import rules.PositionRules

object KafkaMain {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("InsertKudu").setMaster("yarn")
    val ssc = new StreamingContext(conf, Seconds(10))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kf01:9092,kf02:9092,kf03:9092,kf04:9092,kf05:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[org.apache.kafka.common.serialization.ByteArrayDeserializer],
      "group.id" -> "scala_kafka_test",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("LWLK_POSITION","HYPT_POSITION")
    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )
    val positionRules = new PositionRules
    stream.foreachRDD(rdd => {
      if(!rdd.isEmpty()) {
        rdd.foreachPartition(partitionRecords => {
          partitionRecords.map(record => (VehiclePosition.parseFrom(record.value()),
            {if(VehiclePosition.parseFrom(record.value()).accessCode
              == positionRules.repeatFilter(record.partition())) 0 else 1},
            positionRules.positionJudge(VehiclePosition.parseFrom(record.value())))
          ).foreach(m=>m._3.toString.toLong)
        })
      }
    })

    //ssc.checkpoint("/Users/kasim/workspace/")
    ssc.start()
    ssc.awaitTermination()
  }
}
