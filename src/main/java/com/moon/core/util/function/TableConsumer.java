package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface TableConsumer<X, Y, Z> {

    /**
     * 从三个维度处理结果
     *
     * @param x x 轴
     * @param y y 轴
     * @param z z 值
     */
    void accept(X x, Y y, Z z);
}
