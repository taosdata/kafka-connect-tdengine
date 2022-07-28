package com.taosdata.kafka.connect;

import com.taosdata.kafka.connect.config.ConnectionConfig;
import com.taosdata.kafka.connect.sink.TDengineSinkTask;
import com.taosdata.kafka.connect.source.SourceConfig;
import com.taosdata.kafka.connect.source.SourceConstants;
import com.taosdata.kafka.connect.source.TDengineSourceConnector;
import com.taosdata.kafka.connect.source.TDengineSourceTask;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTaskContext;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TDengineTaskTest {
//    Logger log = LoggerFactory.getLogger(TDengineTaskTest.class);

    private static Map<String, String> configMap = new HashMap<>();
    private static Connection connection;
    private static Statement statement;
    private String topic = "topic";

    @Test
    void sinkTest() throws SQLException {
        TDengineSinkTask task = new TDengineSinkTask();
        SinkTaskContext context = getSinkTaskContext();
        task.initialize(context);
        configMap.put("connector.class", "com.taosdata.kafka.connect.sink.TDengineSinkConnector");
        task.start(configMap);

        String line = "st,t1=3i64,t2=4f64,t3=\"t3\" c1=3i64,c3=L\"passit\",c2=false,c4=4f64 1626006833639000000";
        SinkRecord record = new SinkRecord(topic, 1, null, "key", null, line, 0);
        List<SinkRecord> records = Collections.singletonList(record);
        task.put(records);
        task.stop();

        statement.executeUpdate("use " + configMap.get(ConnectionConfig.CONNECTION_DB));
        ResultSet resultSet = statement.executeQuery("select * from " + line.substring(0, line.indexOf(",")));
        resultSet.next();
        assertEquals(1626006833639000000L, resultSet.getLong(1));
    }

    @Test
    public void sourceTest() throws InterruptedException {
        configMap.put("connector.class", "com.taosdata.kafka.connect.source.TDengineSourceConnector");

//        configMap.put("value.converter", "org.apache.kafka.connect.json.JsonConverter");
//        configMap.put("value.converter.schemas.enable", "false");

        TDengineSourceConnector connector = new TDengineSourceConnector();
        connector.start(configMap);
        connector.taskConfigs(1).stream().findFirst()
                .ifPresent(m -> configMap.put(SourceConfig.TABLES_CONFIG, m.get(SourceConfig.TABLES_CONFIG)));

        SourceTaskContext context = new SourceTaskContext() {

            @Override
            public Map<String, String> configs() {
                return null;
            }

            @Override
            public OffsetStorageReader offsetStorageReader() {
                return new OffsetStorageReader() {
                    @Override
                    public <T> Map<String, Object> offset(Map<String, T> partition) {
                        return new HashMap<String, Object>() {{
                            put(SourceConstants.TIMESTAMP_MILLISECOND, 1554660260231L);
                        }};
                    }

                    @Override
                    public <T> Map<Map<String, T>, Map<String, Object>> offsets(Collection<Map<String, T>> partitions) {
                        return null;
                    }
                };
            }
        };

        TDengineSourceTask task = new TDengineSourceTask();
        task.initialize(context);
        task.start(configMap);
        String result = "st,t1=L\"3i64\",t2=L\"4f64\",t3=L\"\"t3\"\" c1=3i64,c3=L\"passit\",c2=false,c4=4.0f64 1626006833639000000";
        task.poll().stream().findFirst().ifPresent(e -> assertEquals(result, e.value()));
        task.stop();
    }

    @BeforeAll
    public static void before() throws SQLException {
        configMap.put("tasks.max", "1");
        configMap.put("topics", "schemaless");

        configMap.put("connection.url", "jdbc:TAOS://127.0.0.1:6030");
        configMap.put("connection.user", "root");
        configMap.put("connection.password", "taosdata");
        configMap.put("connection.database", "sink");
        configMap.put("connection.attempts", "3");
        configMap.put("connection.backoff.ms", "5000");
        // sink
        configMap.put("data.precision", "ns");
        configMap.put("batch.size", "1000");
        configMap.put("max.retries", "3");
        configMap.put("retry.backoff.ms", "3000");
        configMap.put("db.schemaless", "line");

        configMap.put("connection.database.prefix", "kafka_");
        configMap.put("db.charset", "UTF-8");
        // source
        configMap.put("poll.interval.ms", "1000");
        configMap.put("topic.prefix", "");
        configMap.put("timestamp.initial", "2022-01-01 00:00:00");
        configMap.put("fetch.max.rows", "10");

        connection = DriverManager.getConnection(configMap.get(ConnectionConfig.CONNECTION_URL_CONFIG), configMap.get(ConnectionConfig.CONNECTION_USER), configMap.get(ConnectionConfig.CONNECTION_PASSWORD));
        statement = connection.createStatement();
        statement.executeUpdate("drop database if exists " + configMap.get(ConnectionConfig.CONNECTION_DB));
    }

    @AfterAll
    public static void after() {
        try {
            if (connection != null) {
                if (statement != null) {
                    statement.executeUpdate("drop database if exists " + configMap.get(ConnectionConfig.CONNECTION_DB));
                    statement.close();
                }
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SinkTaskContext getSinkTaskContext() {
        return new SinkTaskContext() {
            @Override
            public Map<String, String> configs() {
                return null;
            }

            @Override
            public void offset(Map<TopicPartition, Long> offsets) {

            }

            @Override
            public void offset(TopicPartition tp, long offset) {

            }

            @Override
            public void timeout(long timeoutMs) {

            }

            @Override
            public Set<TopicPartition> assignment() {
                return null;
            }

            @Override
            public void pause(TopicPartition... partitions) {

            }

            @Override
            public void resume(TopicPartition... partitions) {

            }

            @Override
            public void requestCommit() {

            }
        };
    }
}