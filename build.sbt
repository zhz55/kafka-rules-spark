
name := "kafka-rules-spark"

version := "1.0"

scalaVersion := "2.11.11"

mainClass := Option("KuduMain")

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.1" % "provided",
  "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.1.1" % "provided",
  "org.apache.spark" % "spark-sql_2.11" % "2.1.1" % "provided",
  "org.apache.kudu" % "kudu-spark2_2.11" % "1.2.0",
  "com.101tec" % "zkclient" % "0.10"
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
