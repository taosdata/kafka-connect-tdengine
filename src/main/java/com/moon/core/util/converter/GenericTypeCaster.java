package com.moon.core.util.converter;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.enums.ArrayOperator;
import com.moon.core.enums.Arrays2;
import com.moon.core.enums.Casters;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.CollectUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.Optional;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.moon.core.enums.Casters.*;
import static com.moon.core.lang.ClassUtil.forNameOrNull;
import static com.moon.core.lang.reflect.MethodUtil.getDeclaredMethod;
import static com.moon.core.lang.reflect.MethodUtil.invokeStatic;
import static com.moon.core.util.CollectUtil.addAll;
import static com.moon.core.util.Optional.ofNullable;
import static com.moon.core.util.OptionalUtil.resolveOrNull;

/**
 * @author moonsky
 */
public class GenericTypeCaster implements TypeCaster {

    protected final WeakAccessor<MapBuilder> mapAccessor = WeakAccessor.of(MapBuilder::new);
    protected final WeakAccessor<ListBuilder> listAccessor = WeakAccessor.of(ListBuilder::new);
    protected final WeakAccessor<ArrayBuilder> arrayAccessor = WeakAccessor.of(ArrayBuilder::new);
    protected final WeakAccessor<CollectBuilder> collectAccessor = WeakAccessor.of(CollectBuilder::new);

    protected final Map<Class, BiFunction<Object, Class, Object>> converters = new HashMap<>();

    public GenericTypeCaster() { registerDefaultConverter(); }

    /**
     * 注册默认转换器
     */
    private void registerDefaultConverter() {
        for (Casters value : Casters.values()) {
            BiFunction converter = value;
            add(value.TYPE, converter);
        }
        ofNullable(forNameOrNull("com.google.common.base.Optional")).ifPresent(optCls -> {
            Method m = getDeclaredMethod(optCls, "fromNullable", Object.class);
            add(optCls, (v, t) -> optCls.isInstance(v) ? v : invokeStatic(m, resolveOrNull(v)));
        });
        add(Optional.class, (v, t) -> v instanceof Optional ? (Optional) v : ofNullable(resolveOrNull(v)));
        // Collection convert
        add(Collection.class, (value, toType) -> {
            if (value == null || toType == null) { return null; }
            Class cls;
            CollectBuilder builder = collectAccessor.get();
            if (value instanceof List) {
                return builder.toCollect((List) value, toType);
            } else if (value instanceof Collection) {
                return builder.toCollect((Collection) value, toType);
            } else if (value instanceof Map) {
                return builder.toCollect((Map) value, toType);
            } else if ((cls = value.getClass()).isArray()) {
                if (cls == int[].class) {
                    return builder.toCollect((int[]) value, toType);
                } else if (cls == long[].class) {
                    return builder.toCollect((long[]) value, toType);
                } else if (cls == double[].class) {
                    return builder.toCollect((double[]) value, toType);
                } else if (cls == byte[].class) {
                    return builder.toCollect((byte[]) value, toType);
                } else if (cls == char[].class) {
                    return builder.toCollect((char[]) value, toType);
                } else if (cls == short[].class) {
                    return builder.toCollect((short[]) value, toType);
                } else if (cls == boolean[].class) {
                    return builder.toCollect((boolean[]) value, toType);
                } else if (cls == float[].class) {
                    return builder.toCollect((float[]) value, toType);
                }
                return builder.toCollect((Object[]) value, toType);
            }
            return builder.toCollect(value, toType);
        });
        // List convert
        add(List.class, (value, toType) -> {
            if (value == null || toType == null) { return null; }
            ListBuilder builder = listAccessor.get();
            if (value instanceof List) {
                return builder.toList((List) value, toType);
            } else if (value instanceof Collection) {
                return builder.toList((Collection) value, toType);
            } else if (value instanceof Map) {
                return builder.toList((Map) value, toType);
            }
            Class cls;
            if ((cls = value.getClass()).isArray()) {
                if (cls == int[].class) {
                    return builder.toList((int[]) value, toType);
                } else if (cls == long[].class) {
                    return builder.toList((long[]) value, toType);
                } else if (cls == double[].class) {
                    return builder.toList((double[]) value, toType);
                } else if (cls == byte[].class) {
                    return builder.toList((byte[]) value, toType);
                } else if (cls == char[].class) {
                    return builder.toList((char[]) value, toType);
                } else if (cls == short[].class) {
                    return builder.toList((short[]) value, toType);
                } else if (cls == boolean[].class) {
                    return builder.toList((boolean[]) value, toType);
                } else if (cls == float[].class) {
                    return builder.toList((float[]) value, toType);
                }
                return builder.toList((Object[]) value, toType);
            }
            return builder.toList(value, toType);
        });
        add(Map.class, converterOfMap());
    }

    /**
     * Map convert
     *
     * @param <C> 泛型
     *
     * @return 转换器
     */
    private <C> BiFunction<Object, Class<C>, C> converterOfMap() {
        return (value, toType) -> {
            if (value == null || toType == null) { return null; }
            Class cls;
            MapBuilder builder = mapAccessor.get();
            if (value instanceof Collection) {
                return builder.toMap((Collection) value, toType);
            } else if (value instanceof Map) {
                return builder.toMap((Map) value, toType);
            } else if ((cls = value.getClass()).isArray()) {
                if (cls == int[].class) {
                    return builder.toMap((int[]) value, toType);
                } else if (cls == long[].class) {
                    return builder.toMap((long[]) value, toType);
                } else if (cls == double[].class) {
                    return builder.toMap((double[]) value, toType);
                } else if (cls == byte[].class) {
                    return builder.toMap((byte[]) value, toType);
                } else if (cls == char[].class) {
                    return builder.toMap((char[]) value, toType);
                } else if (cls == short[].class) {
                    return builder.toMap((short[]) value, toType);
                } else if (cls == boolean[].class) {
                    return builder.toMap((boolean[]) value, toType);
                } else if (cls == float[].class) {
                    return builder.toMap((float[]) value, toType);
                }
                return builder.toMap((Object[]) value, toType);
            } else {
                return builder.toMap(value, toType);
            }
        };
    }

    /**
     * 添加新的转换器，受保护，实际执行的方法
     *
     * @param toType 目标类型
     * @param func   转换器
     * @param <C>    泛型
     *
     * @return this
     */
    private <C> TypeCaster add(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
        BiFunction converter = func;
        converters.put(toType, converter);
        return this;
    }

    /**
     * 注册新的转换器，如果已存在将覆盖原有转换器
     *
     * @param toType 目标类型
     * @param func   转换器
     * @param <C>    泛型
     *
     * @return this
     */
    @Override
    public <C> TypeCaster register(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
        return add(toType, func);
    }

    /**
     * 注册缺少的转换器
     *
     * @param toType 目标类型
     * @param func   转换器
     * @param <C>    泛型
     *
     * @return this
     */
    @Override
    public <C> TypeCaster registerIfAbsent(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
        BiFunction converter = func;
        converters.putIfAbsent(toType, converter);
        return this;
    }

    /**
     * 转换成指定类型数据
     *
     * @param value 带转换值
     * @param type  目标类型
     * @param <T>   泛型
     *
     * @return this
     */
    @Override
    public <T> T toType(Object value, Class<T> type) {
        if (type == null) { return null; }

        BiFunction<Object, Class, Object> func = converters.get(type);
        if (func != null) {
            return (T) func.apply(value, type);
        } else if (type.isEnum()) {
            return (T) toEnum.apply(value, type);
        } else if (type.isArray() || type == Array.class) {
            return toArray(value, type);
        } else if (List.class.isAssignableFrom(type)) {
            return (T) converters.get(List.class).apply(value, type);
        } else if (Collection.class.isAssignableFrom(type)) {
            return (T) converters.get(Collection.class).apply(value, type);
        } else if (Map.class.isAssignableFrom(type)) {
            return (T) converters.get(Map.class).apply(value, type);
        } else if (value instanceof Map) {
            return toBean((Map) value, type);
        } else if (type.isInstance(value)) {
            return (T) value;
        } else if (value == null) {
            if (type.isPrimitive()) {
                if (type == int.class) {
                    return (T) Integer.valueOf(0);
                } else if (type == long.class) {
                    return (T) Long.valueOf(0);
                } else if (type == double.class) {
                    return (T) Double.valueOf(0);
                } else if (type == short.class) {
                    return (T) Short.valueOf((short) 0);
                } else if (type == byte.class) {
                    return (T) Byte.valueOf((byte) 0);
                } else if (type == float.class) {
                    return (T) Float.valueOf(0);
                } else if (type == boolean.class) {
                    return (T) Boolean.FALSE;
                }
            } else {
                return null;
            }
        }

        throw new ClassCastException(StringUtil.format("Can not cast: {} to type of: {}", value, type));
    }

    private <T, S> T convert(Object value, Class<T> type, Class<S> superType) {
        return (T) converters.get(superType).apply(value, type);
    }

    private <E> E convert(Object value, Casters converter, Class type) { return (E) converter.apply(value, type); }

    private <E> E convert(Object value, Casters converter) { return convert(value, converter, converter.TYPE); }

    @Override
    public boolean toBooleanValue(Object value) { return convert(value, toBooleanValue); }

    @Override
    public Boolean toBoolean(Object value) { return convert(value, toBoolean); }

    @Override
    public char toCharValue(Object value) { return convert(value, toCharValue); }

    @Override
    public Character toCharacter(Object value) { return convert(value, toCharacter); }

    @Override
    public byte toByteValue(Object value) { return convert(value, toByteValue); }

    @Override
    public Byte toByte(Object value) { return convert(value, toByte); }

    @Override
    public short toShortValue(Object value) { return convert(value, toShortValue); }

    @Override
    public Short toShort(Object value) { return convert(value, toShort); }

    @Override
    public int toIntValue(Object value) { return convert(value, toIntValue); }

    @Override
    public Integer toInteger(Object value) { return convert(value, toInteger); }

    @Override
    public long toLongValue(Object value) { return convert(value, toLongValue); }

    @Override
    public Long toLong(Object value) { return convert(value, toLong); }

    @Override
    public float toFloatValue(Object value) { return convert(value, toFloatValue); }

    @Override
    public Float toFloat(Object value) { return convert(value, toFloat); }

    @Override
    public double toDoubleValue(Object value) { return convert(value, toDoubleValue); }

    @Override
    public Double toDouble(Object value) { return convert(value, toDouble); }

    @Override
    public BigInteger toBigInteger(Object value) { return convert(value, toBigInteger); }

    @Override
    public BigDecimal toBigDecimal(Object value) { return convert(value, toBigDecimal); }

    @Override
    public String toString(Object value) { return convert(value, toString); }

    @Override
    public StringBuilder toStringBuilder(Object value) { return convert(value, toStringBuilder); }

    @Override
    public StringBuffer toStringBuffer(Object value) { return convert(value, toStringBuffer); }

    @Override
    public <T extends java.util.Optional> T toOptional(Object value) { return convert(value, toOptional); }

    /**
     * if data is null or clazz is null will back null
     *
     * @param value
     * @param clazz
     * @param <T>
     *
     * @return
     */
    @Override
    public <T extends Enum<T>> T toEnum(Object value, Class<T> clazz) { return convert(value, toEnum, clazz); }

    /**
     * if data is null or clazz is null will back null.
     * And can not convert to clazz will throw Exception
     *
     * @param map   带转换值
     * @param clazz 目标类型
     * @param <T>   泛型
     *
     * @return 转换后的值
     */
    @Override
    public <T> T toBean(Map map, Class<T> clazz) {
        if (clazz == null || map == null) {
            return null;
        }
        T obj = ConstructorUtil.newInstance(clazz);
        if (!map.isEmpty()) {
            BeanInfoUtil.getFieldDescriptorsMap(clazz).forEach((name, desc) -> {
                map.computeIfPresent(name, (key, value) -> {
                    desc.ifSetterPresent(descriptor -> descriptor.setValue(obj, value, true));
                    return value;
                });
            });
        }
        return obj;
    }

    /**
     * if data is null or clazz is null will back null
     *
     * @param value     带转换值
     * @param arrayType 数组类型
     * @param <T>       数组类型泛型
     *
     * @return 数组
     */
    @Override
    public <T> T toArray(Object value, Class<T> arrayType) {
        if (arrayType == null) {
            return null;
        } else if (arrayType == Array.class) {
            return (T) toTypeArray(value, Object.class);
        } else if (arrayType.isArray()) {
            return (T) toTypeArray(value, arrayType.getComponentType());
        }
        return ThrowUtil.runtime("Must an array type:" + arrayType);
    }

    /**
     * if data is null or clazz is null will back null
     *
     * @param value         待转换值
     * @param componentType 数组元素类型
     * @param <T>           数组元素类型
     *
     * @return 数组
     */
    @Override
    public <T> T[] toTypeArray(Object value, Class<T> componentType) {
        if (value == null || componentType == null) {
            return null;
        }
        Class cls;
        ArrayBuilder builder = arrayAccessor.get();
        if (value instanceof List) {
            return builder.toArray((List) value, componentType);
        } else if (value instanceof Collection) {
            return builder.toArray((Collection) value, componentType);
        } else if (value instanceof Map) {
            return builder.toArray((Map) value, componentType);
        } else if ((cls = value.getClass()).isArray()) {
            ArrayOperator operator = Arrays2.getOrObjects(cls);
            T[] array = createArray(componentType, operator.length(value));
            operator.forEach(value, (item, index) -> array[index] = toType(item, componentType));
            return array;
        }
        return builder.toArray(value, componentType);
    }

    @Override
    public <T extends Map> T toMap(Object value, Class<T> mapClass) { return convert(value, mapClass, Map.class); }

    @Override
    public <T extends List> T toList(Object value, Class<T> listType) { return convert(value, listType, List.class); }

    @Override
    public <T extends Collection> T toCollection(Object value, Class<T> collectType) {
        return convert(value, collectType, Collection.class);
    }

    // **********************************************************************************************
    // collections builder: to collection, to data, to array and to map.
    // 凡是进入 builder 的指定类型（type）和 data 均不为 null，故不做 null 值判断
    // **********************************************************************************************

    private static class CollectBuilder {

        Collection toCollect(Map value, Class listImplType) {
            return addAll(createCollect(listImplType, isAbstract(listImplType)), value.values());
        }

        Collection toCollect(Collection value, Class listImplType) {
            return listImplType.isInstance(value) ? value
                : createCollect(listImplType, isAbstract(listImplType), value);
        }

        Collection toCollect(Object[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(int[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(long[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(double[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(char[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(byte[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(short[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(float[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(boolean[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createCollect(listImplType, false, array);
        }

        Collection toCollect(Object value, Class listImplType) {
            return CollectUtil.add(createCollect(listImplType, isAbstract(listImplType)), value);
        }

        <T extends Collection, E> Collection createCollect(Class<T> listImplType, boolean isDefault, E... values) {
            return addAll(createCollect(listImplType, isDefault), values);
        }

        <T extends Collection> Collection createCollect(Class<T> listImplType, boolean isDefault) {
            return newInstance(isDefault, listImplType,
                isDefault && Set.class.isAssignableFrom(listImplType) ? HashSet::new : ArrayList::new);
        }
    }

    private static class ListBuilder {

        List toList(Map value, Class listImplType) {
            return addAll(createList(listImplType, isAbstract(listImplType)), value.values());
        }

        List toList(Collection value, Class listImplType) {
            return listImplType.isInstance(value) ? (List) value
                : createList(listImplType, isAbstract(listImplType), value);
        }

        List toList(Object[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(int[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(long[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(double[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(short[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(char[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(byte[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(float[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(boolean[] array, Class listImplType) {
            return isAbstract(listImplType) ? ListUtil.toList(array) : createList(listImplType, false, array);
        }

        List toList(Object value, Class listImplType) {
            return CollectUtil.add(createList(listImplType, isAbstract(listImplType)), value);
        }

        <T extends List, E> List createList(Class<T> listImplType, boolean isDefault, E... values) {
            return addAll(createList(listImplType, isDefault), values);
        }

        <T extends List> List createList(Class<T> listImplType, boolean isDefault) {
            return newInstance(isDefault, listImplType, ArrayList::new);
        }
    }

    private static class ArrayBuilder {

        <T> T[] toArray(List value, Class<T> componentType) {
            return (T[]) value.toArray(createArray(componentType, value.size()));
        }

        <T> T[] toArray(Collection value, Class<T> componentType) {
            return (T[]) value.toArray(createArray(componentType, value.size()));
        }

        <T> T[] toArray(Map value, Class<T> componentType) { return toArray(value.values(), componentType); }

        <T> T[] toArray(Object value, Class<T> componentType) {
            T[] array = createArray(componentType, 1);
            Array.set(array, 0, value);
            return array;
        }
    }

    private enum StructureEnum {
        NONE,
        DEFAULT,
        ARRAY_LENGTH_2,
        ENTRY,
        LIST_SIZE_2
    }

    private static class MapBuilder {

        <T> T toMap(Object value, Class mapClass) {
            Map result = createMap(mapClass);
            BeanInfoUtil.getFieldDescriptorsMap(value.getClass()).forEach(
                (name, desc) -> desc.ifGetterPresent(descriptor -> result.put(name, descriptor.getValue(value, true))));
            return (T) result;
        }

        <T> T toMap(Map value, Class<T> mapClass) {
            if (mapClass.isInstance(value)) {
                return (T) value;
            }
            Map result = createMap(mapClass);
            result.putAll(value);
            return (T) result;
        }

        <T> T toMap(Object[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = Array.getLength(value); index < len; index++) {
                Object object = value[index];
                if (mode == StructureEnum.NONE && (mode = structureMode(object)) == StructureEnum.NONE) {
                    continue;
                }
                try {
                    putKeyValue(mode, result, object, index);
                } catch (NullPointerException e) {
                    continue;
                } catch (Exception e) {
                    index = -1;
                    mode = StructureEnum.DEFAULT;
                    result.clear();
                }
            }
            return (T) result;
        }

        <T> T toMap(int[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(long[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(double[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(char[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(byte[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(short[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(float[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(boolean[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(Collection value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            int index = 0;
            for (Iterator iterator = IteratorUtil.of(value); iterator.hasNext(); index++) {
                Object object = iterator.next();
                if (mode == StructureEnum.NONE && (mode = structureMode(object)) == StructureEnum.NONE) {
                    continue;
                }
                try {
                    putKeyValue(mode, result, object, index);
                } catch (NullPointerException e) {
                    continue;
                } catch (Exception e) {
                    index = -1;
                    mode = StructureEnum.DEFAULT;
                    result.clear();
                }
            }
            return (T) result;
        }

        void putKeyValue(StructureEnum mode, Map result, Object object, int index) {
            switch (mode) {
                case ENTRY:
                    Map.Entry entry = (Map.Entry) object;
                    result.put(entry.getKey(), entry.getValue());
                    break;
                case ARRAY_LENGTH_2:
                    result.put(Array.get(object, 0), Array.get(object, 1));
                    break;
                case LIST_SIZE_2:
                    List list = (List) object;
                    result.put(list.get(0), list.get(1));
                    break;
                default:
                    result.put(index, object);
                    break;
            }
        }

        StructureEnum structureMode(Object object) {
            if (object == null) {
                return StructureEnum.NONE;
            } else if (object instanceof Map.Entry) {
                return StructureEnum.ENTRY;
            } else if (object.getClass().isArray() && Array.getLength(object) == 2) {
                return StructureEnum.ARRAY_LENGTH_2;
            } else if (object instanceof List && ListUtil.size((List) object) == 2) {
                return StructureEnum.LIST_SIZE_2;
            } else {
                return StructureEnum.DEFAULT;
            }
        }

        <T> T createMap(Class mapImplClass) {
            return newInstance(isAbstract(mapImplClass), mapImplClass, HashMap::new);
        }
    }

    private final static <T> T[] createArray(Class<T> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    private final static boolean isAbstract(Class type) {
        if (type == null) { return true; }
        int modifier = type.getModifiers();
        return Modifier.isInterface(modifier) || Modifier.isAbstract(modifier);
    }

    private final static <T> T newInstance(boolean isDefault, Class type, Supplier supplier) {
        try {
            return (T) (isDefault ? supplier.get() : ConstructorUtil.newInstance(type));
        } catch (Exception e) {
            return (T) supplier.get();
        }
    }
}
