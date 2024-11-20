package com.taosdata.kafka.connect.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taosdata.jdbc.TaosGlobalConfig;
import com.taosdata.jdbc.tmq.ConsumerRecords;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.enums.OutputFormatEnum;
import com.taosdata.kafka.connect.util.SQLUtils;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * tableName timestampColumn columns tags
 */
public abstract class TableMapper {
    private static final Logger log = LoggerFactory.getLogger(TableMapper.class);

    protected final String tableName;
    private final int batchMaxRows;
    private final Connection connection;
    protected final String topic;

    protected List<String> columns = Lists.newArrayList();
    protected List<String> tags = Lists.newArrayList();
    protected SchemaBuilder tagBuilder = SchemaBuilder.struct();
    protected Schema valueSchema;
    protected String timestampColumn;
    //    protected Map<String, Schema> valueBuilder = Maps.newHashMap();
    protected Map<String, String> columnType = Maps.newHashMap();
    private final OutputFormatEnum format;

    PreparedStatement preparedStatement;

    public TableMapper(String topic, String tableName, int batchMaxRows, Processor processor, OutputFormatEnum format) throws SQLException {
        this.topic = topic;
        this.tableName = tableName;
        this.batchMaxRows = batchMaxRows;
        this.connection = processor.getConnection();
        this.format = format;
        preparedStatement = getOrCreatePreparedStatement();
    }

    public PreparedStatement getOrCreatePreparedStatement() {
        if (preparedStatement != null) {
            return preparedStatement;
        }
        getMetaSchema();
        try {
            StringBuilder sb = new StringBuilder().append("select _c0,");
            if (!tags.isEmpty()) {
                sb.append("`").append(String.join("`,`", tags)).append("`");
                sb.append(", ");
            }
            for (int i = 0; i < columns.size(); i++) {
                sb.append("`").append(columns.get(i)).append("`");
                if (i != columns.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(" from `").append(tableName).append("` where _c0 > ? and _c0 <= ? order by _c0 asc , `").append(columns.get(0)).append("`");
            log.debug("execute query sql: {}", sb);
            preparedStatement = connection.prepareStatement(sb.toString());
//                    "select * from `" + tableName + "` where _c0 > ? and _c0 <= ? order by _c0 asc and " + columns.get(0));
            if (batchMaxRows > 0) {
                preparedStatement.setFetchSize(batchMaxRows);
            }
        } catch (SQLException e) {
            log.error("init table mapper failed", e);
        }
        return preparedStatement;
    }

    public void getMetaSchema() {
        if (!columns.isEmpty()) {
            return;
        }
        ResultSet resultSet = null;
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(SQLUtils.describeTableSql(tableName));
            resultSet.next();
            timestampColumn = resultSet.getString(1);
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                columnType.put(name, resultSet.getString(2));
                if (SourceConstants.TABLE_TAG.equals(resultSet.getString(4))) {
                    tags.add(name);
                } else {
                    columns.add(name);
                }
            }
            if (format == OutputFormatEnum.JSON) {
                for (String tag : tags) {
                    tagBuilder.field(tag, convertType(columnType.get(tag)));
                }
                SchemaBuilder sb = SchemaBuilder.struct();
                sb.field(timestampColumn, SchemaBuilder.int64().build());
                for (String column : columns) {
                    sb.field(column, convertType(columnType.get(column)));
                }
                if (!tags.isEmpty()) {
                    sb.field("tags", tagBuilder.optional().build());
                }
                valueSchema = sb.build();
            }
        } catch (SQLException e) {
            log.error("get table {} meta failed", tableName, e);
        } finally {
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log.error("can't close resultSet", e);
                }
            }
        }
    }

    public abstract PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition);

    public abstract List<SourceRecord> process(List<ConsumerRecords<Map<String, Object>>> records
            , Map<String, String> partition, TimeStampOffset offset);

    public void closeStatement() {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException ignored) {
                // intentionally ignored
                log.warn("closeStatement error: ", ignored);
            }
        }
        preparedStatement = null;
        columns = Lists.newArrayList();
        tags = Lists.newArrayList();
        columnType = Maps.newHashMap();
        tagBuilder = SchemaBuilder.struct();
    }

    private Schema convertType(String type) {
        switch (type) {
            case "TINYINT":
                return Schema.OPTIONAL_INT8_SCHEMA;
            case "SMALLINT":
                return Schema.OPTIONAL_INT16_SCHEMA;
            case "INT":
                return Schema.OPTIONAL_INT32_SCHEMA;
            case "TIMESTAMP":
            case "BIGINT":
            case "BIGINT UNSIGNED":
                return Schema.OPTIONAL_INT64_SCHEMA;
            case "FLOAT":
                return Schema.OPTIONAL_FLOAT32_SCHEMA;
            case "DOUBLE":
                return Schema.OPTIONAL_FLOAT64_SCHEMA;
            case "BOOL":
                return Schema.OPTIONAL_BOOLEAN_SCHEMA;
            case "NCHAR":
            case "JSON":
            case "BINARY":
            case "VARCHAR":
                return Schema.OPTIONAL_STRING_SCHEMA;
            default:
                return null;
        }
    }

    protected Object getValue(Object value, String type) throws RuntimeException {
        switch (type) {
            case "BINARY":
            case "VARCHAR":
                if (value instanceof byte[]) {
                    String charset = TaosGlobalConfig.getCharset();
                    try {
                        return new String((byte[]) value, charset);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            default:
                return value;
        }
    }
}
