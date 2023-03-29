package com.moon.core.enums;

import com.moon.core.lang.BooleanUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.function.BiIntConsumer;

import java.util.HashMap;
import java.util.Iterator;

import static com.moon.core.lang.JoinerUtil.join;
import static com.moon.core.util.IteratorUtil.of;

/**
 * @author moonsky
 */
public enum Arrays2 implements ArrayOperator {
    /**
     * 普通数组
     */
    OBJECTS(new Object[0]) {
        @Override
        public Object apply(int value) { return new Object[value]; }

        @Override
        public Object[] to(Object o) { return (Object[]) o; }

        @Override
        public Iterator iterator(Object o) { return of(to(o)); }

        @Override
        public String stringify(Object o) { return join(to(o), getJoinOn()); }

        @Override
        public boolean test(Object data) { return data instanceof Object[]; }

        @Override
        public Object get(Object arr, int index) { return to(arr)[index]; }

        @Override
        public Object set(Object arr, int index, Object value) {
            Object[] data = to(arr);
            Object old = data[index];
            data[index] = value;
            return old;
        }

        @Override
        public Object[] create(int length) { return new Object[length]; }

        @Override
        public int length(Object arr) { return to(arr).length; }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            IteratorUtil.forEach(to(arr), consumer);
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null) {
                return false;
            }
            Object[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            if (item == null) {
                for (int i = 0; i < len; i++) {
                    if (data[i] == null) {
                        return true;
                    }
                }
            }
            for (int i = 0; i < len; i++) {
                if (item.equals(data[i])) {
                    return true;
                }
            }
            return false;
        }
    },
    STRINGS(new String[0]) {
        @Override
        public Object apply(int value) { return new String[value]; }

        @Override
        public String[] to(Object o) { return (String[]) o; }

        @Override
        public Iterator iterator(Object o) { return of(to(o)); }

        @Override
        public String stringify(Object o) { return join(to(o), getJoinOn()); }

        @Override
        public boolean test(Object data) { return data instanceof String[]; }

        @Override
        public Object get(Object arr, int index) { return to(arr)[index]; }

        @Override
        public String set(Object arr, int index, Object value) {
            String[] data = to(arr);
            String old = data[index];
            data[index] = value == null ? null : value.toString();
            return old;
        }

        @Override
        public String[] create(int length) { return new String[length]; }

        @Override
        public int length(Object arr) { return to(arr).length; }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            IteratorUtil.forEach(to(arr), consumer);
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof String)) {
                return false;
            }
            String[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            if (item == null) {
                for (int i = 0; i < len; i++) {
                    if (data[i] == null) {
                        return true;
                    }
                }
            }
            for (int i = 0; i < len; i++) {
                if (item.equals(data[i])) {
                    return true;
                }
            }
            return false;
        }
    },
    BOOLEANS(new boolean[0]) {
        @Override
        public Object apply(int value) { return new boolean[value]; }

        @Override
        public boolean[] to(Object o) { return (boolean[]) o; }

        @Override
        public Boolean[] toObjects(Object arr) {
            if (arr == null) {
                return null;
            }
            boolean[] old = to(arr);
            final int len = old.length;
            Boolean[] now = new Boolean[len];
            for (int i = 0; i < len; i++) {
                now[i] = old[i];
            }
            return now;
        }

        @Override
        public boolean[] toPrimitives(Object arr) {
            if (arr == null) { return null; }
            Boolean[] old = (Boolean[]) arr;
            final int len = old.length;
            boolean[] now = new boolean[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof boolean[];
        }

        @Override
        public Boolean get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Boolean set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Boolean);
            boolean[] data = to(arr);
            boolean old = data[index];
            data[index] = ((Boolean) value).booleanValue();
            return old;
        }

        @Override
        public boolean[] create(int length) {
            return new boolean[length];
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            boolean[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || item == null || !(item instanceof Boolean)) {
                return false;
            }
            boolean[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            if ((Boolean) item) {
                for (int i = 0; i < len; i++) {
                    if (data[i]) {
                        return true;
                    }
                }
            } else {
                for (int i = 0; i < len; i++) {
                    if (!data[i]) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    DOUBLES(new double[0]) {
        @Override
        public Object apply(int value) {
            return new double[value];
        }

        @Override
        public double[] to(Object o) {
            return (double[]) o;
        }

        @Override
        public Double[] toObjects(Object arr) {
            if (arr == null) { return null; }
            final int len = length(arr);
            Double[] now = new Double[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public double[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Double[] old = (Double[]) arr;
            final int len = old.length;
            double[] values = new double[len];
            for (int i = 0; i < len; i++) {
                values[i] = old[i];
            }
            return values;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof double[];
        }

        @Override
        public Double get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Double set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Number);
            double[] data = to(arr);
            double old = data[index];
            data[index] = ((Number) value).doubleValue();
            return old;
        }

        @Override
        public double[] create(int length) { return new double[length]; }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            double[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof Double)) {
                return false;
            }
            double[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            double value = (Double) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    },
    FLOATS(new float[0]) {
        @Override
        public Object apply(int value) {
            return new float[value];
        }

        @Override
        public float[] to(Object o) {
            return (float[]) o;
        }

        @Override
        public Float[] toObjects(Object arr) {
            if (arr == null) {
                return null;
            }
            final int len = to(arr).length;
            Float[] now = new Float[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public float[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Float[] old = (Float[]) arr;
            final int len = old.length;
            float[] values = new float[len];
            for (int i = 0; i < len; i++) {
                values[i] = old[i];
            }
            return values;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof float[];
        }

        @Override
        public Float get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Float set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Number);
            float[] data = to(arr);
            float old = data[index];
            data[index] = ((Number) value).floatValue();
            return old;
        }

        @Override
        public float[] create(int length) {
            return new float[length];
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            float[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof Float)) {
                return false;
            }
            float[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            float value = (Float) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    },
    LONGS(new long[0]) {
        @Override
        public Object apply(int value) {
            return new long[value];
        }

        @Override
        public long[] to(Object o) {
            return (long[]) o;
        }

        @Override
        public Long[] toObjects(Object arr) {
            if (arr == null) { return null; }
            long[] old = to(arr);
            final int len = old.length;
            Long[] now = new Long[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public long[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Long[] old = (Long[]) arr;
            final int len = old.length;
            long[] values = new long[len];
            for (int i = 0; i < len; i++) {
                values[i] = old[i];
            }
            return values;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof long[];
        }

        @Override
        public Long get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public long[] create(int length) {
            return new long[length];
        }

        @Override
        public Long set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Number);
            long[] data = to(arr);
            long old = data[index];
            data[index] = ((Number) value).longValue();
            return old;
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            long[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof Long)) {
                return false;
            }
            long[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            long value = (Long) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    },
    INTS(new int[0]) {
        @Override
        public Object apply(int value) {
            return new int[value];
        }

        @Override
        public int[] to(Object o) {
            return (int[]) o;
        }

        @Override
        public Integer[] toObjects(Object arr) {
            if (arr == null) { return null; }
            int[] old = to(arr);
            final int len = old.length;
            Integer[] now = new Integer[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public int[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Integer[] old = (Integer[]) arr;
            final int len = old.length;
            int[] values = new int[len];
            for (int i = 0; i < len; i++) {
                values[i] = old[i];
            }
            return values;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof int[];
        }

        @Override
        public Integer get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Integer set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Number);
            int[] data = to(arr);
            int old = data[index];
            data[index] = ((Number) value).intValue();
            return old;
        }

        @Override
        public int[] create(int length) {
            return new int[length];
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            int[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof Integer)) {
                return false;
            }
            int[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            int value = (Integer) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    },
    SHORTS(new short[0]) {
        @Override
        public Object apply(int value) {
            return new short[value];
        }

        @Override
        public short[] to(Object o) {
            return (short[]) o;
        }

        @Override
        public Short[] toObjects(Object arr) {
            if (arr == null) { return null; }
            short[] old = to(arr);
            final int len = old.length;
            Short[] now = new Short[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public short[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Short[] old = (Short[]) arr;
            final int len = old.length;
            short[] values = new short[len];
            for (int i = 0; i < len; i++) {
                values[i] = old[i];
            }
            return values;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof short[];
        }

        @Override
        public Short get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Short set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Number);
            short[] data = to(arr);
            short old = data[index];
            data[index] = ((Number) value).shortValue();
            return old;
        }

        @Override
        public short[] create(int length) {
            return new short[length];
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            short[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof Short)) {
                return false;
            }
            short[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            int value = (Short) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    },
    BYTES(new byte[0]) {
        @Override
        public Object apply(int value) {
            return new byte[value];
        }

        @Override
        public byte[] to(Object o) {
            return (byte[]) o;
        }

        @Override
        public Byte[] toObjects(Object arr) {
            if (arr == null) {
                return null;
            }
            byte[] old = to(arr);
            final int len = old.length;
            Byte[] now = new Byte[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public byte[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Byte[] old = (Byte[]) arr;
            final int len = old.length;
            byte[] values = new byte[len];
            for (int i = 0; i < len; i++) {
                values[i] = old[i];
            }
            return values;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return join(to(o), getJoinOn());
        }

        @Override
        public boolean test(Object data) {
            return data instanceof byte[];
        }

        @Override
        public Byte get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Byte set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Number);
            byte[] data = to(arr);
            byte old = data[index];
            data[index] = ((Number) value).byteValue();
            return old;
        }

        @Override
        public byte[] create(int length) {
            return new byte[length];
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            byte[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || !(item instanceof Byte)) {
                return false;
            }
            byte[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            int value = (Byte) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    },
    CHARS(new char[0]) {
        @Override
        public Object apply(int value) {
            return new char[value];
        }

        @Override
        public char[] to(Object o) {
            return (char[]) o;
        }

        @Override
        public Character[] toObjects(Object arr) {
            if (arr == null) { return null; }
            char[] old = to(arr);
            final int len = old.length;
            Character[] now = new Character[len];
            System.arraycopy(arr, 0, now, 0, len);
            return now;
        }

        @Override
        public char[] toPrimitives(Object arr) {
            if (arr == null) {
                return null;
            }
            Character[] characters = (Character[]) arr;
            final int len = characters.length;
            char[] chars = new char[len];
            for (int i = 0; i < len; i++) {
                chars[i] = characters[i];
            }
            return chars;
        }

        @Override
        public Iterator iterator(Object o) {
            return of(to(o));
        }

        @Override
        public String stringify(Object o) {
            return o == null ? null : new String(to(o));
        }

        @Override
        public boolean test(Object data) {
            return data instanceof char[];
        }

        @Override
        public Character get(Object arr, int index) {
            return to(arr)[index];
        }

        @Override
        public Character set(Object arr, int index, Object value) {
            BooleanUtil.requireTrue(value instanceof Character);
            char[] data = to(arr);
            char old = data[index];
            data[index] = (Character) value;
            return old;
        }

        @Override
        public char[] create(int length) {
            return new char[length];
        }

        @Override
        public int length(Object arr) {
            return to(arr).length;
        }

        @Override
        public void forEach(Object arr, BiIntConsumer consumer) {
            char[] array = to(arr);
            for (int i = 0, length = array.length; i < length; i++) {
                consumer.accept(array[i], i);
            }
        }

        @Override
        public boolean contains(Object arr, Object item) {
            if (arr == null || item == null || !(item instanceof Character)) {
                return false;
            }
            char[] data = to(arr);
            int len = data.length;
            if (len == 0) {
                return false;
            }
            char value = (Character) item;
            for (int i = 0; i < len; i++) {
                if (data[i] == value) {
                    return true;
                }
            }
            return false;
        }
    };

    static class Cached {

        final static HashMap<Class, ArrayOperator> CACHE = new HashMap<>();
    }

    public final Class TYPE;
    private final Object EMPTY;

    Arrays2(Object empty) {
        Cached.CACHE.put(TYPE = (this.EMPTY = empty).getClass(), this);
    }

    abstract <T> T to(Object o);

    private final static String getJoinOn() { return Const.EMPTY; }

    @Override
    public <T> T empty() {
        return (T) EMPTY;
    }

    public static ArrayOperator get(Class type) {
        return Cached.CACHE.get(type);
    }

    public static ArrayOperator getOrDefault(Class type, ArrayOperator defaultVal) {
        BooleanUtil.requireTrue(type.isArray());
        return Cached.CACHE.getOrDefault(type, defaultVal);
    }

    public static ArrayOperator getOrObjects(Class type) {
        return getOrDefault(type, OBJECTS);
    }

    public static ArrayOperator getOrDefault(Object array, ArrayOperator defaultType) {
        return getOrDefault(array.getClass(), defaultType);
    }

    public static ArrayOperator getOrObjects(Object array) {
        return getOrObjects(array.getClass());
    }
}
