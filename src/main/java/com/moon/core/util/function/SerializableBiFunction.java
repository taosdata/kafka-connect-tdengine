package com.moon.core.util.function;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface SerializableBiFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {}
