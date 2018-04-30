import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import spray.json._

import SensorJsonProtocol._

object StreamTest {
  case class Test(a: Int)
  def main(args: Array[String]) {
    val isDev = args.length > 0 && args.head.toLowerCase == "dev"

    val spark = SparkSession.builder
      .master(if (isDev) "local[*]" else "spark://192.168.2.100:7077")
      .appName("StreamTest")
      .config("spark.driver.host", "192.168.2.1")
      .getOrCreate

    val sc = spark.sparkContext
    sc setLogLevel "WARN"

    val ssc = new StreamingContext(sc, Seconds(1))

    val lines = ssc.socketTextStream("192.168.2.1", 1337)

    val device = lines.map(_.parseJson.convertTo[SensorRecord])

    device.print

    ssc.start
    ssc.awaitTermination
  }
}
