package com.taosdata.kafka.connect.source;

import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;
import com.taosdata.kafka.connect.config.*;
import com.taosdata.kafka.connect.enums.DataPrecision;
import com.taosdata.kafka.connect.enums.ReadMethodEnum;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

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
    public static final String OUT_FORMAT_JSON_NO_ARRAY = "out.format.json.no.array";
    private static final String OUT_FORMAT_JSON_NO_ARRAY_DOC = "out format json record without array";
    public static final String TOPIC_PER_SUPER_TABLE = "topic.per.stable";
    private static final boolean TOPIC_PER_SUPER_TABLE_DEFAULT = true;
    private static final String TOPIC_PER_SUPER_TABLE_DOC = "Whether to create a topic for each super table, default is true";
    private static final String TOPIC_PER_SUPER_TABLE_DISPLAY = "Topic for each Super Table";

    public static final String TOPIC_NAME_IGNORE_DB = "topic.ignore.db";
    private static final boolean TOPIC_NAME_IGNORE_DB_DEFAULT = false;
    private static final String TOPIC_NAME_IGNORE_DB_DOC = "Whether to ignore the database name when creating a topic, default is false. only valid when topic.per.stable is true";
    private static final String TOPIC_NAME_IGNORE_DB_DISPLAY = "Ignore Database Name";

    public static final String DATA_PRECISION = "data.precision";
    public static final String DATA_PRECISION_DEFAULT = "";
    private static final String DATA_PRECISION_DOC =
            "the precision of the schemaless data, this is valid only in line format";
    private static final String DATA_PRECISION_DISPLAY = "Data Precision";

    private static final String OUT_FORMAT_CONFIG = "out.format";
    private static final String OUT_FORMAT_CONFIG_DEFAULT = "line";
    private static final String OUT_FORMAT_CONFIG_DOC = "out format for writing data to kafka";
    private static final String OUT_FORMAT_CONFIG_DISPLAY = "out format may be one of json or line";

    public static final String TOPIC_DELIMITER = "topic.delimiter";
    private static final String TOPIC_DELIMITER_DEFAULT = "-";
    private static final String TOPIC_DELIMITER_DOC = "The delimiter for topic name, default is '-'";
    private static final String TOPIC_DELIMITER_DISPLAY = "Topic Delimiter";

    // query TDengine data method : subscription or query
    public static final String READ_METHOD = "read.method";
    private static final String READ_METHOD_DEFAULT = "subscription";
    private static final String READ_METHOD_DOC = "read method for query TDengine data, default is subscription";
    private static final String READ_METHOD_DISPLAY = "read method may be one of subscription or query";

    public static final String SUBSCRIPTION_GROUP_ID = "subscription.group.id";
    public static final String SUBSCRIPTION_GROUP_ID_DEFAULT = null;
    private static final String SUBSCRIPTION_GROUP_ID_DOC = "subscription group id for subscription data from TDengine";
    private static final String SUBSCRIPTION_GROUP_ID_DISPLAY = "subscription group id";

    public static final String SUBSCRIPTION_WAL_ONLY =  "subscription.wal.only";
    private static final boolean SUBSCRIPTION_WAL_ONLY_DEFAULT = true;
    private static final String SUBSCRIPTION_WAL_ONLY_DOC = "only subscription wal data from TDengine";
    private static final String SUBSCRIPTION_WAL_ONLY_DISPLAY = "only subscription wal data";

    // subscription from : latest or earliest
    public static final String SUBSCRIPTION_AUTO_OFFSET_RESET =  "subscription.from";
    private static final String SUBSCRIPTION_AUTO_OFFSET_RESET_DEFAULT = "latest";
    private static final String SUBSCRIPTION_AUTO_OFFSET_RESET_DOC = "subscription from latest or earliest";
    private static final String SUBSCRIPTION_AUTO_OFFSET_RESET_DISPLAY = "subscription from latest or earliest";

    private final int pollInterval;
    //    private boolean monitorTables;
    private final String topicPrefix;
    private final Timestamp timestampInitial;
    private final int fetchMaxRows;
    private final long queryInterval;  // default is null, which means query all data;
    private final List<String> tables;
    private final boolean topicPerSuperTable;
    private final boolean topicNameIgnoreDb;
    private final String outFormat;
    private final boolean outFormatJsonNoArray;
    private final String timestampType;
    private final String topicDelimiter;

    private final ReadMethodEnum readMethod;
    private final String subscriptionGroupId;
    private final boolean subscriptionWalOnly;
    private final String subscriptionAutoOffsetReset;

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
        this.topicNameIgnoreDb = this.getBoolean(TOPIC_NAME_IGNORE_DB);
        this.outFormat = this.getString(OUT_FORMAT_CONFIG).toLowerCase();
        this.outFormatJsonNoArray = this.getBoolean(OUT_FORMAT_JSON_NO_ARRAY);
        this.timestampType = getString(DATA_PRECISION).trim();
        this.topicDelimiter = this.getString(TOPIC_DELIMITER);
        this.subscriptionGroupId = this.getString(SUBSCRIPTION_GROUP_ID);

        if("subscription".equalsIgnoreCase(this.getString(READ_METHOD))){
            this.readMethod = ReadMethodEnum.SUBSCRIPTION;
            if (null == this.subscriptionGroupId || this.subscriptionGroupId.trim().length() == 0) {
                throw new ConfigException("subscription.group.id must be set when read.method is subscription");
            }
        }else {
            this.readMethod = ReadMethodEnum.QUERY;
        }

        this.subscriptionWalOnly = this.getBoolean(SUBSCRIPTION_WAL_ONLY);
        this.subscriptionAutoOffsetReset = this.getString(SUBSCRIPTION_AUTO_OFFSET_RESET);
    }

    public static ConfigDef config() {
        int orderInGroup = 0;
        return ConnectionConfig.config()
                .define(
                        DATA_PRECISION,
                        ConfigDef.Type.STRING,
                        DATA_PRECISION_DEFAULT,
                        PrecisionValidator.INSTANCE,
                        ConfigDef.Importance.MEDIUM,
                        DATA_PRECISION_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        DATA_PRECISION_DISPLAY
                )
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
                        TOPIC_NAME_IGNORE_DB,
                        ConfigDef.Type.BOOLEAN,
                        TOPIC_NAME_IGNORE_DB_DEFAULT,
                        ConfigDef.Importance.LOW,
                        TOPIC_NAME_IGNORE_DB_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        TOPIC_NAME_IGNORE_DB_DISPLAY
                )
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
                .define(
                        OUT_FORMAT_JSON_NO_ARRAY,
                        ConfigDef.Type.BOOLEAN,
                        true,
                        ConfigDef.Importance.LOW,
                        OUT_FORMAT_JSON_NO_ARRAY_DOC)
                .define(
                        TOPIC_DELIMITER,
                        ConfigDef.Type.STRING,
                        TOPIC_DELIMITER_DEFAULT,
                        ConfigDef.Importance.LOW,
                        TOPIC_DELIMITER_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        TOPIC_DELIMITER_DISPLAY
                )
                .define(
                        TABLES_CONFIG,
                        ConfigDef.Type.LIST,
                        Collections.emptyList(),
                        ConfigDef.Importance.LOW,
                        TABLES_DOC)
                .define(
                        READ_METHOD,
                        ConfigDef.Type.STRING,
                        READ_METHOD_DEFAULT,
                        SubscriptionValidator.INSTANCE,
                        ConfigDef.Importance.LOW,
                        READ_METHOD_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        READ_METHOD_DISPLAY
                )
                .define(
                        SUBSCRIPTION_GROUP_ID,
                        ConfigDef.Type.STRING,
                        SUBSCRIPTION_GROUP_ID_DEFAULT,
                        ConfigDef.Importance.LOW,
                        SUBSCRIPTION_GROUP_ID_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        SUBSCRIPTION_GROUP_ID_DISPLAY
                )
                .define(
                        SUBSCRIPTION_WAL_ONLY,
                        ConfigDef.Type.BOOLEAN,
                        SUBSCRIPTION_WAL_ONLY_DEFAULT,
                        ConfigDef.Importance.LOW,
                        SUBSCRIPTION_WAL_ONLY_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        SUBSCRIPTION_WAL_ONLY_DISPLAY
                )
                .define(
                        SUBSCRIPTION_AUTO_OFFSET_RESET,
                        ConfigDef.Type.STRING,
                        SUBSCRIPTION_AUTO_OFFSET_RESET_DEFAULT,
                        ConfigDef.Importance.LOW,
                        SUBSCRIPTION_AUTO_OFFSET_RESET_DOC,
                        READ,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        SUBSCRIPTION_AUTO_OFFSET_RESET_DISPLAY)
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

    public boolean isTopicNameIgnoreDb() {
        return topicNameIgnoreDb;
    }

    public String getOutFormat() {
        return outFormat;
    }

    public String getTopicDelimiter() {
        return topicDelimiter;
    }

    public ReadMethodEnum getReadMethod() {
        return readMethod;
    }

    public String getSubscriptionGroupId() {
        return subscriptionGroupId;
    }

    public boolean isSubscriptionWalOnly() {
        return subscriptionWalOnly;
    }

    public String getSubscriptionAutoOffsetReset() {
        return subscriptionAutoOffsetReset;
    }

    public String getTimestampType() {
        return timestampType;
    }

    public Boolean getOutFormatJsonNoArray() {
        return outFormatJsonNoArray;
    }
}
