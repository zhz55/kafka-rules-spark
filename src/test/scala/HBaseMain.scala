//import org.apache.hadoop.hbase.HBaseConfiguration
//import org.apache.hadoop.mapred.JobConf
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Kasim on 2017/8/14.
  */
/*
object HBaseMain {
  def toCreateStreamingContext(args : Array[String]) : StreamingContext = {
    val conf = new SparkConf().setAppName("InsertKudu").setMaster("yarn")
    val ssc = new StreamingContext(conf, Seconds(if (args.length == 1) args(0).toLong else 10l))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kf01:9092,kf02:9092,kf03:9092,kf04:9092,kf05:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[org.apache.kafka.common.serialization.ByteArrayDeserializer],
      "group.id" -> "kafka_insert_kudu",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("HYPT_POSITION", "LWLK_POSITION")

    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )

    val hconf = HBaseConfiguration.create()

    var jobConf = new JobConf(hconf)

    jobConf.set("hbase.zookeeper.quorum", "dn01,dn02,dn03,dn04,dn05")
    jobConf.set("zookeeper.znode.parent", "/hbase")



    ssc
  }
}
*/