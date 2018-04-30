# eb5001-ca-spark

Base on importing project into IntelliJ

```sh
# Generate JAR to target/scala-2.11/stream-test.jar 
sbt assembly

# Submit job for StreamTest (4 cores)
spark-submit --class StreamTest --conf spark.executor.cores=4 --conf spark.cores.max=4 target/scala-2.11/stream-test.jar

# Submit job for StructuredStreamTest (8 cores)
spark-submit --class StructuredStreamTest --conf spark.executor.cores=4 --conf spark.cores.max=8 target/scala-2.11/stream-test.jar
```
