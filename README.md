## spring-pulsar-airquality


Timothy Spann

### New Spring Module for Apache Pulsar

This uses https://github.com/spring-projects-experimental/spring-pulsar

### Setup

* Visual Code with Spring Boot 2.72. & Java 17
* Apache Pulsar Version 2.10.1 works with 2.9.1+
* Set an environment variable with your api key code from airnow
* Point to your Apache Pulsar cluster, if you are using StreamNative cloud I have SSL and configuration in the config class

### src/main/resources/application.yml


### Spark Run

````
val dfPulsar = spark.readStream.format("pulsar").option("service.url", "pulsar://pulsar1:6650").option("admin.url", "http://pulsar1:8080").option("topic", "persistent://public/default/airquality").load()

dfPulsar.printSchema()

scala> dfPulsar.printSchema()
root
 |-- additionalProperties: map (nullable = true)
 |    |-- key: string
 |    |-- value: struct (valueContainsNull = false)
 |-- aqi: integer (nullable = true)
 |-- category: struct (nullable = true)
 |    |-- additionalProperties: map (nullable = true)
 |    |    |-- key: string
 |    |    |-- value: struct (valueContainsNull = false)
 |    |-- name: string (nullable = true)
 |    |-- number: integer (nullable = true)
 |-- dateObserved: string (nullable = true)
 |-- hourObserved: integer (nullable = true)
 |-- latitude: double (nullable = true)
 |-- localTimeZone: string (nullable = true)
 |-- longitude: double (nullable = true)
 |-- parameterName: string (nullable = true)
 |-- reportingArea: string (nullable = true)
 |-- stateCode: string (nullable = true)
 |-- __key: binary (nullable = true)
 |-- __topic: string (nullable = true)
 |-- __messageId: binary (nullable = true)
 |-- __publishTime: timestamp (nullable = true)
 |-- __eventTime: timestamp (nullable = true)
 |-- __messageProperties: map (nullable = true)
 |    |-- key: string
 |    |-- value: string (valueContainsNull = true)



## Example Queries

val pQuery = dfPulsar.selectExpr("*").writeStream.format("console").option("truncate", false).start()

val pQuery = dfPulsar.selectExpr("CAST(__key AS STRING)", 
                                 "CAST(aqi AS INTEGER)",
                                 "CAST(dateObserved AS STRING)",
                                 "CAST(hourObserved AS INTEGER)",
                                 "CAST(latitude AS DOUBLE)",
                                 "CAST(localTimeZone AS STRING)",
                                 "CAST(longitude AS DOUBLE)",
                                 "CAST(parameterName AS STRING)",
                                 "CAST(reportingArea AS STRING)",
                                 "CAST(stateCode AS STRING)")
                                 .as[(String, Integer, String, Integer, Double, String, Double, String, String, String)]
            .writeStream.format("csv")
            .option("truncate", "false")
            .option("header", true)
            .option("path", "/opt/demo/airquality")
            .option("checkpointLocation", "/tmp/checkpoint")
            .start()


pQuery.explain()


pQuery: org.apache.spark.sql.DataFrame = [__key: string, aqi: int ... 8 more fields]

scala>                                  .as[(String, Integer, String, Integer, Double, String, Double, String, String, String)]
res7: org.apache.spark.sql.Dataset[(String, Integer, String, Integer, Double, String, Double, String, String, String)] = [__key: string, aqi: int ... 8 more fields]

scala>             .writeStream.format("csv")
res8: org.apache.spark.sql.streaming.DataStreamWriter[(String, Integer, String, Integer, Double, String, Double, String, String, String)] = org.apache.spark.sql.streaming.DataStreamWriter@28af99b4

scala>             .option("truncate", "false")
res9: org.apache.spark.sql.streaming.DataStreamWriter[(String, Integer, String, Integer, Double, String, Double, String, String, String)] = org.apache.spark.sql.streaming.DataStreamWriter@28af99b4

scala>             .option("header", true)
res10: org.apache.spark.sql.streaming.DataStreamWriter[(String, Integer, String, Integer, Double, String, Double, String, String, String)] = org.apache.spark.sql.streaming.DataStreamWriter@28af99b4

scala>             .option("path", "/opt/demo/airquality")
res11: org.apache.spark.sql.streaming.DataStreamWriter[(String, Integer, String, Integer, Double, String, Double, String, String, String)] = org.apache.spark.sql.streaming.DataStreamWriter@28af99b4

scala>             .option("checkpointLocation", "/tmp/checkpoint")
res12: org.apache.spark.sql.streaming.DataStreamWriter[(String, Integer, String, Integer, Double, String, Double, String, String, String)] = org.apache.spark.sql.streaming.DataStreamWriter@28af99b4

scala>             .start()
22/04/07 17:43:34 WARN ResolveWriteToStream: spark.sql.adaptive.enabled is not supported in streaming DataFrames/Datasets and will be disabled.
res13: org.apache.spark.sql.streaming.StreamingQuery = org.apache.spark.sql.execution.streaming.StreamingQueryWrapper@29b96be0

pQuery.explain()                                                                
== Physical Plan ==
*(1) Project [cast(__key#28 as string) AS __key#156, aqi#18, dateObserved#20, hourObserved#21, latitude#22, localTimeZone#23, longitude#24, parameterName#25, reportingArea#26, stateCode#27]
+- StreamingRelation pulsar, [additionalProperties#17, aqi#18, category#19, dateObserved#20, hourObserved#21, latitude#22, localTimeZone#23, longitude#24, parameterName#25, reportingArea#26, stateCode#27, __key#28, __topic#29, __messageId#30, __publishTime#31, __eventTime#32, __messageProperties#33]



drwxr-xr-x 2 root root 4096 Apr  7 17:44 _spark_metadata
-rw-r--r-- 1 root root  282 Apr  7 17:44 part-00000-249b23ad-ed90-4cd6-a8d1-3adbe7976815-c000.csv
-rw-r--r-- 1 root root  192 Apr  7 17:44 part-00000-24a6f1a6-1b71-4087-ac53-67739a1090d1-c000.csv
-rw-r--r-- 1 root root  282 Apr  7 17:44 part-00000-b3c5c7e8-a06e-4aa2-a321-45ce213e8bd4-c000.csv
-rw-r--r-- 1 root root  192 Apr  7 17:44 part-00000-ad4e60c8-6d55-4cd8-aea4-a5314272dc25-c000.csv
-rw-r--r-- 1 root root  107 Apr  7 17:43 part-00000-3c10eaf9-17e9-46fe-b58b-d8121f02c850-c000.csv
root@pulsar1:/opt/demo/airquality# cat part-00000-249b23ad-ed90-4cd6-a8d1-3adbe7976815-c000.csv
__key,aqi,dateObserved,hourObserved,latitude,localTimeZone,longitude,parameterName,reportingArea,stateCode
8107a050-1c59-4d67-aabd-9752156662c5,16,2022-04-07,16,33.65,EST,-84.43,PM2.5,Atlanta,GA
11bd646a-f464-4035-9095-3376e5a55c8e,14,2022-04-07,16,33.65,EST,-84.43,PM10,Atlanta,GA



````

### Example Run

````

export AIRPORTNOWAPIURL="https://www.airnowapi.org/aq/observation/zipCode/current/?format=application/json&zipCode=30313&distance=25&API_KEY=THISISMYKEYITISCOOL

[INFO] --- spring-boot-maven-plugin:2.7.2:run (default-cli) @ airquality ---
[INFO] Attaching agents: []

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.2)

2022-09-04 10:09:41.055  INFO 67971 --- [           main] d.datainmotion.airquality.AirQualityApp  : Starting AirQualityApp using Java 17.0.4.1 on Timothys-MBP.fios-router.home with PID 67971 (/Users/tspann/Documents/code/springpulsar/airquality/spring-pulsar-airquality/target/classes started by tspann in /Users/tspann/Documents/code/springpulsar/airquality/spring-pulsar-airquality)
2022-09-04 10:09:41.057  INFO 67971 --- [           main] d.datainmotion.airquality.AirQualityApp  : No active profile set, falling back to 1 default profile: "default"
2022-09-04 10:09:41.674  INFO 67971 --- [           main] faultConfiguringBeanFactoryPostProcessor : No bean named 'errorChannel' has been explicitly defined. Therefore, a default PublishSubscribeChannel will be created.
2022-09-04 10:09:41.683  INFO 67971 --- [           main] faultConfiguringBeanFactoryPostProcessor : No bean named 'integrationHeaderChannelRegistry' has been explicitly defined. Therefore, a default DefaultHeaderChannelRegistry will be created.
2022-09-04 10:09:41.855  INFO 67971 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.integration.config.IntegrationManagementConfiguration' of type [org.springframework.integration.config.IntegrationManagementConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2022-09-04 10:09:41.869  INFO 67971 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'integrationChannelResolver' of type [org.springframework.integration.support.channel.BeanFactoryChannelResolver] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2022-09-04 10:09:43.151  INFO 67971 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
2022-09-04 10:09:43.151  INFO 67971 --- [           main] o.s.i.channel.PublishSubscribeChannel    : Channel 'application.errorChannel' has 1 subscriber(s).
2022-09-04 10:09:43.151  INFO 67971 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : started bean '_org.springframework.integration.errorLogger'
2022-09-04 10:09:43.214  INFO 67971 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8999
2022-09-04 10:09:43.226  INFO 67971 --- [           main] d.datainmotion.airquality.AirQualityApp  : Started AirQualityApp in 2.662 seconds (JVM running for 3.089)
2022-09-04 10:09:44.366  INFO 67971 --- [           main] d.datainmotion.airquality.AirQualityApp  : O3=27 for NY New York City Region
2022-09-04 10:09:44.366  INFO 67971 --- [   scheduling-1] d.datainmotion.airquality.AirQualityApp  : O3=15 for CA San Rafael
2022-09-04 10:09:44.554  WARN 67971 --- [           main] c.s.circe.checksum.Crc32cIntChecksum     : Failed to load Circe JNI library. Falling back to Java based CRC32c provider
2022-09-04 10:09:44.567  INFO 67971 --- [           main] d.datainmotion.airquality.AirQualityApp  : Sent dev.datainmotion.airquality.model.Observation@657b3b[dateObserved=2022-09-04 ,hourObserved=9,localTimeZone=EST,reportingArea=New York City Region,stateCode=NY,latitude=40.8419,longitude=-73.8359,parameterName=O3,aqi=27,category=Category[number=1, name='Good', additionalProperties={}],additionalProperties={}]
2022-09-04 10:09:44.567  INFO 67971 --- [           main] d.datainmotion.airquality.AirQualityApp  : PM2.5=27 for NY New York City Region

````

### Flink

````
CREATE CATALOG pulsar WITH (
   'type' = 'pulsar',
   'service-url' = 'pulsar://pulsar1:6650',
   'admin-url' = 'http://pulsar1:8080',
   'format' = 'json'
);

USE CATALOG pulsar;

SHOW TABLES;

Flink SQL> describe airquality;
+----------------------+-----------------------------------------------------------------------------------------------+------+-----+--------+-----------+
|                 name |                                                                                          type | null | key | extras | watermark |
+----------------------+-----------------------------------------------------------------------------------------------+------+-----+--------+-----------+
| additionalProperties |                                                          MAP<STRING NOT NULL, ROW<> NOT NULL> | true |     |        |           |
|                  aqi |                                                                                           INT | true |     |        |           |
|             category | ROW<`additionalProperties` MAP<STRING NOT NULL, ROW<> NOT NULL>, `name` STRING, `number` INT> | true |     |        |           |
|         dateObserved |                                                                                        STRING | true |     |        |           |
|         hourObserved |                                                                                           INT | true |     |        |           |
|             latitude |                                                                                        DOUBLE | true |     |        |           |
|        localTimeZone |                                                                                        STRING | true |     |        |           |
|            longitude |                                                                                        DOUBLE | true |     |        |           |
|        parameterName |                                                                                        STRING | true |     |        |           |
|        reportingArea |                                                                                        STRING | true |     |        |           |
|            stateCode |                                                                                        STRING | true |     |        |           |
+----------------------+-----------------------------------------------------------------------------------------------+------+-----+--------+-----------+

select aqi, parameterName, dateObserved, hourObserved, latitude, longitude, localTimeZone, stateCode, reportingArea from airquality

select max(aqi) as MaxAQI, parameterName, reportingArea from airquality group by parameterName, reportingArea;

select max(aqi) as MaxAQI, min(aqi) as MinAQI, avg(aqi) as AvgAQI, count(aqi) as RowCount, parameterName, reportingArea from airquality group by parameterName, reportingArea;

````


### Command Line Consumer

````
----- got message -----
key:[2e725251-c977-46b4-ae81-45a675a1a473], properties:[], content:{"dateObserved":"2022-04-07 ","hourObserved":13,"localTimeZone":"EST","reportingArea":"Atlanta","stateCode":"GA","latitude":33.65,"longitude":-84.43,"parameterName":"O3","aqi":40,"category":{"number":1,"name":"Good","additionalProperties":{}},"additionalProperties":{}}
----- got message -----
key:[c0ec765d-38b9-4416-bb27-daa80e7654ff], properties:[], content:{"dateObserved":"2022-04-07 ","hourObserved":13,"localTimeZone":"EST","reportingArea":"Atlanta","stateCode":"GA","latitude":33.65,"longitude":-84.43,"parameterName":"PM2.5","aqi":18,"category":{"number":1,"name":"Good","additionalProperties":{}},"additionalProperties":{}}
----- got message -----
key:[7a7f567a-b9d7-470e-992e-86a2c24c9ce8], properties:[], content:{"dateObserved":"2022-04-07 ","hourObserved":13,"localTimeZone":"EST","reportingArea":"Atlanta","stateCode":"GA","latitude":33.65,"longitude":-84.43,"parameterName":"PM10","aqi":17,"category":{"number":1,"name":"Good","additionalProperties":{}},"additionalProperties":{}}

````

### Connecting to Data for Updates/Lookup - ScyllaDB

* https://github.com/tspannhw/airquality-datastore

### Resources

* https://docs.airnowapi.org/
* https://docs.airnowapi.org/webservices
* https://www.airnow.gov/
* https://community.cloudera.com/t5/Community-Articles/Tracking-Air-Quality-with-HDP-and-HDF-Part-1-Apache-NiFi/ta-p/248265


### Other Data Sources Available

* http://feeds.enviroflash.info/rss/forecast/479.xml
* http://feeds.enviroflash.info/

### Other Projects

* https://github.com/tspannhw/FLiPN-AirQuality-REST

### CVE Note

We are using Spring Boot 2.7.2 which does not have this issue.

https://thenewstack.io/springshell-brings-hell-to-java-developers/
