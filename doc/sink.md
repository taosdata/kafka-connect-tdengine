# Sink

In this quick start, you copy data from a single Kafka topic to a database on a local TDengine database.

## Start

This example assumes you are running Confluent version 7.1.1 locally on the default ports. It also assumes your have TDengine installed and running.

1. Start the Confluent Platform using the Confluent CLI command below.

   ```shell
   confluent local services start
   ```

2. create a configuration file for the connector. This configuration is used typically with standalone workers. This file is included with the connector in `config/sink-quickstart.properties`, and contains the following settings:

   ```properties
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

   The `connection.url`,`connection.database` specify the connection URL,database name of the TDengine server. By default the `connection.user`,`connection.password` are `root` and `taosdata`.

3. Run the connector with this configuration:

   ```shell
   confluent local services connect connector load TDengineSinkConnector --config sink-quickstart.properties
   ```

4. create a record in the schemaless topic:

   ``` shell
   bin/kafka-console-producer --broker-list localhost:9092 --topic schemaless
   ```

   The console producer is waiting for input, copy and paste the following record into the terminal:

   ```shell
   st,t1=3i64,t2=4f64,t3="t3" c1=3i64,c3=L"passit",c2=false,c4=4f64 1626006833639000000
   ```

   or use：

   ``` shell
   echo "st,t1=3i64,t2=4f64,t3=\"t3\" c1=3i64,c3=L\"passit\",c2=false,c4=4f64 1626006833639000000" | confluent local services kafka produce schemaless
   ```

5. open the taos shell

   ```shell
   taos
   ```
  
6. run the following query to verify the records:

   ```shell
   taos> use sink;
   Query OK, 0 of 0 row(s) in database (0.000386s)

   taos> show stables;
   name                 |      created_time       | columns |  tags  |   tables    |
   =================================================================================================
   st                                  | 2021-11-09 14:48:13.164 |       5 |      3 |           1 |
   Query OK, 1 row(s) in set (0.001102s)

   taos> show tables;
   table_name              |      created_time       | columns |             stable_name             |          uid          |     tid     |    vgId     |
   ====================================================================================================================================================================
   t_1931d87b0c76e62aa8c5dfa2287dfddb  | 2021-11-09 14:48:13.169 |       5 | st                                  |       844424946914325 |           1 |           3 |
   Query OK, 1 row(s) in set (0.003321s)

   taos> select * from t_1931d87b0c76e62aa8c5dfa2287dfddb;
   _ts           |          c1           |            c3            |  c2   |            c4             |
   =================================================================================================================
   2021-07-11 20:33:53.639 |                     3 | passit                   | false |               4.000000000 |
   Query OK, 1 row(s) in set (0.003365s)
   ```

## REST-based example

this configuration is used typically with [distributed workers](https://docs.confluent.io/platform/current/connect/concepts.html#distributed-workers). Write the following JSON to `tdengine-sink-connector.json`, configure all of the required values, and use the command below to post the configuration to one of the distributed connect worker.

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

Run the connector with this configuration

```shell
curl -X POST -d @tdengine-sink-connector.json http://localhost:8083/connectors -H "Content-Type:application/json"
```

## Configuration

### `connector.class`

To use this connector, specify the name of the connector class in the `connector.class` configuration property, for example: `connector.class=com.taosdata.kafka.connect.sink.TDengineSinkConnector`

- Type: string
- Importance: high
- Default: null

### `tasks.max`

The connector supports running one or more tasks.

- Type: int
- Importance: high
- Default: 1

### `topics`

kafka topics are the categories used to organize messages. Multiple topics are separated by ",".

- Type: string
- Importance: high
- Default: null

### `connection.url`

TDengine JDBC connection URL. For example: `connection.url=jdbc:TAOS://127.0.0.1:6030`

- Type: string
- importance: high
- Default: null

### `connection.user`

TDengine JDBC connection user.

- Type: string
- Importance: high
- Default: root

### `connection.password`

TDengine JDBC connection password.

- Type: string
- Importance: high
- Default: taosdata

### `connection.database`

The TDengine database name which connector will write from kafka topic.

- Type: string
- Importance: high
- Default: null

### `connection.attempts`

Maximum number of attempts to retrieve a valid TDengine JDBC connection. Must be a positive integer.

- Type: int
- Importance: low
- Default: 3

### `connection.backoff.ms`

Backoff time in milliseconds between connection attempts.

- Type: long
- Importance: low
- Default: 5000

### `connection.database.prefix`

when connection.database is not specify, a string for the destination database name. which may contain {topic} as a placeholder for the originating topic name. for example, 'kafka_{topic}' for the topic 'orders' will map to the database name 'kafka_orders'. the default value is null, this means the topic will be mapped to the new database which will have same name as the topic

- Type: String
- Importance: medium
- Default: null

### `batch.size`

Specifies how many records to attempt to batch together for insertion into the destination table.

- Type: int
- Importance: medium
- Default: 100

### `max.retries`

The maximum number of times retry on errors before falling the task.

- type: int
- Importance: medium
- Default: 1

### `retry.backoff.ms`

The time in milliseconds to wait following an error before a retry attempt is made.

- Type: int
- Importance: medium
- Default: 3000

### `db.schemaless`

the format to write data to tdengine, one of line,telnet,json.

- Type: string
- Importance: high
- Default: null

### `data.precision`

the precision of the schemaless data, one of ms, us, ns. this is valid only when `db.schemaless` is line format.

- Type: string
- Importance: medium
- Default: ns
