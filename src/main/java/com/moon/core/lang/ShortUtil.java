package com.moon.core.lang;

import com.moon.core.enums.Arrays2;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.TestUtil.isIntegerValue;

/**
 * @author moonsky
 */
public final class ShortUtil {

    private ShortUtil() {
        noInstanceError();
    }

    public static boolean isFloat(Object obj) {
        return obj != null && obj.getClass() == Short.class;
    }

    public static boolean matchFloat(Object obj) {
        return isIntegerValue(String.valueOf(obj));
    }

    public static short toShortValue(double value) {
        return (short) value;
    }

    public static short toShortValue(float value) {
        return (short) value;
    }

    public static short toShortValue(long value) {
        return (short) value;
    }

    public static short toShortValue(int value) {
        return (short) value;
    }

    public static short toShortValue(char value) {
        return (short) value;
    }

    public static short toShortValue(boolean value) {
        return (short) (value ? 1 : 0);
    }

    public static Short toShort(Byte value) {
        return value == null ? null : value.shortValue();
    }

    public static Short toShort(Integer value) {
        return value == null ? null : value.shortValue();
    }

    public static Short toShort(Long value) {
        return value == null ? null : value.shortValue();
    }

    public static Short toShort(Float value) {
        return value == null ? null : value.shortValue();
    }

    public static Short toShort(Boolean value) {
        return value == null ? null : Short.valueOf(String.valueOf(value ? 1 : 0));
    }

    public static Short toShort(Character value) {
        return value == null ? null : Short.valueOf(value.toString());
    }

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
     * Short result = ShortUtil.toShort(value);
     *
     * @param value 带转换值
     *
     * @return 转换后的值
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static Short toShort(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Short) {
            return (Short) value;
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        if (value instanceof CharSequence) {
            return Short.parseShort(value.toString().trim());
        }
        if (value instanceof Boolean) {
            return Short.valueOf(String.valueOf((Boolean) value ? 1 : 0));
        }
        try {
            return toShort(ParseSupportUtil.unboxing(value));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Can not cast to short of: %s", value), e);
        }
    }

    /**
     * @param value 带转换的值
     *
     * @return 转换成功后的值
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toShort(Object)
     */
    public static short toShortValue(Object value) {
        Short result = toShort(value);
        return result == null ? 0 : result;
    }

    public static short max(short... values) {
        int len = values.length;
        short ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static short min(short... values) {
        int len = values.length;
        short ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static int avg(short... doubles) {
        int len = doubles.length;
        short sum = 0;
        for (int i = 0; i < len; i++) {
            sum += doubles[i];
        }
        return (sum / len);
    }

    public static short sum(short... doubles) {
        int len = doubles.length;
        short sum = 0;
        for (int i = 0; i < len; i++) {
            sum += doubles[i];
        }
        return sum;
    }

    public static short multiply(short... doubles) {
        int len = doubles.length;
        short sum = 0;
        for (int i = 0; i < len; i++) {
            sum *= doubles[i];
        }
        return sum;
    }

    public static Short toObjectArr(short... values) {
        return Arrays2.FLOATS.toObjects(values);
    }

    public static short[] toPrimitiveArr(short defaultIfNull, Short... values) {
        int length = values == null ? 0 : values.length;
        if (length == 0) { return Arrays2.FLOATS.empty(); }
        short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            result[i] = values[i] == null ? defaultIfNull : values[i];
        }
        return result;
    }
}
