package com.moon.core.math;

import com.moon.core.lang.NumberUtil;
import com.moon.core.lang.ParseSupportUtil;
import com.moon.core.lang.ThrowUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.moon.core.math.NumFns.*;
import static com.moon.core.lang.ThrowUtil.runtime;

/**
 * @author moonsky
 */
public final class BigDecimalUtil extends NumberUtil {

    private BigDecimalUtil() { ThrowUtil.noInstanceError(); }

    public static BigDecimal valueOf(String value) { return new BigDecimal(value); }

    public static BigDecimal valueOf(int value) {
        return value == 0 ? BigDecimal.ZERO : (value == 1 ? BigDecimal.ONE : BigDecimal.valueOf(value));
    }

    public static BigDecimal valueOf(long value) {
        return value == 0 ? BigDecimal.ZERO : (value == 1 ? BigDecimal.ONE : BigDecimal.valueOf(value));
    }

    public static BigDecimal valueOf(double value) {
        return value == 0 ? BigDecimal.ZERO : (value == 1 ? BigDecimal.ONE : BigDecimal.valueOf(value));
    }

    public static BigDecimal defaultIfInvalid(String numeric, BigDecimal defaultValue) {
        try {
            return valueOf(numeric);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    public static BigDecimal nullIfInvalid(String numeric) { return defaultIfInvalid(numeric, null); }

    public static BigDecimal zeroIfInvalid(String numeric) { return defaultIfInvalid(numeric, BigDecimal.ZERO); }

    public static BigDecimal oneIfInvalid(String numeric) { return defaultIfInvalid(numeric, BigDecimal.ONE); }

    public static BigDecimal zeroIfNull(BigDecimal number) { return number == null ? BigDecimal.ZERO : number; }

    public static BigDecimal oneIfNull(BigDecimal number) { return number == null ? BigDecimal.ONE : number; }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof BigInteger) {
            return new BigDecimal(value.toString());
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value instanceof CharSequence) {
            return valueOf(value.toString());
        }
        try {
            return toBigDecimal(ParseSupportUtil.unboxing(value));
        } catch (Exception e) {
            return runtime(e, String.format("Can not cast to BigDecimal of: %s", value));
        }
    }

    public static boolean gt(BigDecimal value1, BigDecimal value2) {
        return GT.compare(value1, value2);
    }

    public static boolean lt(BigDecimal value1, BigDecimal value2) {
        return LT.compare(value1, value2);
    }

    public static boolean ge(BigDecimal value1, BigDecimal value2) {
        return GE.compare(value1, value2);
    }

    public static boolean le(BigDecimal value1, BigDecimal value2) {
        return LE.compare(value1, value2);
    }

    public static boolean eq(BigDecimal value1, BigDecimal value2) {
        return EQ.compare(value1, value2);
    }

    public static boolean ne(BigDecimal value1, BigDecimal value2) {
        return NE.compare(value1, value2);
    }
}
