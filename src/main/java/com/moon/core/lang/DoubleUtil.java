package com.moon.core.lang;

import com.moon.core.enums.Arrays2;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.TestUtil.isDoubleValue;
import static java.lang.String.format;

/**
 * @author moonsky
 */
public final class DoubleUtil {

    private DoubleUtil() { noInstanceError(); }

    public static boolean isDouble(Object obj) { return obj != null && obj.getClass() == Double.class; }

    public static boolean matchDouble(CharSequence obj) { return isDoubleValue(String.valueOf(obj)); }

    public static Double toDouble(Byte value) { return value == null ? null : value.doubleValue(); }

    public static Double toDouble(Short value) { return value == null ? null : value.doubleValue(); }

    public static Double toDouble(Integer value) { return value == null ? null : value.doubleValue(); }

    public static Double toDouble(Long value) { return value == null ? null : value.doubleValue(); }

    public static Double toDouble(Float value) { return value == null ? null : value.doubleValue(); }

    public static Double toDouble(Boolean value) { return value == null ? null : (value ? 1D : 0D); }

    public static Double toDouble(Character value) { return value == null ? null : (double) value; }

    /**
     * 目前基本数据 Util 内类似的方法均使用了<strong>极大的容忍度</strong>
     * * 对于普通的转换均能得到预期结果；
     * 对于复杂对象（数组或集合，但不包括自定义对象）的转换需要熟悉方法内部逻辑；
     * * 如果对象 o 是一个集合或数组，当 o 只有一项时，返回这一项并且深度递归
     * * 否则返回这个集合或数组的尺寸（size 或 length）
     * <p>
     * Object value = null;  // ================================ null
     * boolean value = true;  // =============================== 1
     * boolean value = false;  // ============================== 0
     * char value = 'a';  // =================================== 97
     * byte value = 1;  // ===================================== 1
     * int value = 1;  // ====================================== 1
     * short value = 1;  // ==================================== 1
     * long value = 1L;  // ==================================== 1
     * float value = 1F;  // =================================== 1
     * double value = 1F;  // ================================== 1
     * String value = "1";  // ================================= 1
     * StringBuffer value = new StringBuffer("1");  // ========= 1
     * StringBuilder value = new StringBuilder("1");  // ======= 1
     * String value = "  1   ";  // ============================ 1
     * StringBuffer value = new StringBuffer("  1   ");  // ==== 1
     * StringBuilder value = new StringBuilder("  1   ");  // == 1
     * BigDecimal value = new BigDecimal("1");  // ============= 1
     * BigInteger value = new BigInteger("1");  // ============= 1
     * Collection value = new ArrayList(){{put(1)}};  // ======= 1（只有一项时）
     * Collection value = new HashSet(){{put(1)}};  // ========= 1（只有一项时）
     * Collection value = new TreeSet(){{put(1)}};  // ========= 1（只有一项时）
     * Collection value = new LinkedList(){{put(1)}};  // ====== 1（只有一项时）
     * Map value = new HashMap(){{put("key", 1)}};  // ========= 1（只有一项时）
     * <p>
     * int[] value = {1, 2, 3, 4};  // ======================================== 4（大于一项时，返回 size）
     * String[] value = {"1", "1", "1", "1"};  // ============================= 4（大于一项时，返回 size）
     * Collection value = new ArrayList(){{put(1);put(1);put(1);}};  // ======= 3（大于一项时，返回 size）
     * Map value = new HashMap(){{put("key", 1);put("name", 2);}};  // ======== 2（大于一项时，返回 size）
     * <p>
     * Double result = DoubleUtil.toDouble(value);
     *
     * @param value
     *
     * @return
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toDoubleValue(Object)
     */
    public static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof CharSequence) {
            return Double.parseDouble(value.toString().trim());
        }
        if (value instanceof Boolean) {
            return (double) ((Boolean) value ? 1 : 0);
        }
        try {
            return toDouble(ParseSupportUtil.unboxing(value));
        } catch (Exception e) {
            throw new IllegalArgumentException(format("Can not cast to double of: %s", value), e);
        }
    }

    /**
     * @param value
     *
     * @return
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toDouble(Object)
     */
    public static double toDoubleValue(Object value) {
        Double result = toDouble(value);
        return result == null ? 0 : result;
    }

    public static double max(double... values) {
        int len = values.length;
        double ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static double min(double... values) {
        int len = values.length;
        double ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static double avg(double... values) {
        int len = values.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += values[i];
        }
        return sum / len;
    }

    public static double sum(double... values) {
        int len = values.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += values[i];
        }
        return sum;
    }

    public static double multiply(double... values) {
        int len = values.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum *= values[i];
        }
        return sum;
    }

    public static Double[] toObjectArr(float... values) {
        return Arrays2.DOUBLES.toObjects(values);
    }

    public static double[] toPrimitiveArr(double defaultIfNull, Double... values) {
        int length = values == null ? 0 : values.length;
        if (length == 0) { return Arrays2.DOUBLES.empty(); }
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = values[i] == null ? defaultIfNull : values[i];
        }
        return result;
    }
}
