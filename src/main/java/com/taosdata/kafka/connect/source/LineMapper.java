package com.taosdata.kafka.connect.source;

import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.enums.OutputFormatEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

/**
 *
 */
public class LineMapper extends TableMapper {
    private static final Logger log = LoggerFactory.getLogger(LineMapper.class);

    public LineMapper(String topic, String tableName, int batchMaxRows, Processor processor) throws SQLException {
        super(topic, tableName, batchMaxRows, processor, OutputFormatEnum.LINE);
    }

    @Override
    public PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition) {
        StringBuilder sb = new StringBuilder(tableName);
        Timestamp ts = null;
        try {
            if (tags.size() > 0) {
                for (String tag : tags) {
                    String value = columnType.get(tag);
                    switch (value) {
                        case "TIMESTAMP":
                        case "NCHAR":
                            sb.append(",").append(tag).append("=\"").append(resultSet.getString(tag)).append("\"");
                            break;
                        case "INT":
                        case "TINYINT":
                        case "SMALLINT":
                            sb.append(",").append(tag).append("=").append(resultSet.getInt(tag));
                            break;
                        case "BIGINT":
                            sb.append(",").append(tag).append("=").append(resultSet.getLong(tag));
                            break;
                        case "FLOAT":
                            sb.append(",").append(tag).append("=").append(resultSet.getFloat(tag));
                            break;
                        case "DOUBLE":
                            sb.append(",").append(tag).append("=").append(resultSet.getDouble(tag));
                            break;
                        case "BINARY":
                            sb.append(",").append(tag).append("=\"").append(resultSet.getString(tag)).append("\"");
                            break;
                        case "BOOL":
                            sb.append(",").append(tag).append("=").append(resultSet.getBoolean(tag));
                            break;
                        default:
                            throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                    + " with type " + value.getClass());
                    }
                }
            }

            sb.append(" ");
            StringBuilder columnString = new StringBuilder();
            for (String column : columns) {
                String value = columnType.get(column);
                switch (value) {
                    case "TIMESTAMP":
                        columnString.append(column).append("=").append(resultSet.getTimestamp(column)).append(",");
                        break;
                    case "NCHAR":
                        columnString.append(column).append("=L\"").append(resultSet.getString(column)).append("\",");
                        break;
                    case "INT":
                    case "TINYINT":
                    case "SMALLINT":
                        columnString.append(column).append("=").append(resultSet.getInt(column)).append("i32,");
                        break;
                    case "BIGINT":
                        columnString.append(column).append("=").append(resultSet.getLong(column)).append("i64,");
                        break;
                    case "FLOAT":
                        columnString.append(column).append("=").append(resultSet.getFloat(column)).append("f32,");
                        break;
                    case "DOUBLE":
                        columnString.append(column).append("=").append(resultSet.getDouble(column)).append("f64,");
                        break;
                    case "BINARY":
                        columnString.append(column).append("=\"").append(resultSet.getString(column)).append("\",");
                        break;
                    case "BOOL":
                        columnString.append(column).append("=").append(resultSet.getBoolean(column)).append(",");
                        break;
                    default:
                        throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                + " with type " + value.getClass());
                }
            }
            String s = columnString.toString();
            sb.append(s, 0, s.length() - 1);
            sb.append(" ").append(resultSet.getTimestamp(1).getTime()).append(String.format("%06d", resultSet.getTimestamp(1).getNanos() % 1000000));
            ts = resultSet.getTimestamp(1);
        } catch (SQLException e) {
            log.error("resultSet get value error", e);
        }

        return new PendingRecord(partition, ts, topic, null, sb.toString());
    }
}
