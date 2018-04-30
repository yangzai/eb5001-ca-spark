name := "eb5001-ca-spark"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "2.3.0"

val sparkScope = "provided"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion % sparkScope,
  "org.apache.spark" %% "spark-streaming" % sparkVersion % sparkScope,
  "io.spray" %%  "spray-json" % "1.3.3"
)

run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := "stream-test.jar"
mainClass in assembly := Some("StreamTest")
