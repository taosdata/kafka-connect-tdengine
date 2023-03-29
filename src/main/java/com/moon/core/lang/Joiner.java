package com.moon.core.lang;

import com.moon.core.enums.Const;
import com.moon.core.model.supplier.ValueSupplier;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.core.util.CollectUtil.size;
import static com.moon.core.util.IteratorUtil.forEach;

/**
 * @author moonsky
 */
public class Joiner
    implements Supplier<String>, ValueSupplier<String>, Appendable, CharSequence, Cloneable, Serializable {

    private static final long serialVersionUID = 6428348081105594320L;

    private final static String EMPTY = Const.EMPTY;
    private final static int DFT_LEN = 16;

    private Function<Object, String> stringifier;

    private String prefix;
    private String delimiter;
    private String suffix;

    private String useForNull;
    private boolean requireNonNull;

    private StringBuilder container;

    private int itemCount;

    private final static Function defaultStringifier() { return String::valueOf; }

    private static final String emptyIfNull(CharSequence str) { return str == null ? EMPTY : str.toString(); }

    /*
     * -------------------------------------------------------
     * constructor
     * -------------------------------------------------------
     */

    public Joiner(CharSequence delimiter) { this(delimiter, null, null); }

    public Joiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        this(defaultStringifier(), prefix, delimiter, suffix, Const.STR_NULL, false, prefix);
    }

    private Joiner(
        Function<Object, String> stringifier,
        CharSequence prefix,
        CharSequence delimiter,
        CharSequence suffix,
        CharSequence useForNull,
        boolean requireNonNull,
        CharSequence value
    ) {
        this.useForNull = useForNull == null ? null : useForNull.toString();
        this.setStringifier(stringifier)
            .setPrefix(prefix)
            .setDelimiter(delimiter)
            .setSuffix(suffix)
            .requireNonNull(requireNonNull)
            .clear(value);
    }

    public final static Joiner of(CharSequence delimiter) {return new Joiner(delimiter);}

    public final static Joiner of(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return new Joiner(delimiter, prefix, suffix);
    }

    /*
     * -------------------------------------------------------
     * getter and setter
     * -------------------------------------------------------
     */

    public Joiner skipNulls() {
        this.useForNull = null;
        return this;
    }

    public Joiner useEmptyForNull() { return useForNull(EMPTY); }

    public Joiner useForNull(CharSequence cs) {
        this.useForNull = String.valueOf(cs);
        return this;
    }

    public Joiner requireNonNull() { return requireNonNull(true); }

    public Joiner requireNonNull(boolean requireNonNull) {
        this.requireNonNull = requireNonNull;
        return this;
    }

    public Joiner setDefaultStringifier() { return setStringifier(defaultStringifier()); }

    public Joiner setStringifier(Function<Object, String> stringifier) {
        this.stringifier = Objects.requireNonNull(stringifier);
        return this;
    }

    public Function<Object, String> getStringifier() { return stringifier; }

    public Joiner setPrefix(CharSequence prefix) {
        String old = this.prefix, now = this.prefix = emptyIfNull(prefix);
        if (StringUtil.isNotEmpty(old)) {
            container.replace(0, old.length(), now);
        }
        return this;
    }

    public String getPrefix() { return prefix; }

    public Joiner setSuffix(CharSequence suffix) {
        this.suffix = emptyIfNull(suffix);
        return this;
    }

    public String getSuffix() { return suffix; }

    public Joiner setDelimiter(CharSequence delimiter) {
        this.delimiter = emptyIfNull(delimiter);
        return this;
    }

    public String getDelimiter() { return delimiter; }

    /*
     * -------------------------------------------------------
     * inner methods
     * -------------------------------------------------------
     */

    private final Joiner counter() {
        itemCount++;
        return this;
    }

    private final String stringify(Object item) { return stringifier.apply(item); }

    private final <T> T isAllowNull(T item) {
        return requireNonNull ? (item == null ? ThrowUtil.unchecked(null) : item) : item;
    }

    private <T> Joiner ifChecked(T val, Consumer<T> consumer) {
        if ((val = isAllowNull(val)) == null) {
            if (useForNull != null) {
                return add(useForNull);
            }
        } else {
            consumer.accept(val);
        }
        return this;
    }

    private Joiner addStringify(Object item) { return addDelimiter().append(stringify(item)).counter(); }

    /*
     * -------------------------------------------------------
     * join array
     * -------------------------------------------------------
     */

    public <T> Joiner join(T... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(boolean... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(char... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(byte... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(short... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(int... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(long... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(float... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    public Joiner joinValues(double... arr) {
        return ensure(ArrayUtil.length(arr)).ifChecked(arr, values -> forEach(values, this::add));
    }

    /*
     * -------------------------------------------------------
     * join collect
     * -------------------------------------------------------
     */

    public Joiner join(Collection collect) {
        return ensure(size(collect)).ifChecked(collect, values -> forEach(values, this::add));
    }

    public Joiner join(Iterable iterable) { return ifChecked(iterable, values -> forEach(values, this::add)); }

    public Joiner join(Iterator iterator) { return ifChecked(iterator, values -> forEach(values, this::add)); }

    public Joiner join(Enumeration enumeration) {
        return ifChecked(enumeration, values -> forEach(values, this::add));
    }

    public <K, V> Joiner join(Map<K, V> map, BiFunction<? super K, ? super V, CharSequence> merger) {
        return ifChecked(map, values -> values.forEach((key, value) -> add(merger.apply(key, value))));
    }

    /*
     * -------------------------------------------------------
     * merge
     * -------------------------------------------------------
     */

    public Joiner merge(Joiner joiner) { return add(joiner); }

    public Joiner merge(java.util.StringJoiner joiner) { return add(joiner); }

    /*
     * -------------------------------------------------------
     * adds
     * -------------------------------------------------------
     */

    private Joiner addDelimiter() { return itemCount > 0 ? appendDelimiter() : this; }

    public Joiner add(CharSequence csq) { return ifChecked(csq, this::addStringify); }

    public Joiner add(Object csq) { return ifChecked(csq, this::addStringify); }

    public Joiner add(char value) { return addDelimiter().append(value).counter(); }

    public Joiner add(int value) { return addDelimiter().append(value).counter(); }

    public Joiner add(long value) { return addDelimiter().append(value).counter(); }

    public Joiner add(float value) { return addDelimiter().append(value).counter(); }

    public Joiner add(double value) { return addDelimiter().append(value).counter(); }

    public Joiner add(boolean value) { return addDelimiter().append(value).counter(); }

    /*
     * -------------------------------------------------------
     * append to other
     * -------------------------------------------------------
     */

    public <A extends Appendable> A appendTo(A appender) {
        Objects.requireNonNull(appender);
        try {
            appender.append(toString());
        } catch (IOException e) {
            ThrowUtil.runtime(e);
        }
        return appender;
    }

    /*
     * -------------------------------------------------------
     * append: 始终忠诚的在最后追加一个值
     * -------------------------------------------------------
     */

    /**
     * 直接追加一个分隔符
     *
     * @return 当前 Joiner
     */
    public Joiner appendDelimiter() { return append(delimiter); }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    public Joiner append(Object value) {
        container.append(value);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param csq 将要追加的值
     *
     * @return 当前 Joiner
     */
    @Override
    public Joiner append(CharSequence csq) {
        container.append(csq);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param csq   将要追加的值
     * @param start 起始位置
     * @param end   结束位置
     *
     * @return 当前 Joiner
     */
    @Override
    public Joiner append(CharSequence csq, int start, int end) {
        container.append(csq, start, end);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    @Override
    public Joiner append(char value) {
        container.append(value);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    public Joiner append(int value) {
        container.append(value);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    public Joiner append(long value) {
        container.append(value);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    public Joiner append(float value) {
        container.append(value);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    public Joiner append(double value) {
        container.append(value);
        return this;
    }

    /**
     * 直接追加一个值
     *
     * @param value 将要追加的值
     *
     * @return 当前 Joiner
     */
    public Joiner append(boolean value) {
        container.append(value);
        return this;
    }

    /*
     * -------------------------------------------------------
     * overrides
     * -------------------------------------------------------
     */

    private Joiner clear(CharSequence defaultValue) {
        this.container = defaultValue == null ? new StringBuilder() : new StringBuilder(defaultValue);
        this.itemCount = 0;
        return this;
    }

    private Joiner ensure(int size) { return ensureCapacity(size * DFT_LEN); }

    public Joiner ensureCapacity(int minCapacity) {
        container.ensureCapacity(minCapacity);
        return this;
    }

    public Joiner clear() { return clear(prefix); }

    public int contentLength() { return container.length() - prefix.length(); }

    @Override
    public int length() { return container.length() + suffix.length(); }

    @Override
    public Joiner clone() {
        return new Joiner(stringifier, prefix, delimiter, suffix, useForNull, requireNonNull, container);
    }

    @Override
    public char charAt(int index) {
        int contentLen = container.length();
        return index < contentLen ? container.charAt(index) : suffix.charAt(index - contentLen);
    }

    @Override
    public Joiner subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValue() { return get(); }

    @Override
    public String get() { return toString(); }

    @Override
    public String toString() { return new StringBuilder(container).append(suffix).toString(); }
}
