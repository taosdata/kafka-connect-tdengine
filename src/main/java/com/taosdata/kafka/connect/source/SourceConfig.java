package com.taosdata.kafka.connect.source;

import com.taosdata.kafka.connect.config.ConnectionConfig;
import com.taosdata.kafka.connect.config.OutFormatValidator;
import org.apache.kafka.common.config.ConfigDef;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Source properties
 */
public class SourceConfig extends ConnectionConfig {
    private static final String READ = "read";

    public static final String POLL_INTERVAL_MS_CONFIG = "poll.interval.ms";
    public static final int POLL_INTERVAL_MS_DEFAULT = 1000;
    private static final String POLL_INTERVAL_MS_DOC =
            "Frequency in ms to poll for new or removed tables, which may result in updated task "
                    + "configurations to start polling for data in added tables.";
    private static final String POLL_INTERVAL_MS_DISPLAY
            = "Monitoring Table Change Interval (ms)";

    private static final String MONITOR_TABLES_CONFIG = "monitor.tables";
    private static final boolean MONITOR_TABLES_DEFAULT = false;
    private static final String MONITOR_TABLES_DOC =
            "monitor new table create or delete, which will request Task Reconfiguration";
    private static final String MONITOR_TABLES_DISPLAY =
            "fetch tables ";

    public static final String TOPIC_PREFIX_CONFIG = "topic.prefix";
    private static final String TOPIC_PREFIX_DOC =
            "Prefix to generate the Kafka topic with table to publish data to";
    private static final String TOPIC_PREFIX_DISPLAY = "Topic Prefix";

    public static final String TIMESTAMP_INITIAL_CONFIG = "timestamp.initial";
    public static final Long TIMESTAMP_INITIAL_DEFAULT = null;
    public static final String TIMESTAMP_INITIAL_DOC =
            "The timestamp used for initial queries. If not specified, all data will be retrieved.";
    public static final String TIMESTAMP_INITIAL_DISPLAY = "Unix time value of initial timestamp";

    public static final String FETCH_MAX_ROWS_CONFIG = "fetch.max.rows";
    public static final int FETCH_MAX_ROWS_DEFAULT = 100;
    private static final String FETCH_MAX_ROWS_DOC =
            "Maximum number of rows to include in a single batch when polling for new data. This "
                    + "setting can be used to limit the amount of data buffered internally in the connector.";
    private static final String FETCH_MAX_ROWS_DISPLAY = "Max Rows Per Batch";

    public static final String TABLES_CONFIG = "tables";
    private static final String TABLES_DOC = "List of tables for this task to watch for changes.";

    private static final String OUT_FORMAT_CONFIG = "out.format";
    private static final String OUT_FORMAT_CONFIG_DEFAULT = "line";
    private static final String OUT_FORMAT_CONFIG_DOC = "out format for writing data to kafka";
    private static final String OUT_FORMAT_CONFIG_DISPLAY = "out format may be one of json or telnet";

    private final int pollInterval;
    //    private boolean monitorTables;
    private final String topicPrefix;
    private final Timestamp timestampInitial;
    private final int fetchMaxRows;
    private final List<String> tables;
    private final String outFormat;

    public SourceConfig(Map<?, ?> props) {
        super(config(), props);
        this.pollInterval = this.getInt(POLL_INTERVAL_MS_CONFIG);
//        this.monitorTables = this.getBoolean(MONITOR_TABLES_CONFIG);
        this.topicPrefix = this.getString(TOPIC_PREFIX_CONFIG).trim();
        String time = this.getString(TIMESTAMP_INITIAL_CONFIG);
        if (null != time && time.trim().length() > 0) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.timestampInitial = Timestamp.valueOf(LocalDateTime.parse(time, df));
        } else {
            this.timestampInitial = new Timestamp(0L);
        }
        this.fetchMaxRows = this.getInt(FETCH_MAX_ROWS_CONFIG);
        this.tables = this.getList(SourceConstants.CONFIG_TABLES);
        this.outFormat = this.getString(OUT_FORMAT_CONFIG).trim();
    }

    public static ConfigDef config() {
        int orderInGroup = 0;
        return ConnectionConfig.config()
                .define(
                        POLL_INTERVAL_MS_CONFIG,
                        ConfigDef.Type.INT,
                        POLL_INTERVAL_MS_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        POLL_INTERVAL_MS_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        POLL_INTERVAL_MS_DISPLAY
                )
//                .define(
//                        MONITOR_TABLES_CONFIG,
//                        ConfigDef.Type.BOOLEAN,
//                        MONITOR_TABLES_DEFAULT,
//                        ConfigDef.Importance.LOW,
//                        MONITOR_TABLES_DOC,
//                        READ,
//                        ++orderInGroup,
//                        ConfigDef.Width.SHORT,
//                        MONITOR_TABLES_DISPLAY
//                )
                .define(
                        TOPIC_PREFIX_CONFIG,
                        ConfigDef.Type.STRING,
                        ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH,
                        TOPIC_PREFIX_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.MEDIUM,
                        TOPIC_PREFIX_DISPLAY
                )
                .define(
                        TIMESTAMP_INITIAL_CONFIG,
                        ConfigDef.Type.STRING,
                        TIMESTAMP_INITIAL_DEFAULT,
                        ConfigDef.Importance.MEDIUM,
                        TIMESTAMP_INITIAL_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.MEDIUM,
                        TIMESTAMP_INITIAL_DISPLAY
                )
                .define(
                        FETCH_MAX_ROWS_CONFIG,
                        ConfigDef.Type.INT,
                        FETCH_MAX_ROWS_DEFAULT,
                        ConfigDef.Importance.LOW,
                        FETCH_MAX_ROWS_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        FETCH_MAX_ROWS_DISPLAY
                )
                .define(
                        TABLES_CONFIG,
                        ConfigDef.Type.LIST,
                        Collections.EMPTY_LIST,
                        ConfigDef.Importance.LOW,
                        TABLES_DOC)
                .define(
                        OUT_FORMAT_CONFIG,
                        ConfigDef.Type.STRING,
                        OUT_FORMAT_CONFIG_DEFAULT,
                        OutFormatValidator.INSTANCE,
                        ConfigDef.Importance.MEDIUM,
                        OUT_FORMAT_CONFIG_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        OUT_FORMAT_CONFIG_DISPLAY
                )
                ;
    }

    public int getPollInterval() {
        return pollInterval;
    }

//    public boolean isMonitorTables() {
//        return monitorTables;
//    }

    public int getFetchMaxRows() {
        return fetchMaxRows;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public Timestamp getTimestampInitial() {
        return timestampInitial;
    }

    public List<String> getTables() {
        return tables;
    }

    public String getOutFormat() {
        return outFormat.toLowerCase();
    }
}
