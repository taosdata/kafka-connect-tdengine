package com.taosdata.kafka.connect.source;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * test source module
 */
public class SourceInsertTest {
    private String host = "127.0.0.1";
    private String dbname = "source";
    private Connection conn;

    @Test
    public void prepareData(){
        createConnection();
    }

    private void createConnection(){
        final String url = "jdbc:TAOS://" + host + ":6030/?user=root&password=taosdata";
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute("drop database if exists " + dbname);
            stmt.execute("create database if not exists " + dbname + " precision 'ns'");
            stmt.execute("use " + dbname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
