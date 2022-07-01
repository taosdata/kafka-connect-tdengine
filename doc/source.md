# Source

The Kafka Connect TDengine Source connector is used to move creations in a TDengine database to Apache Kafka® topics in
real-time. Data is loaded by periodically executing query, the connector loads only new records. By default, all super
tables in a database are copied, each to its own output topic.

## Start

This example assumes you are running Confluent version 7.1.1 locally on the default ports. It also assumes your have
TDengine installed and running.

### Start TDengine Source Connector

1. Start the Confluent Platform using the Confluent CLI command below.

   ```shell
   confluent local services start
   ```

2. create a configuration file for the connector. This configuration is used typically with standalone workers. This
   file is included with the connector in `config/source-quickstart.properties`, and contains the following settings:

   ```properties
   name=tdengine-source
   connector.class=com.taosdata.kafka.connect.source.TDengineSourceConnector
   tasks.max=1
   connection.url=jdbc:TAOS://127.0.0.1:6030
   connection.username=root
   connection.password=taosdata
   connection.database=source
   connection.attempts=3
   connection.backoff.ms=5000
   
   poll.interval.ms=1000
   topic.prefix=tdengine-
   fetch.max.rows=100
   out.format=line
   key.converter=org.apache.kafka.connect.storage.StringConverter
   value.converter=org.apache.kafka.connect.storage.StringConverter

   ```

   The `connection.url`,`connection.database` specify the connection URL,database name of the TDengine server. By
   default the `connection.user`,`connection.password` are `root` and `taosdata`.

3. Run the connector with this configuration:

   ```shell
   confluent local services connect connector load sourceConnector --config source-quickstart.properties
   ```

### Create TDengine Database and Load Data

4. open the taos shell and

   ```shell
   taos
   ```

5. create a database in TDengine. create a super table and seed it with some data:

    ```shell
    create database source precision 'ns';
    use source;
    create table st (ts timestamp , value int) tags (tg nchar(30));
    insert into t1 using st tags('Los Angeles') values(now, 100);
    insert into t2 using st tags('chicago') values(now, 200);
    ```

6. To check that it has copied the data that was present when you started Kafka Connect, start a console consumer,
   reading from the beginning of the topic:

   ```shell
   $ kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic tdengine-source
   
   ## Your output should resemble:
   st,tg=L"Los Angeles" value=100i32 1656591975567764000
   st,tg=L"chicago" value=200i32 1656592200810039000
   ```

## REST-based example

this configuration is used typically with [distributed workers](https://docs.confluent.io/platform/current/connect/concepts.html#distributed-workers). Write the following JSON to `tdengine-source-connector.json`, configure all of the required values, and use the command below to post the configuration to one of the distributed connect worker.

```json
 {
  "name": "TDengineSourceConnector",
  "config": {
    "connector.class": "com.taosdata.kafka.connect.source.TDengineSourceConnector",
    "tasks.max": 1,
    "connection.url": "jdbc:TAOS://127.0.0.1:6030",
    "connection.username": "root",
    "connection.password": "taosdata",
    "connection.database": "source",
    "connection.attempts": 3,
    "connection.backoff.ms": 1000,
    "topic.prefix": "tdengine-",
    "poll.interval.ms": 1000,
    "fetch.max.rows": 100,
    "out.format": "line",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.storage.StringConverter"
  }
}
```

Run the connector with this configuration

```shell
curl -X POST -d @tdengine-source-connector.json http://localhost:8083/connectors -H "Content-Type:application/json"
```

## Configuration

### connector.class

To use this connector, specify the name of the connector class in the `connector.class` configuration property.

```shell
connector.class=com.taosdata.kafka.connect.source.TDengineSourceConnector
```

- Type:string
- Importance:high
- Default:null

### tasks.max

The connector can be configured to use as few as one task (tasks.max=1) or scale to as many tasks as required to capture all table changes.

- Type:int
- Importance:high
- Default:1

### connection.url

The URL of the TDengine database to write to.

- Type:string
- Importance:high
- Default:null

### connection.username

The username to connect to TDengine with. default value is `root`.

- Type:string
- Importance:high
- Default:null

### connection.password

The password to connect to TDengine with. default value is `taosdata`.

- Type:string
- Importance:high
- Default:null

### connection.database

The TDengine database name from which records have to be read and publish data to configured Apache Kafka® topic.

- Type:string
- Importance:high
- Default:null

### connection.attempts

The maximum number of times to retry on errors before failing the connection.

- Type:int
- Importance:high
- Default:3

### connection.backoff.ms

Backoff time duration to wait before retrying connection (in milliseconds).

- Type:int
- Importance:high
- Default:5000

### topic.prefix

Prefix that should be prepended to super table names to generate the name of Apache Kafka® topic to publish to.

- Type:string
- Importance:high
- Default: ""

### timestamp.initial

The timestamp used for initial queries. If not specified, all data will be retrieved. format is `yyyy-MM-dd HH:mm:ss` 

- Type:string
- Importance:low
- Default: "1970-01-01 00:00:00"

### poll.interval.ms

Frequency in ms to poll for new or removed tables, which may result in updated task configurations to start polling for data in added tables. (in milliseconds)

- Type:int
- Importance:medium
- Default:1000

### fetch.max.rows

Maximum number of rows to include in a single batch when polling for new data. This setting can be used to limit the amount of data buffered internally in the connector.

- Type:int
- Importance:low
- Default:100

### out.format

out format for writing data to kafka, may be one of json or telnet currently. default is `line`

- Type:string
- Importance:high
- Default:line
