package rules

/**
  * Created by Kasim on 2017/7/17.
  */
import org.apache.kafka.common.TopicPartition
import org.apache.spark.rdd.RDD

trait OffsetsStore {

  def readOffsets(topic: String): Option[Map[TopicPartition, Long]]

  def saveOffsets(topic: String, rdd: RDD[_]): Unit

}
