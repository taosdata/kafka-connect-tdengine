package com.moon.core.lang;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class StringFactory implements Supplier<String> {

    private String source;

    public StringFactory() { }

    public static StringFactory of(CharSequence source) { return of().with(source); }

    public static StringFactory of() { return new StringFactory(); }

    private StringFactory set(String source) {
        this.source = source;
        return this;
    }

    public StringFactory with(CharSequence source) {
        return set(StringUtil.stringify(source));
    }

    public StringFactory withIfAbsent(CharSequence sequence) {
        return isAbsent() ? with(sequence) : this;
    }

    public StringFactory replace(char old, char replacement) {
        return set(StringUtil.replace(this.source, old, replacement));
    }

    public StringFactory replaceFirst(String old, String replacement) {
        return set(StringUtil.replaceFirst(this.source, old, replacement));
    }

    public StringFactory replaceAll(String old, String replacement) {
        return set(StringUtil.replaceAll(this.source, old, replacement));
    }

    public StringFactory replaceMatched(String regex, String replacement) {
        return set(source == null ? null : source.replaceAll(regex, replacement));
    }

    public StringFactory add(CharSequence sequence) {
        return set(this.source + sequence);
    }

    public StringFactory addNonNull(CharSequence sequence) {
        return sequence == null ? this : add(sequence);
    }

    public boolean isAbsent() { return this.source == null; }

    public boolean isPresent() { return !isAbsent(); }

    public StringFactory use(Consumer<String> consumer) {
        consumer.accept(this.source);
        return this;
    }

    public StringFactory useIfPresent(Consumer<String> consumer) {
        if (isPresent()) {
            consumer.accept(this.source);
        }
        return this;
    }

    @Override
    public String get() { return this.source; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        StringFactory that = (StringFactory) o;
        return Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() { return Objects.hash(source); }

    @Override
    public String toString() { return String.valueOf(this.source); }
}
