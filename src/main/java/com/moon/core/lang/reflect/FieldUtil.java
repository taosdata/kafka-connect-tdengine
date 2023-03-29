package com.moon.core.lang.reflect;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakLocation;
import com.moon.core.util.IteratorUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 类的成员变量的相关操作
 * 1、获取标准 setter / getter 方法
 * 2、设置字段值：优先通过 setter 方法设置，如没有 setter 方法，尝试找到对应字段，并尝试直接写入值
 * 3、获取指定值：优先通过 getter 方法获取，如没有 getter 方法，尝试找到对应字段，并获取值
 * 4、获取指定类的指定名称公共字段
 * 6、获取指定类的指定名称静态公共字段
 * 7、获取指定类的指定名称字段
 * 8、获取指定类的指定名称静态字段
 * 9、获取所有公共字段
 * 10、获取所有本类声明字段
 * 11、获取所有静态公共字段
 * 12、获取所有本类声明的静态字段
 * 13、获取所有父类字段：不包括父类私有字段
 * 14、获取所有字段：不包括父类私有字段
 * 15、获取指定名称的可访问字段：从本类逐级向上查找字段名称字段，可返回本类的指定名称字段或父类指定名称非私有字段
 *
 * @author ZhangDongMin
 */
public final class FieldUtil {

    private FieldUtil() { ThrowUtil.noInstanceError(); }

    private final static WeakLocation<Class, TypeEnum, List<Field>> WEAK = WeakLocation.ofManaged();

    private final static WeakLocation<Class, String, Field> WEAK_APPOINT_FIELD = WeakLocation.ofManaged();

    /**
     * 获取标准 setter 方法
     *
     * @param field
     * @return
     */
    public static Method getSetterMethod(Field field) { return BeanInfoUtil.getSetterMethod(field); }

    /**
     * 获取标准 getter 方法
     *
     * @param field
     * @return
     */
    public static Method getGetterMethod(Field field) { return BeanInfoUtil.getGetterMethod(field); }

    /**
     * 获取标准 getter 方法
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Method getGetterMethod(Class clazz, String name) { return BeanInfoUtil.getGetterMethod(clazz, name); }

    /**
     * 获取标准 getter 方法
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Method getSetterMethod(Class clazz, String name) { return BeanInfoUtil.getSetterMethod(clazz, name); }

    /**
     * 获取指定公共字段
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getPublicField(Class clazz, String name) {
        return WEAK_APPOINT_FIELD.getOrWithElse(clazz, name, () -> {
            try {
                return clazz.getField(name);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    /**
     * 获取所有公共字段（成员字段 + 静态字段）
     *
     * @param clazz
     * @return
     */
    public static List<Field> getPublicFields(Class clazz) {
        return WEAK.getOrWithElse(Objects.requireNonNull(clazz), TypeEnum.PUBLIC,
            () -> UnmodifiableArrayList.unmodifiable(clazz.getFields()));
    }

    /**
     * 获取所有成员公共字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getPublicMemberFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.PUBLIC_MEMBER, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isMember);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 获取所有静态公共字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getPublicStaticFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.PUBLIC_STATIC, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isStatic);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 获取本类声明的指定字段
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getDeclaredField(Class clazz, String name) {
        return WEAK_APPOINT_FIELD.getOrWithElse(clazz, name, () -> {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    /**
     * 获取本类所有声明字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeclaredFields(Class clazz) {
        return WEAK.getOrWithElse(Objects.requireNonNull(clazz), TypeEnum.DECLARED,
            () -> UnmodifiableArrayList.unmodifiable(clazz.getDeclaredFields()));
    }

    /**
     * 获取本类所有成员声明字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeclaredMemberFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.DECLARED_MEMBER, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isMember);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 获取本类所有静态声明字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeclaredStaticFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.DECLARED_STATIC, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isStatic);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 获取父类指定名称字段
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getSuperDeclaredField(Class clazz, String name) {
        List<Field> superFields = getSuperDeclaredFields(clazz);
        for (Field field : superFields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        throw new IllegalArgumentException(new NoSuchFieldException(
            "Not exist field in super class: " + clazz + " with name: " + name));
    }

    /**
     * 返回本类可访问的指定名称字段
     * 本类找不到会沿父级逐级向上查找，直到找到了返回，否则抛出异常
     *
     * @param clazz 类
     * @param name  字段名
     * @return 字段
     */
    public static Field getAccessibleField(Class clazz, String name) {
        try {
            return getDeclaredField(clazz, name);
        } catch (IllegalArgumentException e) {
            return getSuperDeclaredField(clazz, name);
        }
    }

    /**
     * 返回所有父类的字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getSuperDeclaredFields(Class clazz) {
        return WEAK.getOrWithElse(Objects.requireNonNull(clazz), TypeEnum.SUPER, () -> {
            Map<String, Field> superFields = new HashMap<>();
            Class currCls = clazz;
            while (true) {
                if ((currCls = currCls.getSuperclass()) == null) {
                    return new UnmodifiableArrayList(superFields.values()).flipToUnmodify();
                } else {
                    List<Field> fields = getDeclaredFields(currCls);
                    IteratorUtil.forEach(fields, field ->
                        superFields.putIfAbsent(field.getName(), field));
                }
            }
        });
    }

    /**
     * 返回所有父类的成员字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getSuperDeclaredMemberFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.SUPER_MEMBER, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isMember);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 返回所有父类的静态字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getSuperDeclaredStaticFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.SUPER_STATIC, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isStatic);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 获取所有所有（包括父类）字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class clazz) {
        return WEAK.getOrWithElse(Objects.requireNonNull(clazz), TypeEnum.ALL, () -> {
            Map<String, Field> cacheFields = new HashMap<>();
            Class currCls = clazz;
            while (true) {
                Class current = currCls;
                if (current == null) {
                    return new UnmodifiableArrayList(cacheFields.values()).flipToUnmodify();
                } else {
                    IteratorUtil.forEach(getDeclaredFields(current), field ->
                        cacheFields.putIfAbsent(field.getName(), field));
                    currCls = currCls.getSuperclass();
                }
            }
        });
    }

    /**
     * 返回所有所有（包括父类）的成员字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllMemberFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.ALL_MEMBER, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isMember);
            return fields.flipToUnmodify();
        });
    }

    /**
     * 返回所有所有（包括父类）的静态字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllStaticFields(Class clazz) {
        return WEAK.getOrWithElse(clazz, TypeEnum.ALL_STATIC, () -> {
            UnmodifiableArrayList<Field> fields = new UnmodifiableArrayList<>();
            ReflectionSupport.filter(getDeclaredFields(clazz), fields, AssertModifier.isStatic);
            return fields.flipToUnmodify();
        });
    }

    /*
     * 获取、设置字段的值
     */

    public static Object setValue(Field field, Object source, Object value) {
        return BeanInfoUtil.getFieldDescriptor(field).setValue(source, value, false);
    }

    public static Object setValue(Field field, Object source, Object value, boolean accessible) {
        return BeanInfoUtil.getFieldDescriptor(field).setValue(source, value, accessible);
    }

    public static Object setValue(String fieldName, Object source) {
        return getValue(fieldName, source, false);
    }

    public static Object setValue(String fieldName, Object source, boolean accessible) {
        return BeanInfoUtil.getFieldDescriptor(source.getClass(), fieldName).getValue(source, accessible);
    }

    public static Object getValue(Field field, Object source) {
        return getValue(field, source, false);
    }

    public static Object getValue(Field field, Object source, boolean accessible) {
        return BeanInfoUtil.getFieldDescriptor(field).getValue(source, accessible);
    }

    public static Object getValue(String fieldName, Object source) {
        return getValue(fieldName, source, false);
    }

    public static Object getValue(String fieldName, Object source, boolean accessible) {
        return BeanInfoUtil.getFieldDescriptor(source.getClass(), fieldName).getValue(source, accessible);
    }

    enum AssertModifier implements Predicate<Field> {
        isStatic {
            @Override
            public boolean test(Field field) {
                return Modifier.isStatic(field.getModifiers());
            }
        },
        isMember {
            @Override
            public boolean test(Field field) {
                return !Modifier.isStatic(field.getModifiers());
            }
        }
    }
}
