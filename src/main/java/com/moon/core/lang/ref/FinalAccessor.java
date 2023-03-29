package com.moon.core.lang.ref;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public class FinalAccessor<T> implements Supplier<T> {

    private T value;

    public FinalAccessor() { }

    public FinalAccessor(T value) { this.set(value); }

    public static <T> FinalAccessor<T> of() { return new FinalAccessor<>(); }

    public static <T> FinalAccessor<T> of(T value) { return new FinalAccessor<>(value); }

    /*
     * ------------------------------------------------------------
     * sets
     * ------------------------------------------------------------
     */

    public FinalAccessor<T> clear() {
        this.value = null;
        return this;
    }

    public FinalAccessor<T> set(T value) {
        this.value = value;
        return this;
    }

    public FinalAccessor<T> set(Supplier<T> supplier) {
        this.value = supplier.get();
        return this;
    }

    public FinalAccessor<T> setIfAbsent(T value) { return isPresent() ? this : set(value); }

    public FinalAccessor<T> setIfAbsent(Supplier<T> supplier) { return isPresent() ? this : set(supplier.get()); }

    /*
     * ------------------------------------------------------------
     * gets
     * ------------------------------------------------------------
     */

    @Override
    public T get() { return value; }

    public T getOrDefault(T defaultValue) { return isPresent() ? value : defaultValue; }

    public T getOrElse(Supplier<T> supplier) { return isPresent() ? value : supplier.get(); }

    public T requireGet() { return Objects.requireNonNull(value); }

    public T requireGet(String exceptionMessage) { return Objects.requireNonNull(value, exceptionMessage); }

    public <EX extends Throwable> T requireGet(EX ex) {
        if (isAbsent()) {
            throw new IllegalArgumentException(ex);
        }
        return value;
    }

    public <EX extends Throwable> T requireGet(Supplier<EX> supplier) throws EX {
        if (isAbsent()) {
            throw supplier.get();
        }
        return value;
    }

    /*
     * ------------------------------------------------------------
     * assertions
     * ------------------------------------------------------------
     */

    public boolean isPresent() { return value != null; }

    public boolean isAbsent() { return value == null; }

    public boolean isEquals(Object value) { return Objects.equals(value, this.value); }

    /*
     * ------------------------------------------------------------
     * consumers
     * ------------------------------------------------------------
     */

    public FinalAccessor<T> ifPresent(Consumer<T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
        return this;
    }

    public <R> R ifPresentOrNull(Function<T, R> consumer) {
        return isPresent() ? consumer.apply(value) : null;
    }

    /*
     * ------------------------------------------------------------
     * computer
     * ------------------------------------------------------------
     */

    public T replaceOf(T newValue) {
        T value = get();
        set(newValue);
        return value;
    }

    public FinalAccessor<T> compute(Function<T, T> computer) {
        value = computer.apply(value);
        return this;
    }

    public FinalAccessor<T> computeIfPresent(Function<T, T> computer) {
        if (isPresent()) {
            value = computer.apply(value);
        }
        return this;
    }

    /*
     * ------------------------------------------------------------
     * mappers
     * ------------------------------------------------------------
     */

    public <O> FinalAccessor<O> transform(Function<T, O> function) { return of(function.apply(value)); }

    @Override
    public String toString() { return String.valueOf(value); }
}
