import spray.json._

object SensorJsonProtocol extends DefaultJsonProtocol {
  implicit val sensorFormat: RootJsonFormat[SensorRecord] = jsonFormat6(SensorRecord)
}

case class SensorRecord(humidity: String, sensor_uuid: String, ambient_temperature: String,
                        timestamp: Long, photosensor: String, radiation_level : String)
