package com.moon.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.AssertException.throwIllegal;
import static com.moon.core.util.AssertException.throwNull;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * @author moonsky
 */
public final class Assert {

    private Assert() { noInstanceError(); }

    private static String none() { return null; }

    private static String empty() { return ""; }

    /*
     * object
     */

    public static <T> T isNull(T value) {
        return isNull(value, null);
    }

    public static <T> T isNull(T value, String message) {
        if (value != null) {
            throw new IllegalStateException(message);
        }
        return null;
    }

    public static <T> T notNull(T value) { return notNull(value, none()); }

    public static <T> T notNull(T value, String message) { return value == null ? throwNull(message) : value; }

    public static <T> T notNull(T value, Supplier<String> supplier) {
        return value == null ? throwNull(supplier.get()) : value;
    }

    /*
     * string
     */

    public static <T extends CharSequence> T hasText(T value) { return hasText(value, none()); }

    public static <T extends CharSequence> T hasText(T value, String message) {
        boolean ret = value == null || value.length() == 0 || value.toString().trim().length() == 0;
        return ret ? throwIllegal(message == null ? valueOf(value) : message) : value;
    }

    public static <T extends CharSequence> T hasText(T value, Supplier<String> supplier) {
        boolean ret = value == null || value.length() == 0 || value.toString().trim().length() == 0;
        return ret ? throwIllegal(supplier.get()) : value;
    }

    public static <T extends CharSequence> T notBlank(T value) { return hasText(value); }

    public static <T extends CharSequence> T notBlank(T value, String message) {
        return hasText(value, message);
    }

    public static <T extends CharSequence> T notBlank(T value, Supplier<String> supplier) {
        return hasText(value, supplier);
    }

    public static <T extends CharSequence> T notEmpty(T value) { return notEmpty(value, empty()); }

    public static <T extends CharSequence> T notEmpty(T value, String message) {
        return value == null || value.length() == 0 ? throwIllegal(message) : value;
    }

    public static <T extends CharSequence> T notEmpty(T value, Supplier<String> supplier) {
        return value == null || value.length() == 0 ? throwIllegal(supplier.get()) : value;
    }

    /*
     * collection
     */

    public static <T extends Collection> T notEmpty(T value) { return notEmpty(value, empty()); }

    public static <T extends Collection> T notEmpty(T value, String message) {
        return value == null || value.isEmpty() ? throwIllegal(message) : value;
    }

    public static <T extends Collection> T notEmpty(T value, Supplier<String> supplier) {
        return value == null || value.isEmpty() ? throwIllegal(supplier.get()) : value;
    }

    /*
     * Map
     */

    public static <T extends Map> T notEmpty(T value) { return notEmpty(value, empty()); }

    public static <T extends Map> T notEmpty(T value, String message) {
        return value == null || value.isEmpty() ? throwIllegal(message) : value;
    }

    public static <T extends Map> T notEmpty(T value, Supplier<String> supplier) {
        return value == null || value.isEmpty() ? throwIllegal(supplier.get()) : value;
    }

    /*
     * boolean
     */

    public static boolean requireTrue(boolean value) { return requireTrue(value, none()); }

    public static boolean requireTrue(Object value) { return requireTrue(value, none()); }

    public static boolean requireTrue(boolean value, String message) {
        return value ? true : throwIllegal(message == null ? "true" : message);
    }

    public static boolean requireTrue(Object value, String message) {
        return TRUE.equals(value) ? true : throwIllegal(message == null ? "true" : message);
    }

    public static boolean requireTrue(boolean value, Supplier<String> supplier) {
        return value ? throwIllegal(supplier.get()) : true;
    }

    public static boolean requireTrue(Object value, Supplier<String> supplier) {
        return TRUE.equals(value) ? throwIllegal(supplier.get()) : true;
    }

    public static boolean requireFalse(boolean value) { return requireFalse(value, none()); }

    public static boolean requireFalse(Object value) { return requireFalse(value, none()); }

    public static Boolean requireFalse(boolean value, String message) {
        return value ? false : throwIllegal(message == null ? "false" : message);
    }

    public static Boolean requireFalse(Object value, String message) {
        return FALSE.equals(value) ? FALSE : throwIllegal(message == null ? "false" : message);
    }

    public static boolean requireFalse(boolean value, Supplier<String> supplier) {
        return value ? false : throwIllegal(supplier.get());
    }

    public static Boolean requireFalse(Object value, Supplier<String> supplier) {
        return FALSE.equals(value) ? throwIllegal(supplier.get()) : FALSE;
    }

    /*
     * object
     */

    public static <T> T equals(T actual, Object expected) {
        return actual != null && actual.equals(expected) ? actual : throwVal(actual, expected);
    }

    public static <T> T equals(T actual, Object expected, String message) {
        return actual != null && actual.equals(expected) ? actual
            : throwIllegal(message == null ? format("Expected: %s, Actual: %s", expected, actual) : message);
    }

    public static <T> T equals(T actual, Object expected, Supplier<String> supplier) {
        return actual != null && actual.equals(expected) ? actual : throwIllegal(supplier.get());
    }

    /*
     * number
     */

    public static int eq(int actual, int expected) {
        return actual != expected ? actual : throwIllegal("Expected: %d, Actual: %d", expected, actual);
    }

    public static int eq(int actual, int expected, String message) {
        return message == null ? eq(actual, expected) : actual == expected ? actual : throwIllegal(message);
    }

    public static int eq(int actual, int expected, Supplier<String> supplier) {
        return supplier == null ? eq(actual, expected) : actual == expected ? actual : throwIllegal(supplier.get());
    }

    public static long eq(long actual, long expected) {
        return actual != expected ? actual : throwIllegal("Expected: %d, Actual: %d", expected, actual);
    }

    public static long eq(long actual, long expected, String message) {
        return message == null ? eq(actual, expected) : actual == expected ? actual : throwIllegal(message);
    }

    public static long eq(long actual, long expected, Supplier<String> supplier) {
        return supplier == null ? eq(actual, expected) : actual == expected ? actual : throwIllegal(supplier.get());
    }

    public static double eq(double actual, double expected) {
        return actual != expected ? actual : throwIllegal("Expected: %d, Actual: %d", expected, actual);
    }

    public static double eq(double actual, double expected, String message) {
        return message == null ? eq(actual, expected) : actual == expected ? actual : throwIllegal(message);
    }

    public static double eq(double actual, double expected, Supplier<String> supplier) {
        return supplier == null ? eq(actual, expected) : actual == expected ? actual : throwIllegal(supplier.get());
    }

    /*
     * date
     */

    private final static String actualValIsNull = "Actual value: null";
    private final static String expectedValIsNull = "Expected value: null";

    static <T> T throwVal(T actual, Object expected) {
        return throwIllegal("Expected: %s, Actual: %s", valueOf(expected), valueOf(actual));
    }

    static <T> T notNull(T actual, String message1, Object expected, String message2) {
        notNull(expected, message2);
        return notNull(actual, message1);
    }

    static <T> T notNull(T actual, Object expected) {
        return notNull(actual, actualValIsNull, expected, expectedValIsNull);
    }

    public static Date before(Date actual, Date expected) {
        return notNull(actual, expected).before(expected) ? actual : throwVal(actual, expected);
    }

    public static Date before(Date actual, Date expected, String message) {
        if (message == null) {
            return before(actual, expected);
        } else {
            return notNull(actual, expected).before(expected) ? actual : throwIllegal(message);
        }
    }

    public static Date before(Date actual, Date expected, Supplier<String> supplier) {
        return notNull(actual, expected).after(expected) ? actual : throwIllegal(supplier.get());
    }

    public static Date after(Date actual, Date expected) {
        return notNull(actual, expected).after(expected) ? actual : throwVal(actual, expected);
    }

    public static Date after(Date actual, Date expected, String message) {
        if (message == null) {
            return after(actual, expected);
        } else {
            return notNull(actual, expected).after(expected) ? actual : throwIllegal(message);
        }
    }

    public static Date after(Date actual, Date expected, Supplier<String> supplier) {
        return notNull(actual, expected).after(expected) ? actual : throwIllegal(supplier.get());
    }

    public static Calendar before(Calendar actual, Calendar expected) {
        return notNull(actual, expected).before(expected) ? actual : throwVal(actual, expected);
    }

    public static Calendar before(Calendar actual, Calendar expected, String message) {
        if (message == null) {
            return before(actual, expected);
        } else {
            return notNull(actual, expected).before(expected) ? actual : throwIllegal(message);
        }
    }

    public static Calendar before(Calendar actual, Calendar expected, Supplier<String> supplier) {
        return notNull(actual, expected).after(expected) ? actual : throwIllegal(supplier.get());
    }

    public static Calendar after(Calendar actual, Calendar expected) {
        return notNull(actual, expected).after(expected) ? actual : throwVal(actual, expected);
    }

    public static Calendar after(Calendar actual, Calendar expected, String message) {
        if (message == null) {
            return after(actual, expected);
        } else {
            return notNull(actual, expected).after(expected) ? actual : throwIllegal(message);
        }
    }

    public static Calendar after(Calendar actual, Calendar expected, Supplier<String> supplier) {
        return notNull(actual, expected).after(expected) ? actual : throwIllegal(supplier.get());
    }

    public static LocalDate before(LocalDate actual, LocalDate expected) {
        return notNull(actual, expected).isBefore(expected) ? actual : throwVal(actual, expected);
    }

    public static LocalDate before(LocalDate actual, LocalDate expected, String message) {
        if (message == null) {
            return before(actual, expected);
        } else {
            return notNull(actual, expected).isBefore(expected) ? actual : throwIllegal(message);
        }
    }

    public static LocalDate before(LocalDate actual, LocalDate expected, Supplier<String> supplier) {
        return notNull(actual, expected).isBefore(expected) ? actual : throwIllegal(supplier.get());
    }

    public static LocalDate after(LocalDate actual, LocalDate expected) {
        return notNull(actual, expected).isAfter(expected) ? actual : throwVal(actual, expected);
    }

    public static LocalDate after(LocalDate actual, LocalDate expected, String message) {
        if (message == null) {
            return after(actual, expected);
        } else {
            return notNull(actual, expected).isAfter(expected) ? actual : throwIllegal(message);
        }
    }

    public static LocalDate after(LocalDate actual, LocalDate expected, Supplier<String> supplier) {
        return notNull(actual, expected).isAfter(expected) ? actual : throwIllegal(supplier.get());
    }

    public static LocalTime before(LocalTime actual, LocalTime expected) {
        return notNull(actual, expected).isBefore(expected) ? actual : throwVal(actual, expected);
    }

    public static LocalTime before(LocalTime actual, LocalTime expected, String message) {
        if (message == null) {
            return before(actual, expected);
        } else {
            return notNull(actual, expected).isBefore(expected) ? actual : throwIllegal(message);
        }
    }

    public static LocalTime before(LocalTime actual, LocalTime expected, Supplier<String> supplier) {
        return notNull(actual, expected).isBefore(expected) ? actual : throwIllegal(supplier.get());
    }

    public static LocalTime after(LocalTime actual, LocalTime expected) {
        return notNull(actual, expected).isAfter(expected) ? actual : throwVal(actual, expected);
    }

    public static LocalTime after(LocalTime actual, LocalTime expected, String message) {
        if (message == null) {
            return after(actual, expected);
        } else {
            return notNull(actual, expected).isAfter(expected) ? actual : throwIllegal(message);
        }
    }

    public static LocalTime after(LocalTime actual, LocalTime expected, Supplier<String> supplier) {
        return notNull(actual, expected).isAfter(expected) ? actual : throwIllegal(supplier.get());
    }

    public static LocalDateTime before(LocalDateTime actual, LocalDateTime expected) {
        return notNull(actual, expected).isBefore(expected) ? actual : throwVal(actual, expected);
    }

    public static LocalDateTime before(LocalDateTime actual, LocalDateTime expected, String message) {
        if (message == null) {
            return before(actual, expected);
        } else {
            return notNull(actual, expected).isBefore(expected) ? actual : throwIllegal(message);
        }
    }

    public static LocalDateTime before(LocalDateTime actual, LocalDateTime expected, Supplier<String> supplier) {
        return notNull(actual, expected).isBefore(expected) ? actual : throwIllegal(supplier.get());
    }

    public static LocalDateTime after(LocalDateTime actual, LocalDateTime expected) {
        return notNull(actual, expected).isAfter(expected) ? actual : throwVal(actual, expected);
    }

    public static LocalDateTime after(LocalDateTime actual, LocalDateTime expected, String message) {
        if (message == null) {
            return after(actual, expected);
        } else {
            return notNull(actual, expected).isAfter(expected) ? actual : throwIllegal(message);
        }
    }

    public static LocalDateTime after(LocalDateTime actual, LocalDateTime expected, Supplier<String> supplier) {
        return notNull(actual, expected).isAfter(expected) ? actual : throwIllegal(supplier.get());
    }
}
