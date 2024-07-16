package com.taosdata.kafka.connect.source;

import com.taosdata.kafka.connect.config.ConnectionConfig;
import com.taosdata.kafka.connect.config.QueryIntervalValidator;
import com.taosdata.kafka.connect.config.TimestampInitialValidator;
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

    public static final String QUERY_INTERVAL_CONFIG = "query.interval.ms";
    public static final Long QUERY_INTERVAL_DEFAULT = 0L;
    public static final String QUERY_INTERVAL_DOC =
            "The interval used for query data from TDengine. If not specified or set to 0, all data will be retrieved.";
    public static final String QUERY_INTERVAL_DISPLAY = "query interval from TDengine, unit: millisecond";

    public static final String FETCH_MAX_ROWS_CONFIG = "fetch.max.rows";
    public static final int FETCH_MAX_ROWS_DEFAULT = 100;
    private static final String FETCH_MAX_ROWS_DOC =
            "Maximum number of rows to include in a single batch when polling for new data. This "
                    + "setting can be used to limit the amount of data buffered internally in the connector.";
    private static final String FETCH_MAX_ROWS_DISPLAY = "Max Rows Per Batch";

    public static final String TABLES_CONFIG = "tables";
    private static final String TABLES_DOC = "List of tables for this task to watch for changes.";

    public static final String TOPIC_PER_SUPER_TABLE = "topic.per.stable";
    private static final boolean TOPIC_PER_SUPER_TABLE_DEFAULT = true;
    private static final String TOPIC_PER_SUPER_TABLE_DOC = "Whether to create a topic for each super table, default is true";
    private static final String TOPIC_PER_SUPER_TABLE_DISPLAY = "Topic for each Super Table";

    private final int pollInterval;
    //    private boolean monitorTables;
    private final String topicPrefix;
    private final Timestamp timestampInitial;
    private final int fetchMaxRows;
    private long queryInterval = QUERY_INTERVAL_DEFAULT;  // default is null, which means query all data;
    private final List<String> tables;
    private final boolean topicPerSuperTable;

    public SourceConfig(Map<?, ?> props) {
        super(config(), props);
        this.pollInterval = this.getInt(POLL_INTERVAL_MS_CONFIG);
//        this.monitorTables = this.getBoolean(MONITOR_TABLES_CONFIG);
        this.topicPrefix = this.getString(TOPIC_PREFIX_CONFIG);
        String time = this.getString(TIMESTAMP_INITIAL_CONFIG);
        if (null != time && time.trim().length() > 0) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.timestampInitial = Timestamp.valueOf(LocalDateTime.parse(time, df));
        } else {
            this.timestampInitial = new Timestamp(0L);
        }
        this.queryInterval = this.getLong(QUERY_INTERVAL_CONFIG);
        this.fetchMaxRows = this.getInt(FETCH_MAX_ROWS_CONFIG);
        this.tables = this.getList(TABLES_CONFIG);
        this.topicPerSuperTable = this.getBoolean(TOPIC_PER_SUPER_TABLE);
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
                        TimestampInitialValidator.INSTANCE,
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
                        QUERY_INTERVAL_CONFIG,
                        ConfigDef.Type.LONG,
                        QUERY_INTERVAL_DEFAULT,
                        QueryIntervalValidator.INSTANCE,
                        ConfigDef.Importance.LOW,
                        QUERY_INTERVAL_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.MEDIUM,
                        QUERY_INTERVAL_DISPLAY)
                .define(
                        TOPIC_PER_SUPER_TABLE,
                        ConfigDef.Type.BOOLEAN,
                        TOPIC_PER_SUPER_TABLE_DEFAULT,
                        ConfigDef.Importance.LOW,
                        TOPIC_PER_SUPER_TABLE_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        TOPIC_PER_SUPER_TABLE_DISPLAY
                )
                .define(
                        TABLES_CONFIG,
                        ConfigDef.Type.LIST,
                        Collections.EMPTY_LIST,
                        ConfigDef.Importance.LOW,
                        TABLES_DOC)
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

    public long getQueryInterval() {
        return queryInterval;
    }

    public boolean isTopicPerSuperTable() {
        return topicPerSuperTable;
    }
}
