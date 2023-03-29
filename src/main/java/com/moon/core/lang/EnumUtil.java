package com.moon.core.lang;

import com.moon.core.util.ListUtil;
import com.moon.core.util.TestUtil;

import java.util.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class EnumUtil {

    private EnumUtil() { noInstanceError(); }

    /**
     * 枚举的第一项值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 第一个元素或 null
     */
    public static <T extends Enum<T>> T first(Class<T> enumType) {
        T[] enums = values(enumType);
        return enums.length > 0 ? enums[0] : null;
    }

    /**
     * 枚举类最后一项值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 最后一个元素
     */
    public static <T extends Enum<T>> T last(Class<T> enumType) {
        T[] enums = values(enumType);
        return enums.length > 0 ? enums[enums.length - 1] : null;
    }

    /**
     * 枚举类所有值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> T[] values(Class<T> enumType) {
        return enumType.getEnumConstants();
    }

    /**
     * 枚举类所有值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> List<T> valuesList(Class<T> enumType) {
        return ListUtil.toList(values(enumType));
    }

    /**
     * {@link EnumSet}
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> EnumSet<T> valuesSet(Class<T> enumType) { return EnumSet.allOf(enumType); }

    /**
     * 按名称排序后枚举类所有值
     *
     * @param type 枚举类型
     * @param <T>  泛型
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> T[] sortedValuesByName(Class<T> type) {
        return sortedValues(type, Comparator.comparing(Enum::name));
    }

    /**
     * 按指定顺序排序的枚举类所有值
     *
     * @param comparator 比较强
     * @param type       枚举类型
     * @param <T>        泛型
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> T[] sortedValues(Class<T> type, Comparator<? super T> comparator) {
        return ArrayUtil.sort(comparator, values(type));
    }

    /**
     * 枚举类所有值的名称与值的 Map
     * enum's name map to enum's value
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 枚举值
     *
     * @see Enum#name()
     */
    public static <T extends Enum<T>> Map<String, T> valuesMap(Class<T> enumType) {
        T[] enums = values(enumType);
        int length = enums.length;
        Map<String, T> ret = new HashMap<>(length);
        for (T anEnum : enums) {
            ret.put(anEnum.name(), anEnum);
        }
        return ret;
    }

    /**
     * 排序比较
     *
     * @param e1  枚举值1
     * @param e2  枚举值2
     * @param <E> 枚举类型
     *
     * @return 比较结果
     */
    public static <E extends Enum<E>> int compareTo(E e1, E e2) { return e1.compareTo(e2); }

    /**
     * e1 是否在 e2 前面，比较序号
     *
     * @param e1  枚举值1
     * @param e2  枚举值2
     * @param <E> 枚举类型
     *
     * @return 比较结果
     */
    public static <E extends Enum<E>> boolean isBefore(E e1, E e2) { return compareTo(e1, e2) < 0; }

    /**
     * e1 是否在 e2 后面，比较序号
     *
     * @param e1  枚举值1
     * @param e2  枚举值2
     * @param <E> 枚举类型
     *
     * @return 比较结果
     */
    public static <E extends Enum<E>> boolean isAfter(E e1, E e2) { return compareTo(e1, e2) > 0; }

    /**
     * 枚举类包含多少项
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 枚举值数量
     */
    public static <T extends Enum<T>> int length(Class<T> enumType) { return values(enumType).length; }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) { return Enum.valueOf(enumType, name); }

    public static <T extends Enum<T>> T valueAt(Class<T> enumType, int index) { return toEnum(enumType, index); }

    /**
     * 是否包含指定名称的枚举值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     * @param name     枚举名
     *
     * @return 是否包含枚举值
     */
    public static <T extends Enum<T>> boolean contains(Class<T> enumType, String name) {
        if (enumType == null || name == null) {
            return false;
        }
        try {
            Enum.valueOf(enumType, name);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    /**
     * 返回符合指定名称的枚举值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     * @param name     枚举名
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, String name) {
        try {
            return name == null ? null : Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
            return throwEnumConst(enumType, name);
        }
    }

    /**
     * 返回指定位置的枚举值
     *
     * @param enumType 枚举类型
     * @param <T>      泛型
     * @param ordinal  序号
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, int ordinal) {
        try {
            return values(enumType)[ordinal];
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return throwEnumConst(enumType, ordinal);
        }
    }

    /**
     * 超级转换器
     *
     * @param value    带转换的值
     * @param enumType 枚举类型
     * @param <T>      泛型
     *
     * @return 枚举值
     */
    public static <T extends Enum<T>> T toEnum(Object value, Class<T> enumType) {
        if (value == null || enumType == null) {
            return null;
        } else if (enumType.isInstance(value)) {
            return (T) value;
        } else if (value instanceof CharSequence) {
            String name = value.toString();
            if (TestUtil.isNumeric(name)) {
                int ordinal = Integer.parseInt(name);
                return toEnum(enumType, ordinal);
            }
            return toEnum(enumType, name);
        } else if (value instanceof Integer) {
            return toEnum(enumType, (int) value);
        } else if (value instanceof Number) {
            return toEnum(enumType, ((Number) value).intValue());
        } else if (value.getClass().isEnum()) {
            return toEnum(enumType, ((Enum) value).name());
        }
        return throwEnumConst(enumType, value);
    }

    private static <T> T throwEnumConst(Class type, Object value) {
        throw new IllegalArgumentException("No enum constant " + type.getCanonicalName() + "[" + value + "]");
    }
}
