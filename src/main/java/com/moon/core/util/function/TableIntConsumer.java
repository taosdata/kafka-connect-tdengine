package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface TableIntConsumer<F, S> {

    /**
     * 处理数据
     *
     * @param firstArg  第一个参数
     * @param secondArg 第二个参数
     * @param thirdArg  第三个参数，通常可能是索引
     */
    void accept(F firstArg, S secondArg, int thirdArg);
}
