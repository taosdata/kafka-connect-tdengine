package com.moon.runner;

/**
 * 自定义函数
 * <p>
 * RunnerUtil 可以执行自定义函数，自定义函数需要实现这个借口
 * <p>
 * 自定义函数需要多少个参数就实现对应的方法即可
 * <p>
 * 自定义函数优先级高于静态方法，即同名的函数和静态方法追踪执行的是函数，而不是静态方法
 * <p>
 * 所以此情况下可能存在返回值不符合预期，甚至抛出异常
 * <p>
 * 故，自定义函数的命名空间建议全部小写
 *
 * @author moonsky
 */
public interface RunnerFunction {
    /**
     * 每个 function 需要定义一个名字，
     * 而且符合 java 变量命名规则
     * 首字母小写
     *
     * @return
     */
    String functionName();

    /**
     * 这个函数执行相同参数的返回值是否相同，根据这个值会有少量优化
     *
     * @return
     */
    default boolean isChangeless() { return true; }

    /**
     * 执行无参方法
     *
     * @return
     */
    default Object apply() { throw new UnsupportedOperationException(); }

    /**
     * 执行含有一个参数的方法
     *
     * @param value
     * @return
     */
    default Object apply(Object value) { throw new UnsupportedOperationException(); }

    /**
     * 执行含有两个参数的方法
     *
     * @param value1
     * @param value2
     * @return
     */
    default Object apply(Object value1, Object value2) { throw new UnsupportedOperationException(); }

    /**
     * 执行含有三个参数的方法
     *
     * @param value1
     * @param value2
     * @param value3
     * @return
     */
    default Object apply(Object value1, Object value2, Object value3) { throw new UnsupportedOperationException(); }

    /**
     * 执行无参或含有多个参数的方法
     *
     * @param values
     * @return
     */
    default Object apply(Object... values) { throw new UnsupportedOperationException(); }
}
