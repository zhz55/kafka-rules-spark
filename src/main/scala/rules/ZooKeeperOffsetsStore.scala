package rules

/**
  * Created by Kasim on 2017/7/17.
  */
import kafka.utils.{Logging, ZkUtils}
import org.I0Itec.zkclient._
import org.apache.kafka.common.TopicPartition
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010._

class ZooKeeperOffsetsStore(zkHosts: String, zkPath: String) extends OffsetsStore with Logging{

  private val zkClient = new ZkClient(zkHosts, 10000, 10000)
  private val zkConnection = new ZkConnection("dn01:2181,dn02:2181,dn03:2181,dn04:2181,dn05:2181")
  private val zkUtils = new ZkUtils(zkClient, zkConnection, false)

  // Read the previously saved offsets from Zookeeper
  override def readOffsets(topic: String): Option[Map[TopicPartition, Long]] = {

    logger.info("Reading offsets from ZooKeeper")
    val stopwatch = new Stopwatch()

    val (offsetsRangesStrOpt, _) = zkUtils.readDataMaybeNull(zkPath)

    offsetsRangesStrOpt match {
      case Some(offsetsRangesStr) =>
        logger.debug(s"Read offset ranges: ${offsetsRangesStr}")

        val offsets = offsetsRangesStr.split(",")
          .map(s => s.split(":"))
          .map { case Array(partitionStr, offsetStr) => (new TopicPartition(topic, partitionStr.toInt) -> offsetStr.toLong) }
          .toMap

        logger.info("Done reading offsets from ZooKeeper. Took " + stopwatch)
        //println("Done reading offsets from ZooKeeper. Took " + stopwatch)
        Some(offsets)
      case None =>
        logger.info("No offsets found in ZooKeeper. Took " + stopwatch)
        //println("No offsets found in ZooKeeper. Took " + stopwatch)
        None
    }

  }

  // Save the offsets back to ZooKeeper
  //
  // IMPORTANT: We're not saving the offset immediately but instead save the offset from the previous batch. This is
  // because the extraction of the offsets has to be done at the beginning of the stream processing, before the real
  // logic is applied. Instead, we want to save the offsets once we have successfully processed a batch, hence the
  // workaround.
  override def saveOffsets(topic: String, rdd: RDD[_]): Unit = {

    logger.info("Saving offsets to ZooKeeper")
    val stopwatch = new Stopwatch()

    val offsetsRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    offsetsRanges.foreach(offsetRange => logger.debug(s"Using ${offsetRange}"))

    val offsetsRangesStr = offsetsRanges.map(offsetRange => s"${offsetRange.partition}:${offsetRange.fromOffset}")
      .mkString(",")
    logger.debug(s"Writing offsets to ZooKeeper: ${offsetsRangesStr}")
    //println(s"Writing offsets to ZooKeeper: ${offsetsRangesStr}" + ", " + zkPath)
    zkUtils.updatePersistentPath(zkPath, offsetsRangesStr)

    logger.info("Done updating offsets in ZooKeeper. Took " + stopwatch)
    //println("Done updating offsets in ZooKeeper. Took " + stopwatch)

  }

}
