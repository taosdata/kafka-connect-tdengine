package com.taosdata.kafka.connect.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.util.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
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

    protected final List<String> columns = Lists.newArrayList();
    protected final List<String> tags = Lists.newArrayList();
    protected final Map<String, String> columnType = Maps.newHashMap();

    PreparedStatement preparedStatement;

    public TableMapper(String topic, String tableName, int batchMaxRows, Processor processor) throws SQLException {
        this.topic = topic;
        this.tableName = tableName;
        this.batchMaxRows = batchMaxRows;
        this.connection = processor.getConnection();
        preparedStatement = getOrCreatePreparedStatement();
    }

    public PreparedStatement getOrCreatePreparedStatement() {
        if (preparedStatement != null) {
            return preparedStatement;
        }

        ResultSet resultSet = null;
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQLUtils.describeTableSql(tableName));
            resultSet = statement.getResultSet();
            resultSet.next();
            String timestampColumn = resultSet.getString(1);
            columnType.put(timestampColumn, resultSet.getString(2));
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                columnType.put(name, resultSet.getString(2));
                if (SourceConstants.TABLE_TAG.equals(resultSet.getString(4))) {
                    tags.add(name);
                } else {
                    columns.add(name);
                }
            }
            StringBuilder queryString = new StringBuilder();
            queryString.append("select *");
            if (!tags.isEmpty()) {
                queryString.append(",").append(String.join(",", tags));
            }
            queryString.append(" from ").append(tableName);
            queryString.append(String.format(" where %s > ? and %s <= ?", timestampColumn, timestampColumn));
            preparedStatement = connection.prepareStatement(queryString.toString());
            if (batchMaxRows > 0) {
                preparedStatement.setFetchSize(batchMaxRows);
            }
        } catch (SQLException e) {
            log.error("init table mapper failed", e);
        } finally {
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log.error("can't close resultSet", e);
                }
            }
        }
        return preparedStatement;
    }

    public abstract PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition) ;

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
    }

    // may be use in custom query
    private String appendWhere(String query, String startTimestamp, String endTimestamp) {
        List<String> split = Arrays.asList(query.split("(?i:\\s+where\\s+)"));
        String appendedQuery = split.get(0) + " where time > '" + startTimestamp + "' and time <= '" + endTimestamp + "'";
        if (split.size() > 1) {
            appendedQuery = appendedQuery + " and " + split.get(1);
        }

        return appendedQuery;
    }
}
