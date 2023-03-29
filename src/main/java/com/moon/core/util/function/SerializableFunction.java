package com.moon.core.util.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {}
