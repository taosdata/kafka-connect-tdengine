package com.moon.core.lang;

import com.moon.core.enums.Arrays2;

import static com.moon.core.lang.StringUtil.stringify;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.TestUtil.isDoubleValue;
import static java.lang.Float.parseFloat;
import static java.lang.Float.valueOf;

/**
 * @author moonsky
 */
public final class FloatUtil {

    private FloatUtil() {
        noInstanceError();
    }

    public static boolean isFloat(Object o) {
        return o != null && o.getClass() == Float.class;
    }

    public static boolean matchFloat(Object o) {
        return isDoubleValue(stringify(o));
    }

    public static Float toFloat(Boolean bool) {
        return bool == null ? null : (float) (bool ? 1 : 0);
    }

    public static Float toFloat(Character value) {
        return value == null ? null : valueOf(value);
    }

    public static Float toFloat(Byte value) {
        return value == null ? null : value.floatValue();
    }

    public static Float toFloat(Short value) {
        return value == null ? null : value.floatValue();
    }

    public static Float toFloat(Long value) {
        return value == null ? null : value.floatValue();
    }

    public static Float toFloat(Double value) {
        return value == null ? null : value.floatValue();
    }

    public static Float toFloat(CharSequence cs) {
        return cs == null ? null : parseFloat(cs.toString());
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
     * Float result = FloatUtil.toFloat(value);
     *
     * @param object
     *
     * @return
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toFloatValue(Object)
     */
    public static Float toFloat(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Float) {
            return (Float) object;
        }
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }
        if (object instanceof CharSequence) {
            return parseFloat(object.toString().trim());
        }
        if (object instanceof Boolean) {
            return (float) ((boolean) object ? 1 : 0);
        }
        try {
            return toFloat(ParseSupportUtil.unboxing(object));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Can not cast to int of: %s", object), e);
        }
    }

    /**
     * @param value
     *
     * @return
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toFloat(Object)
     */
    public static float toFloatValue(Object value) {
        Float result = toFloat(value);
        return result == null ? 0 : result;
    }

    public static float avg(float... values) {
        float ret = 0;
        int len = values.length;
        for (int i = 0; i < len; ret += values[i++]) {
        }
        return ret / len;
    }

    public static float sum(float... values) {
        float ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static float multiply(float... values) {
        float ret = 1;
        int le = values.length;
        for (int i = 0; i < le; i++) {
            ret *= values[i];
        }
        return ret;
    }

    public static float max(float... values) {
        int len = values.length;
        float ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Float min(float... values) {
        int len = values.length;
        float ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Float toObjectArr(float... values) {
        return Arrays2.FLOATS.toObjects(values);
    }

    public static float[] toPrimitiveArr(float defaultIfNull, Float... values) {
        int length = values == null ? 0 : values.length;
        if (length == 0) { return Arrays2.FLOATS.empty(); }
        float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = values[i] == null ? defaultIfNull : values[i];
        }
        return result;
    }
}
