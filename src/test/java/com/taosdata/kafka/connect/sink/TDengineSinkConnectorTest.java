package com.taosdata.kafka.connect.sink;

import org.apache.kafka.common.config.Config;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TDengineSinkConnectorTest {

    @Test
    public void testValidationConfig() {
        TDengineSinkConnector connector = new TDengineSinkConnector();
        Map<String, String> configMap = new HashMap<>();
        configMap.put("tasks.max", "1");
        configMap.put("topics", "schemaless");
        configMap.put("connection.url", "jdbc:ABC://127.0.0.1:6030");
        configMap.put("connection.user", "root");
        configMap.put("connection.password", "taosdata");
        configMap.put("connection.database", "sink");
        configMap.put("connection.attempts", "3");
        configMap.put("connection.backoff.ms", "5000");
        configMap.put("connection.database.prefix", "kafka_");
        configMap.put("max.retries", "3");
        configMap.put("retry.backoff.ms", "3000");
        configMap.put("batch.size", "1000");
        configMap.put("db.charset", "UTF-7");
        configMap.put("db.timeunit", "mill");
        configMap.put("db.schemaless", "abc");
        configMap.put("data.precision", "as");
        connector.start(configMap);
        List<String> list = configErrors(connector.validate(configMap));
        assertTrue(list.contains("Invalid value UTF-7 for configuration db.charset: Charset is invalid."));
        assertTrue(list.contains("Invalid value jdbc:ABC://127.0.0.1:6030 for configuration connection.url: check connection.url is correct"));
        assertTrue(list.contains("Invalid value abc for configuration db.schemaless: schemaless config must be one of (line, telnet, json)"));
        assertTrue(list.contains("Invalid value as for configuration data.precision: database precision config must be one of (ms, us, ns)"));
    }

    private List<String> configErrors(Config config) {
        return config.configValues()
                .stream()
                .flatMap(cfg -> cfg.errorMessages().stream())
                .collect(Collectors.toList());
    }

}