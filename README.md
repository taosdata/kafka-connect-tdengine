# TDengine Connector (Source and Sink) for Confluent Platform

[TDengine](https://www.taosdata.com/) is a highly efficient platform to store, query, and analyze time-series data. It is specially designed and optimized for IoT, Internet of Vehicles, Industrial IoT, IT Infrastructure and Application Monitoring, etc. It works like a relational database, such as MySQL.

[Apache Kafka](https://kafka.apache.org/) is an open-source distributed event streaming platform used by thousands of companies for high-performance data pipelines, streaming analytics, data integration, and mission-critical applications.

kafka-connect-tdengine is a [Kafka Connector](http://kafka.apache.org/documentation.html#connect) for real-time data synchronization from Kafka to TDengine

## Features

### At least once delivery

This connector guarantees that records from the Kafka topic are delivered at least once.

### Multiple Tasks

The connector supports running one or more tasks. You can specify the number of tasks in the tasks.max configuration parameter. This can lead to huge performance gains when a large amount of data need to be parsed.

### Multiple Format Support

This connector supports multiple input formats, such as [line](https://docs.influxdata.com/influxdb/v2.1/reference/syntax/line-protocol/),[telnet](http://opentsdb.net/docs/build/html/api_telnet/put.html),[json](http://opentsdb.net/docs/build/html/api_http/put.html). Users can explicitly define mappings for types in indices. When a mapping is not explicitly defined, TDengine can determine field types from data, however, some types such as timestamp and decimal, may not be correctly inferred. To ensure that the types are correctly inferred, the connector provides json format to infer a mapping from the schemas of Kafka messages.

## Build from Source

To build a development version you'll need a recent version of Kafka as well as a set of upstream Confluent projects, which you'll have to build from their appropriate snapshot branch.
See the [Confluent quickstart](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html) for guidance on this process.

1. Clone this Github repository:  
   `git clone git@github.com:taosdata/kafka-connect-tdengine.git`
2. You can build kafka-connect-tdengine with `Maven` using the standard lifecycle phases.  
   `mvn clean package`
3. A `.zip` file will be produced in the `/target/components/packages/` folder after the process has run.
4. Install the `.zip` into a directory specified in the `plugin.path` of your connect worker's configuration properties file. See the [Confluent instructions](https://docs.confluent.io/home/connect/install.html#install-connector-manually) for further information on this step.
5. [Configure](#configuration) the connector.

## Quick Start

- [sink](doc/sink.md#Start)
- [source](doc/source.md#Start)

## Configuration

- [sink](doc/sink.md#configuration)
- [source](doc/source.md#configuration)

## Contribute

- Source Code: https://github.com/taosdata/kafka-connect-tdengine
- Issue Tracker: https://github.com/taosdata/kafka-connect-tdengine/issues

## License

This project is licensed under the [GNU AFFERO GENERAL PUBLIC LICENSE](LICENSE).
