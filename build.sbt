
name := "kafka-rules-spark"

version := "1.0"

scalaVersion := "2.11.11"

mainClass := Option("KafkaMain")

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.1",
  "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.1.1"
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
