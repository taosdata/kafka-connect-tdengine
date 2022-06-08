package com.taosdata.kafka.connect.util;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class VersionUtilsTest {

    @Test
    public void getVersion() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("version.txt")))) {
            assertEquals(reader.readLine(), VersionUtils.getVersion());
        }
    }
}