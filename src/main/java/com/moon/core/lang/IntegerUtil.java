package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class IntegerUtil {

    private IntegerUtil() {
        noInstanceError();
    }

    public static boolean isInteger(Object o) {
        return o != null && o.getClass() == Integer.class;
    }

    public static boolean matchInteger(CharSequence o) {
        return IntUtil.matchInt(o);
    }

    public static Integer toInteger(Boolean bool) {
        return bool == null ? null : (bool ? 1 : 0);
    }

    public static Integer toInteger(Character value) {
        return value == null ? null : Integer.valueOf(value);
    }

    public static Integer toInteger(Byte value) {
        return value == null ? null : value.intValue();
    }

    public static Integer toInteger(Short value) {
        return value == null ? null : value.intValue();
    }

    public static Integer toInteger(Long value) {
        return value == null ? null : value.intValue();
    }

    public static Integer toInteger(Float value) {
        return value == null ? null : value.intValue();
    }

    public static Integer toInteger(Double value) {
        return value == null ? null : value.intValue();
    }

    public static Integer toInteger(CharSequence cs) {
        return cs == null ? null : Integer.parseInt(cs.toString());
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
     * int result = IntUtil.toIntValue(value);
     *
     * @param object 带转换值
     *
     * @return 转换后的值
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static Integer toInteger(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof CharSequence) {
            return Integer.parseInt(object.toString().trim());
        } else if (object instanceof Boolean) {
            return (boolean) object ? 1 : 0;
        }
        try {
            return IntUtil.toIntValue(ParseSupportUtil.unboxing(object));
        } catch (Exception e) {
            return ThrowUtil.runtime(e, String.format("Can not cast to int of: %s", object));
        }
    }

    /**
     * 求最大值
     *
     * @param values 数据集合
     *
     * @return 最大值
     */
    public static int max(Iterable<Integer> values) {
        int max = Integer.MIN_VALUE;
        for (Integer value : values) {
            if (value != null) {
                max = Math.max(max, value);
            }
        }
        return max;
    }

    /**
     * 求最大值
     *
     * @param values 数据集合
     *
     * @return 最大值
     */
    public static int max(Integer... values) {
        int max = Integer.MIN_VALUE;
        for (Integer value : values) {
            if (value != null) {
                max = Math.max(max, value);
            }
        }
        return max;
    }

    /**
     * 求最小值
     *
     * @param values 数据集合
     *
     * @return 最小值
     */
    public static int min(Iterable<Integer> values) {
        int max = Integer.MAX_VALUE;
        for (Integer value : values) {
            if (value != null) {
                max = Math.min(max, value);
            }
        }
        return max;
    }

    /**
     * 求最小值
     *
     * @param values 数据集合
     *
     * @return 最小值
     */
    public static int min(Integer... values) {
        int max = Integer.MAX_VALUE;
        for (Integer value : values) {
            if (value != null) {
                max = Math.min(max, value);
            }
        }
        return max;
    }

    /**
     * 求和
     *
     * @param forNull null 值时默认值
     * @param values  数据集合
     *
     * @return 和
     */
    public static int sum(int forNull, Iterable<Integer> values) {
        int sum = 0;
        for (Integer value : values) {
            sum += value == null ? forNull : value;
        }
        return sum;
    }

    /**
     * 求和
     *
     * @param forNull null 值时默认值
     * @param values  数据集合
     *
     * @return 和
     */
    public static int sum(int forNull, Integer... values) {
        int sum = 0;
        for (Integer value : values) {
            sum += value == null ? forNull : value;
        }
        return sum;
    }

    /**
     * 求积
     *
     * @param forNull null 值时默认值
     * @param values  数据集合
     *
     * @return 积
     */
    public static int multiply(int forNull, Iterable<Integer> values) {
        int sum = 1;
        for (Integer value : values) {
            sum *= value == null ? forNull : value;
        }
        return sum;
    }

    /**
     * 求积
     *
     * @param forNull null 值时默认值
     * @param values  数据集合
     *
     * @return 积
     */
    public static int multiply(int forNull, Integer... values) {
        int sum = 1;
        for (Integer value : values) {
            sum *= value == null ? forNull : value;
        }
        return sum;
    }
}
