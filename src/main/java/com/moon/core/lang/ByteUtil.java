package com.moon.core.lang;

import com.moon.core.util.TestUtil;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ByteUtil {

    private ByteUtil() {
        noInstanceError();
    }


    public static boolean isByte(Object o) {
        return o != null && o.getClass() == Byte.class;
    }

    public static boolean matchNumber(Object o) {
        return TestUtil.isGeneralNumber(String.valueOf(o));
    }

    public static Byte toByte(Boolean bool) {
        return bool == null ? null : Byte.valueOf(String.valueOf(bool ? 1 : 0));
    }

    public static Byte toByte(Character value) {
        return value == null ? null : Byte.valueOf(value.toString());
    }

    public static Byte toByte(Integer value) {
        return value == null ? null : value.byteValue();
    }

    public static Byte toByte(Short value) {
        return value == null ? null : value.byteValue();
    }

    public static Byte toByte(Long value) {
        return value == null ? null : value.byteValue();
    }

    public static Byte toByte(Float value) {
        return value == null ? null : value.byteValue();
    }

    public static Byte toByte(Double value) {
        return value == null ? null : value.byteValue();
    }

    public static Byte toByte(CharSequence cs) {
        return cs == null ? null : Byte.valueOf(cs.toString());
    }

    /**
     * 目前基本数据 Util 内类似的方法均使用了<strong>极大的容忍度</strong>
     * * 对于普通的转换均能得到预期结果；
     * 对于复杂对象（数组或集合，但不包括自定义对象）的转换需要熟悉方法内部逻辑；
     * * 如果对象 o 是一个集合或数组，当 o 只有一项时，返回这一项并且深度递归
     * * 否则返回这个集合或数组的尺寸（size 或 length）
     * <p>
     * Object value = null;  // =============================== null
     * boolean value = true;  // ============================== 1
     * boolean value = false;  // ============================= 0
     * char value = 'a';  // ================================== 97
     * byte value = 1;  // ==================================== 1
     * int value = 1;  // ===================================== 1
     * short value = 1;  // =================================== 1
     * long value = 1L;  // =================================== 1
     * float value = 1F;  // ================================== 1
     * double value = 1F;  // ================================= 1
     * String value = "1";  // ================================ 1
     * StringBuffer value = new StringBuffer("1");  // ======== 1
     * StringBuilder value = new StringBuilder("1");  // ====== 1
     * String value = "  1   ";  // =========================== 1
     * StringBuffer value = new StringBuffer("  1   ");  // === 1
     * StringBuilder value = new StringBuilder("  1   ");  // = 1
     * BigDecimal value = new BigDecimal("1");  // ============ 1
     * BigInteger value = new BigInteger("1");  // ============ 1
     * Collection value = new ArrayList(){{put(1)}};  // ====== 1（只有一项时）
     * Collection value = new HashSet(){{put(1)}};  // ======== 1（只有一项时）
     * Collection value = new TreeSet(){{put(1)}};  // ======== 1（只有一项时）
     * Collection value = new LinkedList(){{put(1)}};  // ===== 1（只有一项时）
     * Map value = new HashMap(){{put("key", 1)}};  // ======== 1（只有一项时）
     * <p>
     * int[] value = {1, 2, 3, 4};  // ======================================= 4（大于一项时，返回 size）
     * String[] value = {"1", "1", "1", "1"};  // ============================ 4（大于一项时，返回 size）
     * Collection value = new ArrayList(){{put(1);put(1);put(1);}};  // ====== 3（大于一项时，返回 size）
     * Map value = new HashMap(){{put("key", 1);put("name", 2);}};  // ======= 2（大于一项时，返回 size）
     * <p>
     * Byte result = ByteUtil.toByte(value);
     *
     * @param object 待转换的值
     *
     * @return 转换后的 byte 值
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toByteValue(Object)
     */
    public static Byte toByte(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Byte) {
            return (Byte) object;
        }
        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }
        if (object instanceof CharSequence) {
            return Byte.parseByte(object.toString().trim());
        }
        if (object instanceof Boolean) {
            return Byte.valueOf(String.valueOf(((boolean) object) ? 1 : 0));
        }
        try {
            return toByte(ParseSupportUtil.unboxing(object));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Can not cast to int of: %s", object), e);
        }
    }

    /**
     * @param value 待转换的值
     *
     * @return 转换后的 byte 值
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static byte toByteValue(Object value) {
        Byte result = toByte(value);
        return result == null ? 0 : result;
    }

    public static byte avg(byte... values) {
        byte ret = 0;
        int len = values.length;
        for (int i = 0; i < len; ret += values[i++]) {
        }
        return (byte) (ret / len);
    }

    public static Byte avg(Byte[] values) {
        byte ret = 0;
        int len = values.length;
        for (Byte value : values) {
            ret += value;
        }
        return (byte) (ret / len);
    }

    public static Byte avgIgnoreNull(Byte... values) {
        byte ret = 0;
        Byte temp;
        int count = 0;
        for (Byte value : values) {
            temp = value;
            if (temp != null) {
                ret += temp;
                count++;
            }
        }
        return (byte) (ret / count);
    }

    public static byte sum(byte... values) {
        byte ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static Byte sum(Byte[] values) {
        byte ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static Byte sumIgnoreNull(Byte... values) {
        byte ret = 0;
        Byte temp;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            temp = values[i];
            if (temp != null) {
                ret += temp;
            }
        }
        return ret;
    }

    public static Byte multiply(Byte[] values) {
        byte ret = 1;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret *= values[i];
        }
        return ret;
    }

    public static byte multiply(byte... values) {
        byte ret = 1;
        int le = values.length;
        for (int i = 0; i < le; i++) {
            ret *= values[i];
        }
        return ret;
    }

    public static Byte multiplyIgnoreNull(Byte... values) {
        byte ret = 1;
        int len = values.length;
        Byte tmp;
        for (int i = 0; i < len; i++) {
            tmp = values[i];
            if (tmp != null) {
                ret *= tmp;
            }
        }
        return ret;
    }

    public static byte max(byte... values) {
        int len = values.length;
        byte ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Byte max(Byte[] values) {
        int len = values.length;
        byte ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Byte maxIgnoreNull(Byte... values) {
        int len = values.length;
        byte ret = values[0];
        Byte tmp;
        for (int i = 1; i < len; i++) {
            tmp = values[i];
            if (tmp != null && tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    public static Byte min(Byte[] values) {
        int len = values.length;
        byte ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Byte minIgnoreNull(Byte... values) {
        int len = values.length;
        byte ret = values[0];
        Byte tmp;
        for (int i = 1; i < len; i++) {
            tmp = values[i];
            if (tmp != null && tmp < ret) {
                ret = tmp;
            }
        }
        return ret;
    }
}
