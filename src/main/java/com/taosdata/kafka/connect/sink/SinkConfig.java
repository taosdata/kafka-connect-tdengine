package com.taosdata.kafka.connect.sink;

import com.taosdata.kafka.connect.config.CharsetValidator;
import com.taosdata.kafka.connect.config.ConnectionConfig;
import com.taosdata.kafka.connect.config.SchemaValidator;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * all sink task need config
 */
public class SinkConfig extends ConnectionConfig {
    private static final Logger log = LoggerFactory.getLogger(SinkConfig.class);

    private static final String WRITES_GROUP = "Writes";

    public static final String MAX_RETRIES = "max.retries";
    public static final int MAX_RETRIES_DEFAULT = 3;
    private static final String MAX_RETRIES_DOC =
            "The maximum number of times to retry on errors before failing the task.";
    private static final String MAX_RETRIES_DISPLAY = "Maximum Retries";

    public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
    public static final int RETRY_BACKOFF_MS_DEFAULT = 3000;
    private static final String RETRY_BACKOFF_MS_DOC =
            "The time in milliseconds to wait following an error before a retry attempt is made.";
    private static final String RETRY_BACKOFF_MS_DISPLAY = "Retry Backoff (millis)";

    public static final String BATCH_SIZE = "batch.size";
    public static final int BATCH_SIZE_DEFAULT = 3000;
    private static final String BATCH_SIZE_DOC =
            "Specifies how many records to attempt to batch together for insertion into the destination"
                    + " table, when possible.";
    private static final String BATCH_SIZE_DISPLAY = "Batch Size";

    public static final String CHARSET_CONF = "db.charset";
    public static final String CHARSET_DOC = "The character set to use for String key and values.";

    public static final String CONNECTION_PREFIX_CONFIG = CONNECTION_PREFIX + "database.prefix";
    public static final String CONNECTION_PREFIX_CONFIG_DEFAULT = "";
    private static final String CONNECTION_PREFIX_DOC = "when connection.database is not specified, a string for the destination database name" +
            "which may contain '${topic}' as a placeholder for the originating topic name" +
            "for example, kafka_${topic} for the topic 'orders' will map to the database name 'kafka_orders'." +
            "the default value is null, " +
            "this means the topic will be mapped to the new database which will have same name as the topic";
    private static final String CONNECTION_PREFIX_DISPLAY = "JDBC sink destination Database prefix";

    public static final String SCHEMA_LOCATION = "schema.location";
    private static final String SCHEMA_LOCATION_DOC =
            "Specifies the absolute path to the record schema file";
    private static final String SCHEMA_LOCATION_DISPLAY = "Schema location";

    private final int maxRetries;
    private final long retryBackoffMs;
    private final int batchSize;
    private final String charset;
    private final String connectionDatabasePrefix;
    private final String schemaLocation;

    public SinkConfig(Map<?, ?> originals) {
        super(config(), originals);
        this.maxRetries = getInt(MAX_RETRIES);
        this.retryBackoffMs = getInt(RETRY_BACKOFF_MS);
        this.batchSize = getInt(BATCH_SIZE);
        this.charset = getString(CHARSET_CONF);
        this.connectionDatabasePrefix = getString(CONNECTION_PREFIX_CONFIG).trim();
        this.schemaLocation = getString(SCHEMA_LOCATION).trim();
    }

    public static ConfigDef config() {
        int orderInGroup = 0;
        return ConnectionConfig.config()
                .define(
                        BATCH_SIZE,
                        ConfigDef.Type.INT,
                        BATCH_SIZE_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        BATCH_SIZE_DOC,
                        WRITES_GROUP,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        BATCH_SIZE_DISPLAY
                )
                .define(
                        MAX_RETRIES,
                        ConfigDef.Type.INT,
                        MAX_RETRIES_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        MAX_RETRIES_DOC,
                        WRITES_GROUP,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        MAX_RETRIES_DISPLAY
                )
                .define(
                        RETRY_BACKOFF_MS,
                        ConfigDef.Type.INT,
                        RETRY_BACKOFF_MS_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        RETRY_BACKOFF_MS_DOC,
                        WRITES_GROUP,
                        ++orderInGroup,
                        ConfigDef.Width.SHORT,
                        RETRY_BACKOFF_MS_DISPLAY
                )
                .define(
                        CONNECTION_PREFIX_CONFIG,
                        ConfigDef.Type.STRING,
                        CONNECTION_PREFIX_CONFIG_DEFAULT,
                        ConfigDef.Importance.MEDIUM,
                        CONNECTION_PREFIX_DOC,
                        WRITES_GROUP,
                        ++orderInGroup,
                        ConfigDef.Width.MEDIUM,
                        CONNECTION_PREFIX_DISPLAY
                )
                .define(
                        CHARSET_CONF,
                        ConfigDef.Type.STRING,
                        "UTF-8",
                        CharsetValidator.INSTANCE,
                        ConfigDef.Importance.LOW,
                        CHARSET_DOC
                )
                .define(
                        SCHEMA_LOCATION,
                        ConfigDef.Type.STRING,
                        ConfigDef.NO_DEFAULT_VALUE,
                        SchemaValidator.INSTANCE,
                        ConfigDef.Importance.HIGH,
                        SCHEMA_LOCATION_DOC,
                        WRITES_GROUP,
                        ++orderInGroup,
                        ConfigDef.Width.LONG,
                        SCHEMA_LOCATION_DISPLAY
                )
                ;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isSingleDatabase() {
        return !"".equals(getConnectionDb());
    }

    public String getConnectionDatabasePrefix() {
        return connectionDatabasePrefix;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }
}
