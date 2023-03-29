package com.moon.core.beans;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 在 java 内省机制中，一个字段至少有 getter 或 setter 方法中的一个才会被认为是这个类的属性
 * 同时，如果一个类如果没有某一字段，但却有相应的 setter 或 getter 方法其中一个也会被认为是一个属性
 * 以上同时适用于本类和所有父类；
 * 本类 FieldDescriptor 与 {@link PropertyDescriptor} 不同，
 * PropertyDescriptor 只包含 java 规范默认的属性
 * 本类能描述 java 规范所有的属性和存在字段而不存在 setter 或 getter 方法的属性，
 * 不包括父类的私有字段
 *
 * @author ZhangDongMin
 * @see PropertyDescriptor
 */
public final class FieldDescriptor {

    /**
     * 默认访问权限
     */
    private final static boolean FALSE = false;
    /**
     * 所属类
     */
    private final Class declaringClass;
    /**
     * 属性名
     */
    private final String name;

    /**
     * 属性名是否存在对应字段
     */
    private Boolean isFieldPresent = null;
    /**
     * 属性名对应字段
     */
    private volatile Field field;
    /**
     * 属性名对应的数据类型
     */
    private volatile Class propertyType;

    /**
     * 属性描述
     */
    private final PropertyDescriptor property;
    /**
     * 标准 setter 方法
     */
    private final Method setterMethod;
    /**
     * 标准 getter 方法
     */
    private final Method getterMethod;

    private volatile Boolean isSetterPresent;
    /**
     * 属性设置值执行器
     */
    private FieldExecutor setterExecutor;

    private volatile Boolean isGetterPresent;
    /**
     * 属性获取值值执行器
     */
    private FieldExecutor getterExecutor;

    /**
     * 禁止外部实例化
     *
     * @param declaringClass 这个字段属于哪个类
     * @param name           字段名
     * @param property       属性描述器
     */
    private FieldDescriptor(Class declaringClass, String name, PropertyDescriptor property) {
        this.declaringClass = Objects.requireNonNull(declaringClass);
        this.name = Objects.requireNonNull(name);
        if (property == null) {
            this.property = null;
            this.setterMethod = null;
            this.getterMethod = null;
        } else {
            this.property = property;
            this.setterMethod = property.getWriteMethod();
            this.getterMethod = property.getReadMethod();
            this.propertyType = property.getPropertyType();
        }
    }

    /**
     * 禁止外部实例化
     *
     * @param belongClass 这个字段属于哪个类
     * @param name        字段名
     * @param property    属性描述器
     *
     * @return 返回一个字段描述器
     */
    static FieldDescriptor of(Class belongClass, String name, PropertyDescriptor property) {
        return new FieldDescriptor(belongClass, name, property);
    }

    /**
     * 禁止外部实例化
     *
     * @param belongClass 这个字段属于哪个类
     * @param name        字段名
     * @param field       字段
     *
     * @return 返回一个字段描述器
     */
    static FieldDescriptor of(Class belongClass, String name, Field field) {
        FieldDescriptor descriptor = new FieldDescriptor(belongClass, name, null);
        descriptor.field = field;
        return descriptor;
    }

    public Class getDeclaringClass() { return declaringClass; }

    /**
     * 属性是否有对应 getter 方法
     *
     * @return true or false
     */
    public boolean isGetterMethodPresent() { return getterMethod != null; }

    /**
     * 属性是否有对应 setter 方法
     *
     * @return true or false
     */
    public boolean isSetterMethodPresent() { return setterMethod != null; }

    /**
     * 属性是否有对应字段
     *
     * @return true or false
     */
    public boolean isFieldPresent() {
        if (field == null) {
            if (isFieldPresent == null) {
                return loadField();
            } else {
                return isFieldPresent;
            }
        }
        return true;
    }

    public String getName() { return name; }

    /**
     * 返回属性描述信息
     *
     * @return 返回属性描述信息
     */
    public PropertyDescriptor getProperty() { return property; }

    /**
     * 返回字段，如果有的话，否则返回 null
     *
     * @return 返回字段，如果存在的话，否则返回 null
     */
    public Field getField() { return isFieldPresent() ? field : null; }

    /**
     * 返回标准 setter 方法
     *
     * @return setter 方法
     */
    public Method getSetterMethod() { return setterMethod; }

    /**
     * 返回标准 getter 方法
     *
     * @return getter 方法
     */
    public Method getGetterMethod() { return getterMethod; }

    /**
     * 获取 setter 执行器
     *
     * @return setter 执行器
     */
    public FieldExecutor getSetterExecutor() {
        if (isSetterPresent()) {
            return setterExecutor;
        }
        String msg = exMsg("No setter defaultExecutor of ", name, " in class ", String.valueOf(declaringClass),
            " for you!");
        return ThrowUtil.runtime(msg);
    }

    /**
     * 获取 getter 执行器
     *
     * @return getter 执行器
     */
    public FieldExecutor getGetterExecutor() {
        if (isGetterPresent()) {
            return getterExecutor;
        }
        String msg = exMsg("No getter defaultExecutor of ", name, " in class ", String.valueOf(declaringClass),
            " for you!");
        return ThrowUtil.runtime(msg);
    }

    /**
     * is present setter defaultExecutor
     *
     * @return is present setter defaultExecutor
     */
    public boolean isSetterPresent() {
        if (isSetterPresent == null) {
            synchronized (this) {
                if (isSetterPresent == null) {
                    isSetterPresent = createWriterExecutor();
                }
            }
        }
        return isSetterPresent;
    }

    /**
     * is present getter defaultExecutor
     *
     * @return is present getter defaultExecutor
     */
    public boolean isGetterPresent() {
        if (isGetterPresent == null) {
            synchronized (this) {
                if (isGetterPresent == null) {
                    isGetterPresent = createReaderExecutor();
                }
            }
        }
        return isGetterPresent;
    }

    /**
     * apply consumer when setter defaultExecutor is present
     *
     * @param c consumer
     *
     * @return 返回当前执行器
     */
    public FieldDescriptor ifSetterPresent(Consumer<FieldDescriptor> c) {
        if (isSetterPresent()) {
            c.accept(this);
        }
        return this;
    }

    /**
     * apply consumer when setter method is present
     *
     * @param c consumer
     *
     * @return 返回当前执行器
     */
    public FieldDescriptor ifSetterMethodPresent(Consumer<FieldDescriptor> c) {
        if (isSetterMethodPresent()) {
            c.accept(this);
        }
        return this;
    }

    /**
     * apply consumer when getter defaultExecutor is present
     *
     * @param c consumer
     *
     * @return 返回当前执行器
     */
    public FieldDescriptor ifGetterPresent(Consumer<FieldDescriptor> c) {
        if (isGetterMethodPresent()) {
            c.accept(this);
        }
        return this;
    }

    /**
     * apply consumer when setter method is present
     *
     * @param c consumer
     *
     * @return 返回当前执行器
     */
    public FieldDescriptor ifGetterMethodPresent(Consumer<FieldDescriptor> c) {
        if (getGetterMethod() != null) {
            c.accept(this);
        }
        return this;
    }

    /**
     * 返回属性类型
     *
     * @return 字段类型
     */
    public Class getPropertyType() {
        if (propertyType == null) {
            synchronized (this) {
                if (propertyType == null) {
                    if (isSetterMethodPresent()) {
                        Class[] types = this.setterMethod.getParameterTypes();
                        if (types.length == 1) {
                            this.propertyType = types[0];
                            return propertyType;
                        } else {
                            ThrowUtil.runtime("Property isn't present of: " + name);
                        }
                    }
                    if (isFieldPresent()) {
                        this.propertyType = this.field.getType();
                    } else {
                        ThrowUtil.runtime("Property isn't present of: " + name);
                    }
                }
            }
        }
        return propertyType;
    }

    /**
     * 如果存在可执行的 setter 方法，则执行，否则不执行并返回源对象 obj
     *
     * @param obj   将要设置值的对象
     * @param value 值
     *
     * @return 被设置值的对象
     */
    public Object setValueIfPresent(Object obj, Object value) {
        return setValueIfPresent(obj, value, FALSE);
    }

    /**
     * 如果存在可执行的 setter 方法，则执行，否则不执行并返回源对象 obj
     *
     * @param obj        将要设置值的对象
     * @param value      值
     * @param accessible 可见性
     *
     * @return 被设置值的对象
     */
    public Object setValueIfPresent(Object obj, Object value, boolean accessible) {
        if (isSetterPresent()) {
            return setValue(obj, value, accessible);
        }
        return obj;
    }

    /**
     * 给执行对象的属性设置值
     *
     * @param obj   将要设置值的对象
     * @param value 值
     *
     * @return 被设置值的对象
     */
    public Object setValue(Object obj, Object value) {
        return setValue(obj, value, FALSE);
    }

    /**
     * 给执行对象的属性设置值
     *
     * @param obj        将要设置值的对象
     * @param value      值
     * @param accessible 可见性
     *
     * @return 被设置值的对象
     */
    public Object setValue(Object obj, Object value, boolean accessible) {
        return setValue(obj, value, accessible, TypeUtil.cast());
    }

    /**
     * 给执行对象的属性设置值
     *
     * @param obj        将要设置值的对象
     * @param value      值
     * @param accessible 可见性
     * @param converter  设置前将值转换成字段相容的数据类型
     *
     * @return 被设置值的对象
     */
    public Object setValue(Object obj, Object value, boolean accessible, TypeCaster converter) {
        try {
            return getSetterExecutor().execute(obj, converter.toType(value, getPropertyType()), accessible);
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    // ===================================================================================
    // 获取值以及按指定类型获取值
    // ===================================================================================

    /**
     * 如果存在可执行的读取属性的方法，则读取属性值，否则返回 null
     *
     * @param obj 将要获取值的对象
     *
     * @return 获取的值
     */
    public Object getValueIfPresent(Object obj) {
        return getValueIfPresent(obj, FALSE);
    }

    /**
     * 如果存在可执行的读取属性的方法，则读取属性值，否则返回 null
     *
     * @param obj        将要获取值的对象
     * @param accessible 可见性
     *
     * @return 获取的值
     */
    public Object getValueIfPresent(Object obj, boolean accessible) {
        if (isGetterPresent()) {
            return getValue(obj, accessible);
        }
        return null;
    }

    public Object getValue(Object obj) {
        return getValue(obj, FALSE);
    }

    public Object getValue(Object obj, boolean accessible) {
        try {
            return getGetterExecutor().execute(obj, null, accessible);
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    public String getString(Object source) {
        return getString(source, FALSE);
    }

    public String getString(Object source, boolean accessible) {
        return TypeUtil.cast().toString(getValue(source, accessible));
    }

    public int getInt(Object source) {
        return getInt(source, FALSE);
    }

    public int getInt(Object source, boolean accessible) {
        return TypeUtil.cast().toIntValue(getValue(source, accessible));
    }

    public long getLong(Object source) {
        return getLong(source, FALSE);
    }

    public long getLong(Object source, boolean accessible) {
        return TypeUtil.cast().toLongValue(getValue(source, accessible));
    }

    public double getDouble(Object source) {
        return getDouble(source, FALSE);
    }

    public double getDouble(Object source, boolean accessible) {
        return TypeUtil.cast().toDoubleValue(getValue(source, accessible));
    }

    public boolean getBoolean(Object source) {
        return getBoolean(source, FALSE);
    }

    public boolean getBoolean(Object source, boolean accessible) {
        return TypeUtil.cast().toBooleanValue(getValue(source, accessible));
    }

    public char getChar(Object source) {
        return getChar(source, FALSE);
    }

    public char getChar(Object source, boolean accessible) {
        return TypeUtil.cast().toCharValue(getValue(source, accessible));
    }

    public <E> E getWithType(Object source, Class<E> type) {
        return getWithType(source, FALSE, type);
    }

    public <E> E getWithType(Object source, boolean accessible, Class<E> type) {
        return TypeUtil.cast().toType(getValue(source, accessible), type);
    }


    // ==================================================================
    // 内部方法
    // ==================================================================

    /**
     * get a getOrLoad defaultExecutor with field
     */
    private boolean createReaderExecutor() {
        if (getterMethod == null) {
            if (isFieldPresent()) {
                this.getterExecutor = createExecutorWithField(field, (source, value) -> field.get(source));
            } else {
                return false;
            }
        } else {
            this.getterExecutor = createExecutorWithMethod(getterMethod,
                (source, value) -> getterMethod.invoke(source));
        }
        return true;
    }

    private boolean createWriterExecutor() {
        if (setterMethod == null) {
            if (isFieldPresent() && isNotFinal(field)) {
                this.setterExecutor = createExecutorWithField(field, (source, value) -> {
                    field.set(source, value);
                    return source;
                });
            } else {
                return false;
            }
        } else {
            this.setterExecutor = createExecutorWithMethod(setterMethod,
                (source, value) -> setterMethod.invoke(source, value));
        }
        return true;
    }

    private FieldExecutor createExecutorWithMethod(
        final Method method, final FieldHandler handler
    ) {
        final boolean isNotPublic = !isPublic(method);
        return (source, value, accessAble) -> accessorAndExecute(source, value, isNotPublic, accessAble, handler,
            method);
    }

    private FieldExecutor createExecutorWithField(
        final Field field, final FieldHandler handler
    ) {
        final boolean isNotPublic = !isPublic(field);
        return (source, value, accessAble) -> accessorAndExecute(source, value, isNotPublic, accessAble, handler,
            field);
    }

    private Object accessorAndExecute(
        Object source, Object value, boolean isNotPublic, boolean accessAble, FieldHandler handler, AccessibleObject ao
    ) throws Exception {
        Object result;
        if (accessAble) {
            if (isNotPublic) {
                ao.setAccessible(true);
                result = handler.handle(source, value);
                ao.setAccessible(false);
            } else {
                result = handler.handle(source, value);
            }
        } else {
            result = handler.handle(source, value);
        }
        return result;
    }

    /**
     * 加载字段，如果存在字段，返回 true，否则返回 false
     *
     * @return true or false
     */
    private boolean loadField() {
        if (field == null) {
            boolean isFieldPresent;
            synchronized (this) {
                if (field == null) {
                    try {
                        this.field = FieldUtil.getAccessibleField(declaringClass, name);
                        isFieldPresent = this.isFieldPresent = this.field != null;
                    } catch (IllegalArgumentException e) {
                        this.field = null;
                        isFieldPresent = this.isFieldPresent = false;
                    }
                } else {
                    isFieldPresent = field != null;
                }
            }
            return isFieldPresent;
        }
        return true;
    }

    private String exMsg(CharSequence... css) {
        return StringUtil.concat(css);
    }

    private boolean isPublic(Member m) {
        return Modifier.isPublic(m.getModifiers());
    }

    private boolean isNotFinal(Member m) {
        return !Modifier.isFinal(m.getModifiers());
    }
}
