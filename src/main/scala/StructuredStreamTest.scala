import java.util.UUID.randomUUID

import org.apache.spark.sql.SparkSession
import spray.json._
import org.apache.spark.sql.functions._
import SensorJsonProtocol._

object StructuredStreamTest {
  def main(args: Array[String]) {
    val isDev = args.length > 0 && args.head.toLowerCase == "dev"

    val spark = SparkSession.builder
      .master(if (isDev) "local[*]" else "spark://192.168.2.100:7077")
      .appName("StructuredStreamTest")
      .config("spark.driver.host", "192.168.2.1")
      .config("spark.cleaner.referenceTracking.cleanCheckpoints", value = true)
      .getOrCreate()

    val sc = spark.sparkContext
    sc setLogLevel "WARN"

    import spark.implicits._

    val source = spark.readStream
      .format("socket")
      .option("host", "192.168.2.1")
      .option("port", "1337")

    val socketStream1 = source.load
    val socketStream2 = source.load

    val device1 = socketStream1.as[String].map(_.parseJson.convertTo[SensorRecord])
    val device2 = socketStream2.as[String].map(_.parseJson.convertTo[SensorRecord])

    val query1 = (device1 groupBy($"photosensor" > 700 as "socketStream1.photosensor > 700") agg count($"*"))
      .writeStream
      .queryName("device1")
      .outputMode("complete")
      .option("checkpointLocation", s"hdfs://192.168.2.1:9000/tmp/temporary-$randomUUID")
//        .options(if (isDev) Map.empty[String, String] else Map("checkpointLocation" -> s"/tmp/temporary-$randomUUID"))
      .format("console")
      .start

    val query2 = (device2 groupBy($"ambient_temperature" > 10 as "socketStream2.temp > 10") agg count($"*"))
      .writeStream
      .queryName("device2")
      .outputMode("complete")
      .option("checkpointLocation", s"hdfs://192.168.2.1:9000/tmp/temporary-$randomUUID")
//      .options(if (isDev) Map.empty[String, String] else Map("checkpointLocation" -> s"/tmp/temporary-$randomUUID"))
      .format("console")
      .start

    query1.awaitTermination
    query2.awaitTermination
  }
}
