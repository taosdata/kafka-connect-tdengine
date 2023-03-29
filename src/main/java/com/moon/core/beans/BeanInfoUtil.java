package com.moon.core.beans;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakLocation;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.core.util.IteratorUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * 在 java 规范中，一个字段至少有 getter 或 setter 方法中的一个才会被认为是这个类的属性
 * 同时，如果一个类如果没有某一字段，但却有 setter 或 getter 方法中的一个也会被认为是一个属性
 * 以上同时适用于本类和所有父类
 *
 * @author ZhangDongMin
 * @see FieldDescriptor
 */
public abstract class BeanInfoUtil {

    private BeanInfoUtil() { noInstanceError(); }

    private final static WeakLocation<Object, Object, Object> WEAK = WeakLocation.ofManaged();

    /**
     * 获取标准 setter 方法
     *
     * @param field 待获取字段
     *
     * @return setter 方法或抛出异常
     *
     * @see NoSuchMethodException    找不到 setter 方法（被{@link IllegalArgumentException}）包裹
     * @see IllegalArgumentException 找不到 setter 方法
     */
    public static Method getSetterMethod(Field field) {
        return (Method) WEAK
            .getOrWithElse(field, TypeEnum.SETTER, () -> getSetterMethod(field.getDeclaringClass(), field.getName()));
    }

    /**
     * 获取标准 getter 方法
     *
     * @param field 待获取字段
     *
     * @return getter 方法或抛出异常
     *
     * @see NoSuchMethodException    找不到 getter 方法（被{@link IllegalArgumentException}）包裹
     * @see IllegalArgumentException 找不到 getter 方法
     */
    public static Method getGetterMethod(Field field) {
        return (Method) WEAK
            .getOrWithElse(field, TypeEnum.GETTER, () -> getGetterMethod(field.getDeclaringClass(), field.getName()));
    }

    /**
     * 获取标准 setter 方法
     *
     * @param clazz     目标类
     * @param fieldName 待获取字段名
     *
     * @return setter 方法或抛出异常
     *
     * @see NoSuchMethodException    找不到 setter 方法（被{@link IllegalArgumentException}）包裹
     * @see IllegalArgumentException 找不到 setter 方法
     */
    public static Method getSetterMethod(Class clazz, String fieldName) {
        FieldDescriptor descriptor = getFieldDescriptor(clazz, fieldName);
        if (descriptor.isSetterMethodPresent()) {
            return descriptor.getSetterMethod();
        }
        return ThrowUtil
            .runtime(new NoSuchMethodException("Not exist setter in class: " + clazz + " of field: " + fieldName));
    }

    /**
     * 获取标准 getter 方法
     *
     * @param clazz     目标类
     * @param fieldName 待获取字段名
     *
     * @return getter 方法或抛出异常
     *
     * @see NoSuchMethodException    找不到 getter 方法（被{@link IllegalArgumentException}）包裹
     * @see IllegalArgumentException 找不到 getter 方法
     */
    public static Method getGetterMethod(Class clazz, String fieldName) {
        FieldDescriptor descriptor = getFieldDescriptor(clazz, fieldName);
        if (descriptor.isGetterMethodPresent()) {
            return descriptor.getGetterMethod();
        }
        return ThrowUtil.runtime(
            new NoSuchMethodException("Not exist getter for class: " + clazz + " of field namespace: " + fieldName));
    }

    /**
     * 返回指定字段 getter 执行器
     * 执行器与 getter 方法的区别是，getter 是方法，
     * 而执行器是在没有 getter 方法的时候直接对字段获取值
     *
     * @param field 待获取字段
     *
     * @return getter 执行器
     */
    public static FieldExecutor getGetterExecutor(Field field) {
        return (FieldExecutor) WEAK.getOrWithElse(field, TypeEnum.GET_EXECUTOR,
            () -> getGetterExecutor(field.getDeclaringClass(), field.getName()));
    }

    /**
     * 返回指定字段 setter 执行器
     * 执行器与 setter 方法的区别是，setter 是方法，
     * 而执行器是在没有 setter 方法的时候直接对字段设置值
     *
     * @param field 待获取字段
     *
     * @return setter 执行器
     *
     * @see IllegalArgumentException 当 field 被 final 修饰，且没有对应的 setter 方法就会抛出异常
     */
    public static FieldExecutor getSetterExecutor(Field field) {
        return (FieldExecutor) WEAK.getOrWithElse(field, TypeEnum.SET_EXECUTOR,
            () -> getSetterExecutor(field.getDeclaringClass(), field.getName()));
    }

    /**
     * 返回指定字段 getter 执行器
     * 执行器与 getter 方法的区别是，getter 是方法，
     * 而执行器是在没有 getter 方法的时候直接对字段获取值
     *
     * @param clazz        目标类
     * @param propertyName 待获取字段名
     *
     * @return getter 执行器
     *
     * @see IllegalArgumentException 当 clazz 不存在字段属性时
     */
    public static FieldExecutor getGetterExecutor(Class clazz, String propertyName) {
        return getFieldDescriptor(clazz, propertyName).getGetterExecutor();
    }

    /**
     * 返回指定字段 setter 执行器
     * 执行器与 setter 方法的区别是，setter 是方法，
     * 而执行器是在没有 setter 方法的时候直接对字段设置值
     *
     * @param clazz        目标类
     * @param propertyName 待获取字段名
     *
     * @return setter 执行器
     *
     * @see IllegalArgumentException 当 clazz 不存在对应属性，或对应字段被 final 修饰时，抛出异常
     */
    public static FieldExecutor getSetterExecutor(Class clazz, String propertyName) {
        return getFieldDescriptor(clazz, propertyName).getSetterExecutor();
    }

    public static void ifSetterExecutorPresent(Class clazz, String propertyName, Consumer<FieldDescriptor> c) {
        try {
            getFieldDescriptor(clazz, propertyName).ifSetterPresent(c);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    public static void ifGetterExecutorPresent(Class clazz, String propertyName, Consumer<FieldDescriptor> c) {
        try {
            getFieldDescriptor(clazz, propertyName).ifGetterPresent(c);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    /**
     * 返回属性描述信息
     *
     * @param clazz 目标类
     *
     * @return 类属性描述器集合
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptorsMap(Class clazz) {
        return (Map<String, PropertyDescriptor>) WEAK.getOrWithElse(clazz, TypeEnum.ALL_DESC_MAP, () -> {
            Map<String, PropertyDescriptor> ret = new HashMap<>();
            IteratorUtil.forEach(getPropertyDescriptors(clazz), desc -> ret.put(desc.getName(), desc));
            return ret;
        });
    }

    /**
     * 返回指定类的所有字段描述信息
     * 这儿取出的字段可能包含没有 getter / setter 方法的字段信息，也可能不包含
     *
     * @param clazz 目标类
     *
     * @return 类字段描述器集合
     */
    public static Map<String, FieldDescriptor> getFieldDescriptorsMap(Class clazz) {
        return (Map<String, FieldDescriptor>) WEAK
            .getOrWithElse(Objects.requireNonNull(clazz), TypeEnum.ALL_FIELD_MAP, () -> {
                Map<String, FieldDescriptor> ret = new HashMap<>();
                IteratorUtil.forEach(getPropertyDescriptors(clazz), desc -> {
                    String name = desc.getName();
                    if (!isNameOfClass(name)) {
                        ret.put(name, FieldDescriptor.of(clazz, name, desc));
                    }
                });
                return ret;
            });
    }

    /**
     * 返回所有属性描述信息
     *
     * @param clazz 目标类
     *
     * @return 属性描述器
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) {
        return (PropertyDescriptor[]) WEAK.getOrWithElse(clazz, TypeEnum.ALL_DESC, () -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                return beanInfo.getPropertyDescriptors();
            } catch (IntrospectionException e) {
                return ThrowUtil.runtime(e);
            }
        });
    }

    /**
     * 返回属性描述信息
     *
     * @param clazz        目标类
     * @param propertyName 目标属性名
     *
     * @return 目标属性描述器
     *
     * @see IllegalArgumentException 当 clazz 不存在能访问到的对应名称属性时
     */
    public static PropertyDescriptor getPropertyDescriptor(Class clazz, String propertyName) {
        FieldDescriptor descriptor = getFieldDescriptor(clazz, propertyName);
        return descriptor == null ? null : descriptor.getProperty();
    }

    /**
     * 获取指定字段的字段描述信息
     *
     * @param field 目标字段
     *
     * @return 目标字段描述器
     */
    public static FieldDescriptor getFieldDescriptor(Field field) {
        return (FieldDescriptor) WEAK.getOrWithElse(field, TypeEnum.DESCRIPTOR,
            () -> getFieldDescriptor(field.getDeclaringClass(), field.getName()));
    }

    /**
     * 获取指定 clazz 类中指定字段名描述信息，与 PropertyDescriptor 的区别是，
     * PropertyDescriptor 不包含同时没有 getter / setter 方法的字段，
     * 而通过 FieldDescriptor 能返回对应字段信息
     *
     * @param clazz        目标类
     * @param propertyName 目标属性
     *
     * @return 目标字段描述器
     *
     * @see IllegalArgumentException 当 clazz 不存在能访问到的对应名称属性时
     */
    public static FieldDescriptor getFieldDescriptor(Class clazz, String propertyName) {
        String name = Objects.requireNonNull(propertyName);
        return (FieldDescriptor) WEAK.getOrWithElse(clazz, name, () -> {
            Map<String, FieldDescriptor> descriptorMap = getFieldDescriptorsMap(clazz);
            FieldDescriptor descriptor = descriptorMap.get(name);
            if (descriptor == null) {
                Field field = FieldUtil.getAccessibleField(clazz, name);
                descriptor = FieldDescriptor.of(clazz, name, field);
                descriptorMap.put(name, descriptor);
            }
            return descriptor;
        });
    }

    /**
     * 字段名是否是 class，由于每个类都有默认的 getClass 方法，
     * 故每个类都有一个名为 class 的属性，在这里排除这个字段
     *
     * @param name 字段名
     *
     * @return 是否是 class 字段（对应{@link #getClass()}）方法
     */
    static boolean isNameOfClass(String name) {
        return "class".equals(name);
    }

    enum TypeEnum {
        ALL_DESC,
        ALL_DESC_MAP,
        ALL_FIELD_MAP,
        SETTER,
        GETTER,
        SET_EXECUTOR,
        GET_EXECUTOR,
        DESCRIPTOR
    }
}
