package com.moon.core.util.function;

import java.util.Objects;

/**
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface CharConsumer {
    /**
     * Performs this operation on the given argument.
     *
     * @param value the input argument
     */
    void accept(char value);

    /**
     * Returns a composed {@code CharConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code CharConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @see NullPointerException if {@code after} is null
     */
    default CharConsumer andThen(CharConsumer after) {
        Objects.requireNonNull(after);
        return (char t) -> { accept(t); after.accept(t); };
    }
}
