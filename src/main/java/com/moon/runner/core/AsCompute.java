package com.moon.runner.core;

/**
 * @author moonsky
 */
interface AsCompute extends AsRunner {
    /**
     * 计算器
     *
     * @return 是否是个处理器
     */
    @Override
    default boolean isHandler() { return true; }

    /**
     * 计算
     *
     * @param right 右值
     * @param left 左值
     * @param data 参数
     * @return 执行后的值
     */
    @Override
    default Object exe(AsRunner right, AsRunner left, Object data) { return exe(right.run(data), left.run(data)); }
}
