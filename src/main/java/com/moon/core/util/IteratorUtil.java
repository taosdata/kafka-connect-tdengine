package com.moon.core.util;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldDescriptor;
import com.moon.core.enums.Arrays2;
import com.moon.core.enums.Collects;
import com.moon.core.io.FileUtil;
import com.moon.core.lang.EnumUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.function.*;
import com.moon.core.util.iterator.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static com.moon.core.io.FileUtil.getInputStream;
import static com.moon.core.lang.ThrowUtil.runtime;
import static com.moon.core.util.iterator.CollectSplitter.DEFAULT_SPLIT_COUNT;
import static com.moon.core.util.iterator.EmptyIterator.EMPTY;

/**
 * 通用迭代器
 * <p>
 * 【注意】：
 * <p>
 * 此类构造出来的大多数自定义迭代器没有实现{@link Iterator#remove()}方法，
 * <p>
 * 如果在运行是调用将会抛出{@link UnsupportedOperationException}异常；
 * <p>
 * 对于本身就是{@link Iterable}的集合，此类将原生调用其{@link Iterable#iterator()}方法，
 * <p>
 * 此时{@link Iterator#remove()}方法根据具体集合实现。
 *
 * @author moonsky
 */
public final class IteratorUtil {

    private IteratorUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 空迭代器
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Iterator ofEmpty() { return EMPTY; }

    /**
     * 集合反序迭代器
     *
     * @param collect 集合
     * @param <T>     集合项数据类型
     *
     * @return 倒序迭代器
     */
    public static <T> Iterator<T> ofReversed(Collection<? extends T> collect) { return ReverseIterator.of(collect); }


    /*
     * ----------------------------------------------------------------------------
     * array iterator: 数组迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 数组迭代器
     *
     * @param values 数组
     * @param <T>    数组项数据类型
     *
     * @return 迭代器
     */
    @SafeVarargs
    public static <T> Iterator<T> of(T... values) { return ObjectsIterator.of(values); }

    /**
     * 获取 byte[] 类型数组的迭代器
     *
     * @param values byte 数组
     *
     * @return 迭代器
     */
    public static Iterator<Byte> of(byte... values) { return BytesIterator.of(values); }

    /**
     * 获取 short[] 类型数组的迭代器
     *
     * @param values short 数组
     *
     * @return 迭代器
     */
    public static Iterator<Short> of(short... values) { return ShortsIterator.of(values); }

    /**
     * 获取 char[] 类型数组的迭代器
     *
     * @param values char 数组
     *
     * @return 迭代器
     */
    public static Iterator<Character> of(char... values) { return CharsIterator.of(values); }

    /**
     * 获取 int[] 类型数组的迭代器
     *
     * @param values int 数组
     *
     * @return 迭代器
     */
    public static Iterator<Integer> of(int... values) { return IntsIterator.of(values); }

    /**
     * 获取 long[] 类型数组的迭代器
     *
     * @param values long 数组
     *
     * @return 迭代器
     */
    public static Iterator<Long> of(long... values) { return LongsIterator.of(values); }

    /**
     * 获取 float[] 类型数组的迭代器
     *
     * @param values float 数组
     *
     * @return 迭代器
     */
    public static Iterator<Float> of(float... values) { return FloatsIterator.of(values); }

    /**
     * 获取 double[] 类型数组的迭代器
     *
     * @param values double 数组
     *
     * @return 迭代器
     */
    public static Iterator<Double> of(double... values) { return DoublesIterator.of(values); }

    /**
     * 获取 boolean[] 类型数组的迭代器
     *
     * @param values boolean 数组
     *
     * @return 迭代器
     */
    public static Iterator<Boolean> of(boolean... values) { return BooleansIterator.of(values); }

    /*
     * ----------------------------------------------------------------------------
     * string iterator: 字符串字符迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 返回字符串的字符迭代器
     *
     * @param value 字符串
     *
     * @return 迭代器
     */
    public static Iterator<Character> ofChars(CharSequence value) { return CharsIterator.of(value); }

    /*
     * ----------------------------------------------------------------------------
     * file line iterator: 文本行迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 返回文本文件行迭代器
     *
     * @param path
     */
    public static Iterator<String> ofLines(CharSequence path) { return path == null ? EMPTY : new LinesIterator(path); }

    /**
     * 从 Reader 中每次读取一行文本
     * <p>
     * 不会自动关闭流
     *
     * @param reader
     *
     * @return
     */
    public static Iterator<String> ofLines(Reader reader) { return reader == null ? EMPTY : new LinesIterator(reader); }

    /**
     * 从 InputStream 中按默认字符编码（UTF-8）格式每次读取一行文本
     * <p>
     * 不会自动关闭流
     *
     * @param is
     *
     * @return
     */
    public static Iterator<String> ofLines(InputStream is) { return is == null ? EMPTY : new LinesIterator(is); }

    /**
     * 从 InputStream 中按 charset 格式每次读取一行文本
     * <p>
     * 不会自动关闭流
     *
     * @param is
     * @param charset
     *
     * @return
     */
    public static Iterator<String> ofLines(InputStream is, String charset) {
        return is == null ? EMPTY : new LinesIterator(is, charset);
    }

    /**
     * 从 InputStream 中按 charset 格式每次读取一行文本
     * <p>
     * 不会自动关闭流
     *
     * @param is
     * @param charset
     *
     * @return
     */
    public static Iterator<String> ofLines(InputStream is, Charset charset) {
        return is == null ? EMPTY : new LinesIterator(is, charset);
    }

    /**
     * 获取一个文本文件读取迭代器，可用于常用的txt、json、xml等文本文件读取；
     * <p>
     * 迭代器每次返回一行数据，直到文本结尾，对象会自动关闭文件流
     *
     * @param file
     */
    public static Iterator<String> ofLines(File file) { return file == null ? EMPTY : new LinesIterator(file); }

    /*
     * ----------------------------------------------------------------------------
     * io iterator: I/O 迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 文件流迭代读取器 每次将读取的字节放入数组 buffer 中，并返回读取到的长度
     *
     * @param filepath
     * @param buffer
     */
    public static Iterator<Integer> of(String filepath, byte[] buffer) {
        return filepath == null ? EMPTY : new FileStreamIterator(filepath, buffer);
    }

    /**
     * 文件流迭代读取器 每次将读取的字节放入数组 buffer 中，并返回读取到的长度
     *
     * @param file
     * @param buffer
     */
    public static Iterator<Integer> of(File file, byte[] buffer) {
        return file == null ? EMPTY : new FileStreamIterator(file, buffer);
    }

    /**
     * 文件流迭代读取器 每次将读取的字节放入数组 buffer 中，并返回读取到的长度
     *
     * @param is
     * @param buffer
     */
    public static Iterator<Integer> of(InputStream is, byte[] buffer) {
        return is == null ? EMPTY : new FileStreamIterator(is, buffer);
    }

    /*
     * ----------------------------------------------------------------------------
     * java bean iterator: 实体字段迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 将 JavaBean 中每个属性认为是一个字段进行迭代
     *
     * @param object
     *
     * @return
     */
    public static Iterator<Map.Entry<String, FieldDescriptor>> ofFields(Object object) {
        return object == null ? EMPTY : of(BeanInfoUtil.getFieldDescriptorsMap(object.getClass()));
    }

    /*
     * ----------------------------------------------------------------------------
     * collection iterator: 集合迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 默认迭代器
     *
     * @param iterator
     * @param <T>
     *
     * @return
     */
    public static <T> Iterator<T> of(Iterator<T> iterator) { return iterator == null ? EMPTY : iterator; }

    /**
     * 返回 Iterable 集合的迭代器
     *
     * @param iterable
     * @param <T>
     */
    public static <T> Iterator<T> of(Iterable<T> iterable) { return iterable == null ? EMPTY : iterable.iterator(); }

    /**
     * 流迭代器
     *
     * @param stream 流
     * @param <T>    数据类型
     *
     * @return 迭代器
     */
    public static <T> Iterator<T> of(Stream<T> stream) { return stream == null ? EMPTY : stream.iterator(); }

    /**
     * 返回 Map 集合迭代器
     *
     * @param map
     * @param <K>
     * @param <V>
     */
    public static <K, V> Iterator<Map.Entry<K, V>> of(Map<K, V> map) {
        return map == null ? EMPTY : map.entrySet().iterator();
    }

    /**
     * 返回 Enumeration 迭代器
     *
     * @param enumeration
     * @param <T>
     */
    public static <T> Iterator<T> of(Enumeration<T> enumeration) {
        return enumeration == null ? EMPTY : new EnumerationIterator<>(enumeration);
    }

    /**
     * 返回 ResultSet 迭代器
     *
     * @param resultSet
     */
    public static Iterator<ResultSet> of(ResultSet resultSet) {
        return resultSet == null ? EMPTY : new ResultSetIterator(resultSet);
    }

    /*
     * ----------------------------------------------------------------------------
     * enum iterator: 枚举迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 返回类字段信息描述迭代器; 迭代器每次返回一个枚举；
     *
     * @param clazz
     * @param <T>
     */
    public static <T extends Enum<T>> Iterator<T> of(Class<T> clazz) {
        return clazz != null && clazz.isEnum() ? of(clazz.getEnumConstants()) : EMPTY;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Iterator ofAny(Object value) {
        if (value == null) { return EMPTY; }
        if (value instanceof Iterable) { return of((Iterable) value); }
        if (value instanceof Map) { return of((Map) value); }
        if (value instanceof Iterator) { return (Iterator) value; }
        Class type = value.getClass();
        if (type.isEnum()) { return of(type); }
        if (type.isArray()) {
            return Arrays2.getOrObjects(value).iterator(value);
        }
        if (value instanceof CharSequence) { return ofChars((CharSequence) value); }
        if (value instanceof Enumeration) { return of((Enumeration) value); }
        if (value instanceof ResultSet) { return of((ResultSet) value); }
        if (value instanceof File) { return ofLines((File) value); }
        return ofFields(value);
    }

    /**
     * 反序遍历
     *
     * @param collect
     * @param consumer
     * @param <E>
     */
    public final static <E> void forEachReversed(Collection<? extends E> collect, Consumer<? super E> consumer) {
        forEach(ofReversed(collect), consumer);
    }

    /*
     * ----------------------------------------------------------------------------
     * for each(object)
     * ----------------------------------------------------------------------------
     */

    public static void forEachAny(Object data, BiIntConsumer consumer) {
        if (data instanceof Iterable) {
            forEach((Iterable) data, consumer);
        } else if (data instanceof Map) {
            forEach(((Map) data).entrySet(), consumer);
        } else if (data instanceof Iterator) {
            forEach((Iterator) data, consumer);
        } else if (data == null) {
            return;
        } else if (data instanceof ResultSet) {
            forEach((ResultSet) data, consumer);
        } else if (data instanceof Stream) {
            forEach((Stream) data, consumer);
        } else {
            Class type = data.getClass();
            if (type.isArray()) {
                Arrays2.getOrObjects(data).forEach(data, consumer);
            } else if (type.isEnum()) {
                forEach(type, consumer);
            } else {
                forEachFields(data, consumer);
            }
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * for each(JavaBean)
     * ----------------------------------------------------------------------------
     */

    public static void forEachFields(Object bean, BiIntConsumer consumer) {
        if (bean != null) {
            IntAccessor indexer = IntAccessor.of();
            BeanInfoUtil.getFieldDescriptorsMap(bean.getClass()).forEach(
                (name, desc) -> consumer.accept(desc.getValueIfPresent(bean, true), indexer.getAndIncrement()));
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * array for each(item)
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(boolean[] array, BooleanConsumer consumer) {
        if (array != null) { for (boolean value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(double[] array, DoubleConsumer consumer) {
        if (array != null) { for (double value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(float[] array, FloatConsumer consumer) {
        if (array != null) { for (float value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(long[] array, LongConsumer consumer) {
        if (array != null) { for (long value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(int[] array, IntConsumer consumer) {
        if (array != null) { for (int value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(char[] array, CharConsumer consumer) {
        if (array != null) { for (char value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(short[] array, ShortConsumer consumer) {
        if (array != null) { for (short value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     *
     * @return
     */
    public static void forEach(byte[] array, ByteConsumer consumer) {
        if (array != null) { for (byte value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @param <T>
     */
    public static <T> void forEach(T[] array, Consumer<? super T> consumer) {
        if (array != null) { for (T value : array) { consumer.accept(value); } }
    }

    /**
     * 遍历枚举类
     *
     * @param enumType
     * @param consumer
     * @param <T>
     */
    public static <T extends Enum<T>> void forEach(Class<T> enumType, Consumer<? super T> consumer) {
        if (enumType != null) { forEach(EnumUtil.values(enumType), consumer); }
    }

    /*
     * ----------------------------------------------------------------------------
     * array for each count
     * ----------------------------------------------------------------------------
     */

    public static void forEach(final int count, IntConsumer consumer) {
        for (int i = 0; i < count; i++) { consumer.accept(i); }
    }

    /*
     * ----------------------------------------------------------------------------
     * array for each(item, index)
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     *
     * @return
     */
    public static void forEach(int[] array, IntIntConsumer consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     *
     * @return
     */
    public static void forEach(long[] array, LongIntConsumer consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     *
     * @return
     */
    public static void forEach(double[] array, DoubleIntConsumer consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     *
     * @return
     */
    public static <T> void forEach(T[] array, BiIntConsumer<? super T> consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历枚举类
     *
     * @param enumType
     * @param consumer
     * @param <T>
     */
    public static <T extends Enum<T>> void forEach(Class<T> enumType, BiIntConsumer<? super T> consumer) {
        if (enumType != null) { forEach(EnumUtil.values(enumType), consumer); }
    }

    /*
     * ----------------------------------------------------------------------------
     * collect for each
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历 Iterable
     *
     * @param consumer 处理对象
     * @param c
     * @param <T>
     *
     * @return
     */
    public static <T> void forEach(Iterable<T> c, Consumer<? super T> consumer) {
        if (c != null) { c.forEach(consumer); }
    }

    /**
     * 集合索引遍历
     *
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void forEach(Iterable<T> list, BiIntConsumer<? super T> consumer) {
        if (list != null) {
            int i = 0;
            for (T item : list) {
                consumer.accept(item, i);
                i++;
            }
        }
    }

    /**
     * 遍历
     *
     * @param iterator
     * @param consumer
     * @param <T>
     */
    public static <T> void forEach(Iterator<T> iterator, Consumer<? super T> consumer) {
        if (iterator != null) { iterator.forEachRemaining(consumer); }
    }

    /**
     * 遍历处理迭代器，包含索引
     *
     * @param iterator 迭代器
     * @param consumer 处理器
     * @param <T>      数据类型
     */
    public static <T> void forEach(Iterator<T> iterator, BiIntConsumer<? super T> consumer) {
        if (iterator != null) {
            for (int i = 0; iterator.hasNext(); i++) {
                consumer.accept(iterator.next(), i);
            }
        }
    }

    /**
     * 遍历处理迭代流
     *
     * @param stream   流
     * @param consumer 处理器
     * @param <T>      数据类型
     */
    public static <T> void forEach(Stream<T> stream, Consumer<T> consumer) {
        if (stream != null) {
            forEach(stream.iterator(), consumer);
        }
    }

    /**
     * 遍历处理流，可获得索引
     *
     * @param stream   流
     * @param consumer 处理器
     * @param <T>      数据类型
     */
    public static <T> void forEach(Stream<T> stream, BiIntConsumer<T> consumer) {
        if (stream != null) {
            forEach(stream.iterator(), consumer);
        }
    }

    /**
     * 遍历 Map
     *
     * @param consumer 处理对象
     * @param map
     * @param <K>
     * @param <V>
     *
     * @return
     */
    public static <K, V> void forEach(Map<K, V> map, Consumer<? super Map.Entry<K, V>> consumer) {
        if (map != null) { map.entrySet().forEach(consumer); }
    }

    /**
     * 遍历 Map
     *
     * @param consumer 处理对象
     * @param map
     * @param <K>
     * @param <V>
     *
     * @return
     */
    public static <K, V> void forEach(Map<K, V> map, BiConsumer<? super K, ? super V> consumer) {
        if (map != null) { map.forEach(consumer); }
    }

    /**
     * 遍历 Enumeration
     *
     * @param consumer 处理对象
     * @param e
     * @param <T>
     *
     * @return
     */
    public static <T> void forEach(Enumeration<T> e, Consumer<? super T> consumer) {
        if (e != null) { while (e.hasMoreElements()) { consumer.accept(e.nextElement()); } }
    }

    /**
     * 遍历 ResultSet
     *
     * @param resultSet
     * @param consumer
     */
    public static void forEach(ResultSet resultSet, Consumer<? super ResultSet> consumer) {
        if (resultSet != null) { forEach(of(resultSet), consumer); }
    }

    /**
     * 遍历 ResultSet
     *
     * @param resultSet
     * @param consumer
     */
    public static void forEach(ResultSet resultSet, BiIntConsumer<? super ResultSet> consumer) {
        if (resultSet != null) { forEach(of(resultSet), consumer); }
    }

    /*
     * ----------------------------------------------------------------------------
     * string for each
     * ----------------------------------------------------------------------------
     */

    public static void forEachChars(CharSequence cs, CharConsumer consumer) {
        if (cs != null) { forEach(cs.toString().toCharArray(), consumer); }
    }

    /*
     * ----------------------------------------------------------------------------
     * io for each
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历处理文本文件每一行数据
     *
     * @param consumer 处理对象
     * @param file
     */
    public static void forEachLines(File file, Consumer<String> consumer) {
        ofLines(file).forEachRemaining(consumer);
    }

    public static void forEachLines(String filename, Consumer<String> consumer) {
        ofLines(filename).forEachRemaining(consumer);
    }

    public static void forEachLines(Reader reader, Consumer<String> consumer) {
        ofLines(reader).forEachRemaining(consumer);
    }

    /**
     * 文件流读取和处理
     *
     * @param consumer 处理对象，接受一个参数，代表每次读取 byte 长度
     * @param filepath
     * @param buffer
     */
    public static void forEach(String filepath, byte[] buffer, IntConsumer consumer) {
        forEach(FileUtil.getInputStream(filepath), buffer, consumer);
    }

    /**
     * 文件流读取和处理
     *
     * @param consumer 处理对象，接受一个参数，代表每次读取 byte 长度
     * @param file
     * @param buffer
     */
    public static void forEach(File file, byte[] buffer, IntConsumer consumer) {
        forEach(getInputStream(file), buffer, consumer);
    }

    public static void forEach(Reader reader, char[] buffer, Consumer<Integer> consumer) {
        if (reader != null) {
            try {
                int length = buffer.length, limit;
                boolean hasValue;
                do {
                    limit = reader.read(buffer, 0, length);
                    if (hasValue = limit >= 0) {
                        consumer.accept(limit);
                    }
                } while (hasValue);
            } catch (IOException e) {
                runtime(e);
            }
        }
    }

    /**
     * 流读取和处理
     *
     * @param consumer 处理对象
     * @param input
     * @param buffer
     */
    public static void forEach(InputStream input, byte[] buffer, IntConsumer consumer) {
        if (input != null) {
            try {
                final int length = buffer.length;
                boolean whiling;
                int limit;
                do {
                    limit = input.read(buffer, 0, length);
                    if (whiling = (limit >= 0)) {
                        consumer.accept(limit);
                    }
                } while (whiling);
            } catch (IOException e) {
                runtime(e);
            }
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * split
     * ----------------------------------------------------------------------------
     */

    /**
     * 集合拆分器
     * <p>
     * 默认拆分后每个容器里有十六个元素
     *
     * @param c
     * @param <E>
     * @param <C>
     *
     * @return
     */
    public static <E, C extends Collection<E>> Iterator<C> split(C c) { return split(c, DEFAULT_SPLIT_COUNT); }

    /**
     * 集合拆分器
     * <p>
     * 将集合拆分成指定大小的若干个相同类型集合;
     * <p>
     * 不足个数的统一放入最后一个集合
     * <p>
     * 默认拆分后每个容器里有十六个元素
     *
     * @param c
     * @param size 指定拆分大小（拆分后每个集合元素数量）
     * @param <E>
     * @param <C>
     *
     * @return
     */
    public static <E, C extends Collection<E>> Iterator<C> split(C c, int size) {
        return c == null ? EMPTY : new CollectSplitter<>(c, size);
    }

    /**
     * 集合拆分处理器
     *
     * @param c
     * @param size     指定拆分大小（拆分后每个集合元素数量）
     * @param consumer
     * @param <E>
     * @param <C>
     */
    public static <E, C extends Collection<E>> void splitter(C c, int size, Consumer<? super C> consumer) {
        split(c, size).forEachRemaining(consumer);
    }

    /**
     * 集合拆分处理器
     *
     * @param c
     * @param consumer
     * @param <E>
     * @param <C>
     */
    public static <E, C extends Collection<E>> void splitter(C c, Consumer<? super C> consumer) {
        split(c).forEachRemaining(consumer);
    }

    /*
     * ----------------------------------------------------------------------------
     * group by
     * ----------------------------------------------------------------------------
     */

    /**
     * 集合分组
     *
     * @param list     集合
     * @param function 分组键
     * @param <K>      键类型
     * @param <E>      集合单项类型
     * @param <L>      List 类型
     *
     * @return
     */
    public static <K, E, L extends List<E>> Map<K, List<E>> groupBy(L list, Function<? super E, ? extends K> function) {
        return GroupUtil.groupAsList(list, function);
    }

    public static <K, E, S extends Set<E>> Map<K, Set<E>> groupBy(S set, Function<? super E, ? extends K> function) {
        return GroupUtil.groupAsSet(set, function);
    }

    public static <K, E, C extends Collection<E>> Map<K, Collection<E>> groupBy(
        C collect, Function<? super E, ? extends K> function
    ) { return GroupUtil.groupBy(collect, function); }

    public static <K, E, C extends Collection<E>, CR extends Collection<E>>

    Map<K, CR> groupBy(C collect, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier) {
        return GroupUtil.groupBy(collect, function, groupingSupplier);
    }

    /*
     * ----------------------------------------------------------------------------
     * filter
     * ----------------------------------------------------------------------------
     */

    /**
     * @param list
     * @param tester
     * @param <E>
     * @param <L>
     *
     * @return
     */
    public static <E, L extends List<E>> List<E> filter(L list, Predicate<? super E> tester) {
        return FilterUtil.filter(list, tester);
    }

    public static <E, S extends Set<E>> Set<E> filter(S set, Predicate<? super E> tester) {
        return FilterUtil.filter(set, tester);
    }

    /**
     * @param collect
     * @param tester
     * @param resultContainerSupplier 符合过滤条件项容器构造器
     * @param <E>
     * @param <C>
     * @param <CR>
     *
     * @return
     */
    public static <E, C extends Collection<E>, CR extends Collection<E>>

    CR filter(C collect, Predicate<? super E> tester, Supplier<CR> resultContainerSupplier) {
        return FilterUtil.filter(collect, tester, resultContainerSupplier);
    }

    /**
     * @param collect
     * @param tester
     * @param toResultContainer 符合过滤条件的容器
     * @param <E>
     * @param <C>
     * @param <CR>
     *
     * @return 返回提供的容器 toResultContainer
     */
    public static <E, C extends Collection<E>, CR extends Collection<E>>

    CR filter(C collect, Predicate<? super E> tester, CR toResultContainer) {
        return FilterUtil.filter(collect, tester, toResultContainer);
    }

    /*
     * ----------------------------------------------------------------------------
     * map
     * ----------------------------------------------------------------------------
     */

    public static <E, T, L extends List<E>> List<T> map(L list, Function<? super E, T> function) {
        final IntFunction supplier = Collects.deduceOrDefault(list, Collects.ArrayLists);
        return (List) mapTo(list, function, supplier);
    }

    public static <E, T, S extends Set<E>> Set<T> map(S set, Function<? super E, T> function) {
        final IntFunction supplier = Collects.deduceOrDefault(set, Collects.HashSets);
        return (Set) mapTo(set, function, supplier);
    }

    public static <E, T, C extends Collection<E>> Collection<T> map(C collect, Function<? super E, T> function) {
        final IntFunction supplier = Collects.deduceOrDefault(collect, Collects.ArrayLists);
        return mapTo(collect, function, supplier);
    }

    public static <E, T, C extends Collection<E>, CR extends Collection<T>>

    CR mapTo(C collect, Function<? super E, T> function, IntFunction<CR> containerSupplier) {
        return mapTo(collect, function, containerSupplier.apply(collect == null ? 0 : collect.size()));
    }

    public static <E, T, C extends Collection<E>, CR extends Collection<T>>

    CR mapTo(C collect, Function<? super E, T> function, CR container) {
        if (collect != null) {
            for (E item : collect) { container.add(function.apply(item)); }
        }
        return container;
    }
}
