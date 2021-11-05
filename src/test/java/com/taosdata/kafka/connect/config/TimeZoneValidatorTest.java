package com.taosdata.kafka.connect.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @description:
 * @JDK: 1.8
 * @date 2021-11-04 17:09
 */
class TimeZoneValidatorTest {

    @Test
    void ensureValid() {
        TimeZoneValidator instance = TimeZoneValidator.INSTANCE;
        instance.ensureValid("timezone","US/Mountain");
    }
}