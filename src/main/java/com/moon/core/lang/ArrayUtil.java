package com.moon.core.lang;

import com.moon.core.util.CollectUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.function.BiIntConsumer;
import com.moon.core.util.function.BiIntFunction;
import com.moon.core.util.function.TableIntConsumer;
import com.moon.core.util.function.TableIntFunction;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ArrayUtil {

    private ArrayUtil() { noInstanceError(); }

    private static <T> T ifNonNull(T value, Consumer<T> consumer) {
        if (value != null) { consumer.accept(value); }
        return value;
    }

    public static String stringify(Object arr) {
        if (arr == null) { return null; }
        if (arr instanceof Object[]) {return Arrays.deepToString((Object[]) arr);}
        if (arr instanceof int[]) {return Arrays.toString((int[]) arr);}
        if (arr instanceof long[]) {return Arrays.toString((long[]) arr);}
        if (arr instanceof double[]) {return Arrays.toString((double[]) arr);}
        if (arr instanceof byte[]) {return Arrays.toString((byte[]) arr);}
        if (arr instanceof char[]) {return Arrays.toString((char[]) arr);}
        if (arr instanceof short[]) {return Arrays.toString((short[]) arr);}
        if (arr instanceof boolean[]) {return Arrays.toString((boolean[]) arr);}
        return arr.toString();
    }

    /*
     * ----------------------------------------------------------------
     * with
     * ----------------------------------------------------------------
     */

    @SafeVarargs
    public static <T> T[] toArray(T... values) { return values; }

    public static char[] toArray(char... values) { return values; }

    public static byte[] toArray(byte... values) { return values; }

    public static short[] toArray(short... values) { return values; }

    public static int[] toArray(int... values) { return values; }

    public static long[] toArray(long... values) { return values; }

    public static float[] toArray(float... values) { return values; }

    public static double[] toArray(double... values) { return values; }

    public static boolean[] toArray(boolean... values) { return values; }

    /*
     * ----------------------------------------------------------------
     * sort
     * ----------------------------------------------------------------
     */

    @SafeVarargs
    public static <T> T[] sort(T... values) { return ifNonNull(values, Arrays::sort); }

    public static char[] sort(char... values) { return ifNonNull(values, Arrays::sort); }

    public static byte[] sort(byte... values) { return ifNonNull(values, Arrays::sort); }

    public static short[] sort(short... values) { return ifNonNull(values, Arrays::sort); }

    public static int[] sort(int... values) { return ifNonNull(values, Arrays::sort); }

    public static long[] sort(long... values) { return ifNonNull(values, Arrays::sort); }

    public static float[] sort(float... values) { return ifNonNull(values, Arrays::sort); }

    public static double[] sort(double... values) { return ifNonNull(values, Arrays::sort); }

    /*
     * ----------------------------------------------------------------
     * sort
     * ----------------------------------------------------------------
     */

    @SafeVarargs
    public static <T> T[] sort(Comparator<? super T> comparator, T... values) {
        if (values != null) { Arrays.sort(values, comparator); }
        return values;
    }

    /*
     * ----------------------------------------------------------------
     * reverse
     * ----------------------------------------------------------------
     */

    @SuppressWarnings("all")
    public static <T> T[] reverse(T... values) {
        if (values == null) {
            return null;
        }
        T[] items = values;
        T cache;
        for (int i = 0, two = 2, len = items.length, half = len-- / two; i < half; i++) {
            cache = items[len - i];
            items[len - i] = items[i];
            items[i] = cache;
        }
        return items;
    }

    @SuppressWarnings("all")
    public static char[] reverse(char... values) {
        if (values == null) {
            return null;
        }
        char[] items = values;
        char cache;
        for (int i = 0, two = 2, len = items.length, half = len-- / two; i < half; i++) {
            cache = items[len - i];
            items[len - i] = items[i];
            items[i] = cache;
        }
        return items;
    }

    @SuppressWarnings("all")
    public static byte[] reverse(byte... values) {
        if (values == null) {
            return null;
        }
        byte[] items = values;
        byte cache;
        for (int i = 0, two = 2, len = items.length, half = len-- / two; i < half; i++) {
            cache = items[len - i];
            items[len - i] = items[i];
            items[i] = cache;
        }
        return items;
    }

    @SuppressWarnings("all")
    public static int[] reverse(int... values) {
        if (values == null) {
            return null;
        }
        int[] items = values;
        int cache;
        for (int i = 0, two = 2, len = items.length, half = len-- / two; i < half; i++) {
            cache = items[len - i];
            items[len - i] = items[i];
            items[i] = cache;
        }
        return items;
    }

    @SuppressWarnings("all")
    public static long[] reverse(long... values) {
        if (values == null) {
            return null;
        }
        long[] items = values;
        long cache;
        for (int i = 0, two = 2, len = items.length, half = len-- / two; i < half; i++) {
            cache = items[len - i];
            items[len - i] = items[i];
            items[i] = cache;
        }
        return items;
    }

    @SuppressWarnings("all")
    public static double[] reverse(double... values) {
        if (values == null) {
            return null;
        }
        double[] items = values;
        double cache;
        for (int i = 0, two = 2, len = items.length, half = len-- / two; i < half; i++) {
            cache = items[len - i];
            items[len - i] = items[i];
            items[i] = cache;
        }
        return items;
    }

    /*
     * ----------------------------------------------------------------
     * get array type
     * ----------------------------------------------------------------
     */

    public static Class getArrayType(Class componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    /*
     * ----------------------------------------------------------------
     * length
     * ----------------------------------------------------------------
     */

    public static int length(Object[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(boolean[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(double[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(float[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(long[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(int[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(short[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(byte[] arr) { return arr == null ? 0 : arr.length; }

    public static int length(char[] arr) { return arr == null ? 0 : arr.length; }

    @SafeVarargs
    public static <T> boolean isEmpty(T... arr) { return length(arr) == 0; }

    public static boolean isEmpty(double... arr) { return length(arr) == 0; }

    public static boolean isEmpty(long... arr) { return length(arr) == 0; }

    public static boolean isEmpty(int... arr) { return length(arr) == 0; }

    public static boolean isEmpty(byte... arr) { return length(arr) == 0; }

    public static boolean isEmpty(char... arr) { return length(arr) == 0; }

    public static boolean isEmpty(float... arr) { return length(arr) == 0; }

    public static boolean isEmpty(short... arr) { return length(arr) == 0; }

    public static boolean isEmpty(boolean... arr) { return length(arr) == 0; }

    @SafeVarargs
    public static <T> boolean isNotEmpty(T... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(double... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(long... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(int... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(byte... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(char... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(float... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(short... arr) { return length(arr) > 0; }

    public static boolean isNotEmpty(boolean... arr) { return length(arr) > 0; }

    /*
     * ----------------------------------------------------------------
     * fill
     * ----------------------------------------------------------------
     */

    public static <T> T[] fill(T[] arr, T value) { return fillBegin(arr, 0, value); }

    public static boolean[] fill(boolean[] arr, boolean value) { return fillBegin(arr, 0, value); }

    public static double[] fill(double[] arr, double value) { return fillBegin(arr, 0, value); }

    public static float[] fill(float[] arr, float value) { return fillBegin(arr, 0, value); }

    public static long[] fill(long[] arr, long value) { return fillBegin(arr, 0, value); }

    public static int[] fill(int[] arr, int value) { return fillBegin(arr, 0, value); }

    public static short[] fill(short[] arr, short value) { return fillBegin(arr, 0, value); }

    public static byte[] fill(byte[] arr, byte value) { return fillBegin(arr, 0, value); }

    public static char[] fill(char[] arr, char value) { return fillBegin(arr, 0, value); }

    /*
     * ----------------------------------------------------------------
     * fill from
     * ----------------------------------------------------------------
     */

    public static <T> T[] fillBegin(T[] arr, int fromIndex, T value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static boolean[] fillBegin(boolean[] arr, int fromIndex, boolean value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static double[] fillBegin(double[] arr, int fromIndex, double value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static float[] fillBegin(float[] arr, int fromIndex, float value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static long[] fillBegin(long[] arr, int fromIndex, long value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static int[] fillBegin(int[] arr, int fromIndex, int value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static short[] fillBegin(short[] arr, int fromIndex, short value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static byte[] fillBegin(byte[] arr, int fromIndex, byte value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    public static char[] fillBegin(char[] arr, int fromIndex, char value) {
        return fill(arr, fromIndex, length(arr), value);
    }

    /*
     * ----------------------------------------------------------------
     * fill to； 纯粹为了一个返回值
     * ----------------------------------------------------------------
     */

    public static <T> T[] fill(T[] arr, int fromIndex, int toIndex, T value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static boolean[] fill(boolean[] arr, int fromIndex, int toIndex, boolean value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static double[] fill(double[] arr, int fromIndex, int toIndex, double value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static float[] fill(float[] arr, int fromIndex, int toIndex, float value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static long[] fill(long[] arr, int fromIndex, int toIndex, long value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static int[] fill(int[] arr, int fromIndex, int toIndex, int value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static short[] fill(short[] arr, int fromIndex, int toIndex, short value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static byte[] fill(byte[] arr, int fromIndex, int toIndex, byte value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    public static char[] fill(char[] arr, int fromIndex, int toIndex, char value) {
        Arrays.fill(arr, fromIndex, toIndex, value);
        return arr;
    }

    /*
     * ----------------------------------------------------------------
     * remove index, 删除指定位置数据，后面的数据前移一位； 返回原数组
     * ----------------------------------------------------------------
     */

    public static <T> T[] remove(T[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = null;
        return arr;
    }

    public static boolean[] remove(boolean[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = false;
        return arr;
    }

    public static double[] remove(double[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static float[] remove(float[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static long[] remove(long[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static int[] remove(int[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static short[] remove(short[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static byte[] remove(byte[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    public static char[] remove(char[] arr, int index) {
        arr[clearIdxAndGetLastIdx(arr, arr.length, index)] = 0;
        return arr;
    }

    private static int clearIdxAndGetLastIdx(Object arr, int length, int index) {
        int lastIndex = length - 1;
        System.arraycopy(arr, index + 1, arr, index, lastIndex - index);
        return lastIndex;
    }

    /*
     * ----------------------------------------------------------------
     * splice : 总是返回一个新数组
     * ----------------------------------------------------------------
     */

    public static <T> T[] splice(T[] arr, int fromIndex, int count, T... elements) {
        return (T[]) spliceArray(arr,
            arr.length,
            elements,
            elements.length,
            fromIndex,
            count,
            l -> Array.newInstance(arr.getClass().getComponentType(), l));
    }

    public static boolean[] splice(boolean[] arr, int fromIndex, int count, boolean... elements) {
        return (boolean[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, boolean[]::new);
    }

    public static char[] splice(char[] arr, int fromIndex, int count, char... elements) {
        return (char[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, char[]::new);
    }

    public static byte[] splice(byte[] arr, int fromIndex, int count, byte... elements) {
        return (byte[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, byte[]::new);
    }

    public static short[] splice(short[] arr, int fromIndex, int count, short... elements) {
        return (short[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, short[]::new);
    }

    public static int[] splice(int[] arr, int fromIndex, int count, int... elements) {
        return (int[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, int[]::new);
    }

    public static long[] splice(long[] arr, int fromIndex, int count, long... elements) {
        return (long[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, long[]::new);
    }

    public static float[] splice(float[] arr, int fromIndex, int count, float... elements) {
        return (float[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, float[]::new);
    }

    public static double[] splice(double[] arr, int fromIndex, int count, double... elements) {
        return (double[]) spliceArray(arr, arr.length, elements, elements.length, fromIndex, count, double[]::new);
    }

    private static Object spliceArray(
        Object arr, int arrLen, Object elements, int elementsLen, int fromIndex, int count, IntFunction creator
    ) {
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("Invalid value of fromIndex: " + count);
        }
        if (count < 0) {
            throw new ArrayIndexOutOfBoundsException("Invalid value of count: " + count);
        }
        Object value = creator.apply(arrLen + elementsLen - count);
        System.arraycopy(arr, 0, value, 0, fromIndex);
        System.arraycopy(elements, 0, value, fromIndex, elementsLen);
        if ((count = fromIndex + count) < arrLen) {
            System.arraycopy(arr, count, value, fromIndex + elementsLen, arrLen - count);
        }
        return value;
    }

    /*
     * ----------------------------------------------------------------
     * to primitives
     * ----------------------------------------------------------------
     */

    public static boolean[] toPrimitives(Boolean[] value) { return transformArray(value, boolean[]::new); }

    public static char[] toPrimitives(Character[] value) { return transformArray(value, char[]::new); }

    public static byte[] toPrimitives(Byte[] value) { return transformArray(value, byte[]::new); }

    public static short[] toPrimitives(Short[] value) { return transformArray(value, short[]::new); }

    public static int[] toPrimitives(Integer[] value) { return transformArray(value, int[]::new); }

    public static long[] toPrimitives(Long[] value) { return transformArray(value, long[]::new); }

    public static float[] toPrimitives(Float[] value) { return transformArray(value, float[]::new); }

    public static double[] toPrimitives(Double[] value) { return transformArray(value, double[]::new); }

    /*
     * ----------------------------------------------------------------
     * to objects
     * ----------------------------------------------------------------
     */

    public static Boolean[] toObjects(boolean[] value) { return transformArray(value, Boolean[]::new); }

    public static Character[] toObjects(char[] value) { return transformArray(value, Character[]::new); }

    public static Byte[] toObjects(byte[] value) { return transformArray(value, Byte[]::new); }

    public static Short[] toObjects(short[] value) { return transformArray(value, Short[]::new); }

    public static Integer[] toObjects(int[] value) { return transformArray(value, Integer[]::new); }

    public static Long[] toObjects(long[] value) { return transformArray(value, Long[]::new); }

    public static Float[] toObjects(float[] value) { return transformArray(value, Float[]::new); }

    public static Double[] toObjects(double[] value) { return transformArray(value, Double[]::new); }

    private static <T> T transformArray(Object value, IntFunction<T> creator) {
        if (value == null) { return null; }
        final int length = Array.getLength(value);
        T arr = creator.apply(length);
        System.arraycopy(value, 0, arr, 0, length);
        return arr;
    }

    public static Object[] toObjectArray(Object value) {
        if (value == null || value instanceof Object[]) {
            return (Object[]) value;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            Object[] result = new Object[length];
            System.arraycopy(value, 0, result, 0, length);
            return result;
        }
        Collection collect = null;
        if (value instanceof Iterable) {
            if (value instanceof Collection) {
                collect = (Collection) value;
            } else {
                collect = ListUtil.newList((Iterable) value);
            }
        } else if (value instanceof Iterator) {
            collect = ListUtil.newList((Iterator) value);
        } else if (value instanceof Stream) {
            collect = (Collection) ((Stream) value).collect(Collectors.toList());
        }
        if (collect != null) {
            return CollectUtil.toArray(collect, Object[]::new);
        }
        Object[] objects = new Object[1];
        objects[0] = value;
        return objects;
    }

    @SafeVarargs
    public static <D, R> List<R> mapBy(Function<? super D, ? extends R> mapper, D... dataArr) {
        List<R> result = new ArrayList<>();
        int length = length(dataArr);
        for (int i = 0; i < length; i++) {
            result.add(mapper.apply(dataArr[i]));
        }
        return result;
    }

    /*
     * 属性求和
     */

    public static <T> int sum(T[] arr, ToIntFunction<T> getter) {
        int length = length(arr), total = 0;
        for (int i = 0; i < length; i++) {
            total += getter.applyAsInt(arr[i]);
        }
        return total;
    }

    public static <T> long sumAsLong(T[] arr, ToLongFunction<T> getter) {
        int length = length(arr);
        long total = 0;
        for (int i = 0; i < length; i++) {
            total += getter.applyAsLong(arr[i]);
        }
        return total;
    }

    public static <T> double sumAsDouble(T[] arr, ToDoubleFunction<T> getter) {
        int length = length(arr);
        double total = 0;
        for (int i = 0; i < length; i++) {
            total += getter.applyAsDouble(arr[i]);
        }
        return total;
    }

    /*
     reduce
     */

    /**
     * 聚合函数，参照 JavaScript 中 Array.reduce(..) 实现
     * <pre>
     * 1. 接受一个数组作为源数据；
     * 2. 一个处理器，处理器接收两个参数（总值, 当前项）；
     *       其中当前项也是索引，索引相对{@link #reduce(int, BiIntFunction, Object)}少一个参数
     * 3. 初始值，作为第一次传入处理器的参数，也是最后返回结果
     * </pre>
     *
     * @param count      迭代次数
     * @param reducer    聚合函数
     * @param totalValue 返回结果
     * @param <T>        返回值类型
     *
     * @return 返回最后一项处理完后的结果
     *
     * @see #reduce(Object[], TableIntFunction, Object)
     * @see CollectUtil#reduce(Iterable, TableIntFunction, Object)
     * @see CollectUtil#reduce(Iterator, TableIntFunction, Object)
     */
    public static <T> T reduce(int count, BiIntFunction<? super T, T> reducer, T totalValue) {
        return IntUtil.reduce(count, reducer, totalValue);
    }

    /**
     * 聚合函数，参照 JavaScript 中 Array.reduce(..) 实现
     * <pre>
     * 1. 接受一个数组作为源数据；
     * 2. 一个处理器，处理器接收三个参数（总值, 当前项, 索引），返回值作为下一次的总值，最终返回值是函数返回值；
     * 3. 初始值，作为第一次传入处理器的参数，也是最后返回结果
     * </pre>
     *
     * @param arr        入参数组
     * @param reducer    聚合函数
     * @param totalValue 返回结果
     * @param <T>        返回值类型
     * @param <E>        迭代器单项数据类型
     *
     * @return 返回最后一项处理完后的结果
     *
     * @see #reduce(int, BiIntFunction, Object)
     * @see CollectUtil#reduce(Iterable, TableIntFunction, Object)
     * @see CollectUtil#reduce(Iterator, TableIntFunction, Object)
     */
    public static <T, E> T reduce(E[] arr, TableIntFunction<? super T, ? super E, T> reducer, T totalValue) {
        if (arr != null) {
            for (int i = 0, len = arr.length; i < len; i++) {
                totalValue = reducer.apply(totalValue, arr[i], i);
            }
        }
        return totalValue;
    }
}
