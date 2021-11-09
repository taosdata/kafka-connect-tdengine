TDengine is a highly efficient platform to store, query, and analyze time-series data. It is specially designed and optimized for IoT, Internet of Vehicles, Industrial IoT, IT Infrastructure and Application Monitoring, etc. It works like a relational database, such as MySQL.

Users can explicitly define mappings for types in indices. When a mapping is not explicitly defined, TDengine can determine field types from data, however, some types such as timestamp and decimal, may not be correctly inferred.
To ensure that the types are correctly inferred, the connector provides three input formats(line/telnet/json) to infer a mapping from the schemas of Kafka messages.

# Kafka Connect tdengine Connector
kafka-connect-tdengine is a [Kafka Connector](http://kafka.apache.org/documentation.html#connect) Kafka Connector for copying data between Kafka and TDengine

# Development
To build a development version you'll need a recent version of Kafka as well as a set of upstream Confluent projects, which you'll have to build from their appropriate snapshot branch. 
See the [Confluent quickstart](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html) for guidance on this process.
1. Clone this Github repository:  
`git clone git@github.com:taosdata/kafka-connect-tdengine.git`
2. You can build kafka-connect-tdengine with Maven using the standard lifecycle phases.  
`mvn clean package`
3. A `.zip` file will be produced in the `/target/components/packages/` folder after the process has run.
4. Install the `.zip` into a directory specified in the `plugin.path` of your connect worker's configuration properties file. See the [Confluent instructions](https://docs.confluent.io/home/connect/install.html#install-connector-manually) for further information on this step.
5. [Configure](#configuration) the connector.

# Quick Start
In this quick start, you copy data from a single Kafka topic to a database on a local tdengine database.
This example assumes you are running Kafka and Schema Registry locally on the default ports. It also assumes your have TDengine installed and running.

1. Start the Confluent Platform using the Confluent CLI command below.
```text
confluent local services connect start
```

2. create a configuration file for the connector. This configuration is used typically with standalone workers. This file is included with the connector in `./etc/sink-quickstart.properties`, and contains the following settings:
```
name=tdengine-sink
connector.class=com.taosdata.kafka.connect.sink.TDengineSinkConnector
tasks.max=1
topics=schemaless
connection.url=jdbc:TAOS://127.0.0.1:6030
connection.user=root
connection.password=taosdata
connection.database=sink
db.schemaless=line
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=org.apache.kafka.connect.storage.StringConverter
```
The `connection.url`,`connection.database` specify the connection URL,database name of the TDengine server. 
By default the `connection.user`,`connection.password` are 'root' and 'taosdata'.

3. Run the connector with this configuration
```text
confluent local services connect connector load TDengineSinkConnector --config etc/sink-quickstart.properties
```
or  
2. REST-based example
this configuration is used typically with distributed workers. Write the following JSON to tdengine-sink-connector.json, configure all of the required values, 
and use the command below to post the configuration to one of the distributed connect worker.

```json
{
  "name": "TDengineSinkConnector",
  "config": {
    "connector.class": "com.taosdata.kafka.connect.sink.TDengineSinkConnector",
    "tasks.max": "1",
    "topics": "schemaless",
    "connection.url": "jdbc:TAOS://127.0.0.1:6030",
    "connection.user": "root",
    "connection.password": "taosdata",
    "connection.database": "sink",
    "db.schemaless": "line",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.storage.StringConverter"
  }
}
```

3. Run the connector with this configuration
```text
curl -X POST -d @tdengine-sink-connector.json http://localhost:8083/connectors -H "Content-Type: application/json"
```

4. create a record in the `schemaless` topic:
```text
bin/kafka-console-producer --broker-list localhost:9092 --topic schemaless
```
The console producer is waiting for input, Copy and paste the following record into the terminal:
```text
st,t1=3i64,t2=4f64,t3="t3" c1=3i64,c3=L"passit",c2=false,c4=4f64 1626006833639000000
```
or use 
```text
echo "st,t1=3i64,t2=4f64,t3=\"t3\" c1=3i64,c3=L\"passit\",c2=false,c4=4f64 1626006833639000000" | confluent local services kafka produce schemaless
```

5. login taos shell
```text
taos
```
6. run the following query to verify the records:
```text
taos> use sink;
Query OK, 0 of 0 row(s) in database (0.002036s)

taos> show stables;
              name              |      created_time       | columns |  tags  |   tables    |
============================================================================================
 st                             | 2021-11-08 09:51:47.252 |       5 |      3 |           1 |
Query OK, 1 row(s) in set (0.002243s)

taos> show tables;
           table_name           |      created_time       | columns |          stable_name           |          uid          |     tid     |    vgId     |
==========================================================================================================================================================
 t_1931d87b0c76e62aa8c5dfa22... | 2021-11-08 09:51:47.256 |       5 | st                             |       844424946914309 |           1 |           3 |
Query OK, 1 row(s) in set (0.003301s)
```

# configuration
`connector.class`
To use this connector, specify the name of the connector class in the `connector.class` configuration property
```text
connector.class=com.taosdata.kafka.connect.sink.TDengineSinkConnector
```

`tasks.max`
The TDengine Sink connector supports running one or more tasks. You can specify the number of tasks in the `tasks.max` configuration parameter. This can lead to huge performance gains when multiple files need to be parsed.



`topics`
topics are the categories used to organize messages.

### Connection
`connection.url`  
TDengine JDBC connection URL.
For example:`jdbc:TAOS://127.0.0.1:6030`
* Type:string
* importance:high
* Default:null

`connection.user`  
TDengine JDBC connection user.
* Type:string
* Importance:high
* Default:root

`connection.password`  
TDengine JDBC conneciton password.
* Type:string
* Importance:high
* Default:taosdata

`connection.database`  
The TDengine database name in which connector will write from kafka topic.
* Type:string
* Importance:high
* Default:null

`connection.attempts`  
Maximum number of attempts to retrieve a valid TDengine JDBC connection. Must be a positive integer.
* Type:int
* Importance:low
* Default:3

`connection.backoff.ms`  
Backoff time in milliseconds between connection attempts.
* Type:long
* Importance:low
* Default:5000

### Write
`batch.size`  
Maximum number of rows to include in a single batch write
* Type:int
* Importance:medium
* Default:100

`max.retries`  
The maximum number of times retry on errors before falling
* type:int
* Importance:medium
* Default:1

`retry.backoff.ms`  
Frequency in ms to wait before retrying
* Type:int
* Importance:medium
* Default:3000

`db.schemaless`
the format to write data to tdengine, one of line,telnet,json
* Type:string
* Importance:high
* Default:null

# Contribute

- Source Code: https://github.com/taosdata/kafka-connect-tdengine
- Issue Tracker: https://github.com/taosdata/kafka-connect-tdengine/issues

# License

This project is licensed under the [GNU AFFERO GENERAL PUBLIC LICENSE](LICENSE).