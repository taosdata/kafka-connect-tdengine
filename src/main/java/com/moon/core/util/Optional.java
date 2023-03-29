package com.moon.core.util;

import com.moon.core.lang.Executable;
import com.moon.core.lang.ThrowUtil;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface Optional<T> extends Optionally {

    /**
     * null 对象
     */
    Optional empty = OptionalImpl.EMPTY;

    /**
     * null 对象
     *
     * @return
     */
    static Optional empty() { return empty; }

    /**
     * 新对象，value 不可为 null
     *
     * @param value
     * @param <E>
     *
     * @return
     */
    static <E> Optional<E> of(E value) { return of(value, OptionalImpl::new); }

    /**
     * 新对象，可为 null
     *
     * @param value
     * @param <E>
     *
     * @return
     */
    static <E> Optional<E> ofNullable(E value) { return value == null ? empty : of(value); }

    /**
     * 从{@link java.util.Optional}转化
     *
     * @param utilOptional
     * @param <T>
     *
     * @return
     */
    static <T> Optional<T> fromUtil(java.util.Optional<T> utilOptional) {
        return ofNullable(utilOptional.orElse(null));
    }

    /**
     * 构造对象
     *
     * @param value
     * @param mapper
     * @param <E>
     *
     * @return
     */
    static <E, OI extends Optional<E>> OI of(E value, Function<? super E, OI> mapper) { return mapper.apply(value); }

    /**
     * 返回值
     *
     * @return
     */
    default T getOrNull() { return null; }

    /**
     * 返回值或抛出异常
     *
     * @return
     */
    default T get() { return isPresent() ? getOrNull() : ThrowUtil.unchecked(null); }

    /**
     * 返回值或使用默认值
     *
     * @param defaultValue
     *
     * @return
     */
    default T getOrDefault(T defaultValue) { return isPresent() ? getOrNull() : defaultValue; }

    /**
     * 返回值或使用默认值
     *
     * @param supplier
     *
     * @return
     */
    default T getOrElse(Supplier<T> supplier) { return isPresent() ? getOrNull() : supplier.get(); }

    /**
     * 返回值或抛出指定异常
     *
     * @param supplier
     * @param <EX>
     *
     * @return
     *
     * @throws EX
     */
    default <EX extends Throwable> T getOrThrow(Supplier<EX> supplier) throws EX {
        if (isPresent()) { return getOrNull(); }
        throw supplier.get();
    }

    /**
     * 是否存在
     *
     * @return
     */
    @Override
    default boolean isPresent() { return getOrNull() != null; }

    /**
     * 是否不存在
     *
     * @return
     */
    @Override
    default boolean isAbsent() { return getOrNull() == null; }

    /**
     * 确保符合条件，否则返回 null 对象
     *
     * @param predicate
     *
     * @return
     */
    default Optional<T> filter(Predicate<? super T> predicate) {
        return isAbsent() ? this : (predicate.test(getOrNull()) ? this : empty);
    }

    /**
     * 不存在的情况设置默认值
     *
     * @param supplier
     *
     * @return
     */
    default Optional<T> elseIfAbsent(Supplier<T> supplier) { return isAbsent() ? ofNullable(supplier.get()) : this; }

    /**
     * 不存在的情况设置默认值
     *
     * @param defaultValue
     *
     * @return
     */
    default Optional<T> defaultIfAbsent(T defaultValue) { return isAbsent() ? ofNullable(defaultValue) : this; }

    /**
     * 存在的情况下消费
     *
     * @param consumer
     *
     * @return
     */
    default Optional<T> ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) { consumer.accept(getOrNull()); }
        return this;
    }

    /**
     * 不存在的情况下执行
     *
     * @param executor
     *
     * @return
     */
    default Optional<T> ifAbsent(Executable executor) {
        if (isAbsent()) { executor.execute(); }
        return this;
    }

    /**
     * 计算
     *
     * @param computer
     *
     * @return
     */
    default <E> E compute(Function<? super T, ? extends E> computer) { return computer.apply(getOrNull()); }

    /**
     * 转换为{@link java.util.Optional}
     *
     * @return
     */
    default java.util.Optional<T> toUtil() { return java.util.Optional.ofNullable(getOrNull()); }

    /**
     * 转换
     *
     * @param computer
     * @param <E>
     *
     * @return
     */
    default <E> Optional<E> transform(Function<? super T, ? extends E> computer) {
        return ofNullable(computer.apply(getOrNull()));
    }
}
