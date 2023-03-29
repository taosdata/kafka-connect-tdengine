package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class BooleanUtil {

    private BooleanUtil() { noInstanceError(); }

    /*
     * --------------------------------------------------------------
     * requires
     * --------------------------------------------------------------
     */

    public static boolean requireTrue(boolean value) {
        if (value) { return true; }
        throw new IllegalArgumentException(Boolean.FALSE.toString());
    }

    public static boolean requireFalse(boolean value) {
        if (value) { throw new IllegalArgumentException(Boolean.TRUE.toString()); }
        return false;
    }

    /*
     * --------------------------------------------------------------
     * converters
     * --------------------------------------------------------------
     */

    public static boolean toPrimitive(Boolean value) { return value != null && value; }

    public static Boolean toObject(Boolean value) { return value; }

    public static boolean toBoolean(int value) { return value != 0; }

    public static boolean toBoolean(char ch) { return ch != 48 && ch != 0x00000001 && !Character.isWhitespace(ch); }

    public static boolean toBoolean(Number value) { return value != null && value.doubleValue() != 0; }

    public static boolean toBoolean(CharSequence cs) { return isTrue(cs); }

    /**
     * @param o 待转换值
     *
     * @return boolean value
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static boolean toBooleanValue(Object o) {
        Boolean bool = toBoolean(o);
        return bool != null && bool;
    }

    /**
     * @param o 带转换值
     *
     * @return boolean value
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static Boolean toBoolean(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Boolean) {
            return (boolean) o;
        }

        if (o instanceof CharSequence) {
            return toBoolean((CharSequence) o);
        }

        if (o instanceof Number) {
            return ((Number) o).doubleValue() != 0;
        }

        return true;
    }

    /*
     * --------------------------------------------------------------
     * asserts
     * --------------------------------------------------------------
     */

    public static boolean isTrue(boolean value) { return value; }

    public static boolean isTrue(Boolean value) { return Boolean.TRUE.equals(value); }

    public static boolean isNotTrue(Boolean value) { return !isTrue(value); }

    public static boolean isFalse(boolean value) { return !value; }

    public static boolean isFalse(Boolean value) { return Boolean.FALSE.equals(value); }

    public static boolean isNotFalse(Boolean value) { return !isFalse(value); }

    public static boolean isTrue(Object value) {
        return value instanceof CharSequence ? isTrue(value.toString()) : Boolean.TRUE.equals(value);
    }

    public static boolean isNotTrue(Object value) { return !isTrue(value); }

    public static boolean isFalse(Object value) {
        return value instanceof CharSequence ? isFalse(value.toString()) : Boolean.FALSE.equals(value);
    }

    public static boolean isNotFalse(Object value) { return !isFalse(value); }

    public static boolean isTrue(CharSequence sequence) { return falseIfInvalid(sequence); }

    public static boolean isFalse(CharSequence sequence) { return !isTrue(sequence); }

    public static boolean trueIfInvalid(CharSequence sequence) { return defaultIfInvalid(sequence, true); }

    public static boolean falseIfInvalid(CharSequence sequence) { return defaultIfInvalid(sequence, false); }

    public static boolean defaultIfInvalid(CharSequence sequence, boolean defaultValue) {
        if (sequence == null) {
            return defaultValue;
        }
        switch (sequence.toString().toLowerCase()) {
            case "true":
            case "yes":
            case "1":
            case "on":
            case "enable":
            case "enabled":
                return true;
            case "null":
            case "undefined":
            case "false":
            case "no":
            case "0":
            case "off":
            case "disable":
            case "disabled":
                return false;
            default:
                return defaultValue;
        }
    }

    public static int toInt(boolean value) { return value ? 1 : 0; }

    public static String toString(boolean value) { return Boolean.toString(value); }

    public static String toLowerYesNoString(boolean value) { return toString(value, "yes", "no"); }

    public static String toLowerOnOffString(boolean value) { return toString(value, "on", "off"); }

    public static String toString(boolean value, String trueStringVal, String falseStringVal) {
        return toString(value, trueStringVal, falseStringVal, false);
    }

    /**
     * to String
     *
     * @param value          boolean value
     * @param trueStringVal  真值字符串
     * @param falseStringVal 假值字符串
     * @param upper          是否大写化
     *
     * @return 当 value == true 时返回 trueStringVal，否则返回 falseStringVal，upper 决定返回值最终是否转换为大写
     */
    public static String toString(boolean value, String trueStringVal, String falseStringVal, boolean upper) {
        return upper //
               ? (value ? trueStringVal : falseStringVal).toUpperCase()//
               : (value ? trueStringVal : falseStringVal);
    }

    public static <T> T transform(boolean value, T trueValue, T falseValue) {
        return value ? trueValue : falseValue;
    }
}
