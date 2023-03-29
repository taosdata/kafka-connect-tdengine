package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface TableFunction<P1, P2, P3, R> {

    <R> R apply(P1 p1, P2 p2, P3 p3);
}
