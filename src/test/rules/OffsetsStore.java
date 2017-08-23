package rules;

trait OffsetsStore {

  def readOffsets(topic: String): Option[Map[TopicPartition, Long]]

  def saveOffsets(topic: String, rdd: RDD[_]): Unit

}
