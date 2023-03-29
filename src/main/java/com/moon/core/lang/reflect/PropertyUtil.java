package com.moon.core.lang.reflect;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldExecutor;
import com.moon.core.lang.ref.WeakLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

import static com.moon.core.enums.Casters.*;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author ZhangDongMin
 */
public final class PropertyUtil {

    private PropertyUtil() { noInstanceError(); }

    /**
     * 获取某个属性
     *
     * @param t      对象
     * @param getter 对象属性 getter
     * @param <T>    对象数据类型
     * @param <R>    将要获取的属性的数据类型
     *
     * @return 对象属性值或 null
     */
    public static <T, R> R getOrNull(T t, Function<? super T, ? extends R> getter) {
        return t == null ? null : getter.apply(t);
    }

    /**
     * 用 equals 比较某个属性是否相等
     *
     * @param t1     被比较对象
     * @param t2     比较对象
     * @param getter 比较的属性
     * @param <T>    对象数据类型
     *
     * @return 对象属性是否想通
     */
    public static <T> boolean propertyEquals(T t1, T t2, Function<? super T, ?> getter) {
        return (t1 == t2) || ((t1 != null && t2 != null) && Objects.equals(getter.apply(t1), getter.apply(t2)));
    }

    /**
     * 获取标准 setter 方法
     *
     * @param field 字段
     *
     * @return 字段对应的 setter 方法
     */
    public static Method getSetterMethod(Field field) { return BeanInfoUtil.getSetterMethod(field); }

    /**
     * 获取标准 getter 方法
     *
     * @param field 字段
     *
     * @return 字段对应的 getter 方法
     */
    public static Method getGetterMethod(Field field) { return BeanInfoUtil.getGetterMethod(field); }

    /**
     * 获取标准 getter 方法
     *
     * @param clazz
     * @param name
     *
     * @return
     */
    public static Method getGetterMethod(Class clazz, String name) { return BeanInfoUtil.getGetterMethod(clazz, name); }

    /**
     * 获取标准 getter 方法
     *
     * @param clazz
     * @param name
     *
     * @return
     */
    public static Method getSetterMethod(Class clazz, String name) { return BeanInfoUtil.getSetterMethod(clazz, name); }

    /**
     * 优先通过 setter 方法为对象 source 赋值；
     * 没有 setter 方法就尝试直接赋值
     *
     * @param field
     * @param source
     * @param value
     */
    public static void setValue(Field field, Object source, Object value) { setValue(field, source, value, false); }

    /**
     * 设置值
     *
     * @param fieldName
     * @param source
     * @param value
     */
    public static void setValue(String fieldName, Object source, Object value) {
        setValue(fieldName, source, value, false);
    }

    /**
     * 优先通过 setter 方法为对象 source 赋值；
     * 没有 setter 方法就尝试直接赋值
     *
     * @param field
     * @param source
     * @param value
     * @param accessible
     */
    public static void setValue(Field field, Object source, Object value, boolean accessible) {
        writeValue(field.getDeclaringClass(), field.getName(), source, value, accessible);
    }

    /**
     * 设置字段值
     *
     * @param fieldName
     * @param source
     * @param value
     * @param accessible
     */
    public static void setValue(String fieldName, Object source, Object value, boolean accessible) {
        writeValue(source.getClass(), fieldName, source, value, accessible);
    }

    /**
     * 获取字段值
     *
     * @param field
     * @param source
     *
     * @return
     */
    public static Object getValue(Field field, Object source) { return getValue(field, source, false); }

    /**
     * 获取字段值
     *
     * @param field
     * @param source
     * @param accessAble
     *
     * @return
     */
    public static Object getValue(Field field, Object source, boolean accessAble) {
        return readValue(field.getDeclaringClass(), field.getName(), source, accessAble);
    }

    /**
     * 获取指定值
     *
     * @param fieldName
     * @param source
     *
     * @return
     */
    public static Object getValue(String fieldName, Object source) { return getValue(fieldName, source, false); }

    /**
     * 获取字段值
     *
     * @param fieldName
     * @param source
     * @param accessAble
     *
     * @return
     */
    public static Object getValue(String fieldName, Object source, boolean accessAble) {
        return readValue(source.getClass(), fieldName, source, accessAble);
    }

    public static String getString(Field field, Object source) { return getString(field, source, false); }

    public static String getString(Field field, Object source, boolean accessible) {
        return toString.cast(getValue(field, source, accessible));
    }

    public static String getString(String fieldName, Object source) { return getString(fieldName, source, false); }

    public static String getString(String fieldName, Object source, boolean accessible) {
        return toString.cast(getValue(fieldName, source, accessible));
    }

    public static int getInt(Field field, Object source) { return getInt(field, source, false); }

    public static int getInt(Field field, Object source, boolean accessible) {
        return toIntValue.cast(getValue(field, source, accessible));
    }

    public static int getInt(String fieldName, Object source) { return getInt(fieldName, source, false); }

    public static int getInt(String fieldName, Object source, boolean accessible) {
        return toIntValue.cast(getValue(fieldName, source, accessible));
    }

    public static long getLong(Field field, Object source) { return getLong(field, source, false); }

    public static long getLong(Field field, Object source, boolean accessible) {
        return toLongValue.cast(getValue(field, source, accessible));
    }

    public static long getLong(String fieldName, Object source) { return getLong(fieldName, source, false); }

    public static long getLong(String fieldName, Object source, boolean accessible) {
        return toLongValue.cast(getValue(fieldName, source, accessible));
    }

    public static double getDouble(Field field, Object source) { return getInt(field, source, false); }

    public static double getDouble(Field field, Object source, boolean accessible) {
        return toDoubleValue.cast(getValue(field, source, accessible));
    }

    public static double getDouble(String fieldName, Object source) { return getDouble(fieldName, source, false); }

    public static double getDouble(String fieldName, Object source, boolean accessible) {
        return toDoubleValue.cast(getValue(fieldName, source, accessible));
    }

    public static boolean getBoolean(Field field, Object source) { return getBoolean(field, source, false); }

    public static boolean getBoolean(Field field, Object source, boolean accessible) {
        return toBooleanValue.cast(getValue(field, source, accessible));
    }

    public static boolean getBoolean(String fieldName, Object source) { return getBoolean(fieldName, source, false); }

    public static boolean getBoolean(String fieldName, Object source, boolean accessible) {
        return toBooleanValue.cast(getValue(fieldName, source, accessible));
    }

    public static char getChar(Field field, Object source) { return getChar(field, source, false); }

    public static char getChar(Field field, Object source, boolean accessible) {
        return toCharValue.cast(getValue(field, source, accessible));
    }

    public static char getChar(String fieldName, Object source) { return getChar(fieldName, source, false); }

    public static char getChar(String fieldName, Object source, boolean accessible) {
        return toCharValue.cast(getValue(fieldName, source, accessible));
    }

    /**
     * 缓存字段读取和设置的执行器
     */
    private final static WeakLocation<Class, String, FieldExecutor> SETTER_EXECUTOR = WeakLocation.ofManaged();
    private final static WeakLocation<Class, String, FieldExecutor> GETTER_EXECUTOR = WeakLocation.ofManaged();

    /**
     * 读取值
     *
     * @param targetType   目标类
     * @param propertyName 属性名
     * @param source       作用对象，一般 source 是 targetType 的实例
     * @param accessible   可见状态
     *
     * @return 返回字段值
     */
    private static Object readValue(
        Class targetType, String propertyName, Object source, boolean accessible
    ) {
        try {
            return reader(targetType, propertyName).execute(source, null, accessible);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 给属性设置值
     *
     * @param targetType   目标类
     * @param propertyName 属性名
     * @param source       作用对象，一般 source 是 targetType 的实例
     * @param value        要设置的值
     * @param accessible   可见状态
     *
     * @return 返回作用对象 source
     */
    private static Object writeValue(
        Class targetType, String propertyName, Object source, Object value, boolean accessible
    ) {
        try {
            return writer(targetType, propertyName).execute(source, value, accessible);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static FieldExecutor reader(Class clazz, String name) {
        return GETTER_EXECUTOR.getOrWithElse(clazz, name, () -> BeanInfoUtil.getGetterExecutor(clazz, name));
    }

    private static FieldExecutor writer(Class clazz, String name) {
        return SETTER_EXECUTOR.getOrWithElse(clazz, name, () -> BeanInfoUtil.getSetterExecutor(clazz, name));
    }
}
