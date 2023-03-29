package com.moon.core.util.validator;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
interface IValidator<T, IMPL extends IValidator<T, IMPL>> {

    /**
     * 条件验证
     * <p>
     * 只有在{@code tester}返回 true 时，才执行后面{@code scopedValidator}验证器
     *
     * @param tester          进入条件验证的函数
     * @param scopedValidator 符合验证条件下的验证函数
     *
     * @return 当前 IValidator 对象
     */
    IMPL when(Predicate<? super T> tester, Consumer<IMPL> scopedValidator);

    /**
     * 返回对象，单纯的返回对象，不携带任何验证
     *
     * @return 原始对象
     */
    T getValue();

    /**
     * 要求符合指定验证规则，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前 IValidator 对象
     */
    IMPL require(Predicate<? super T> tester, String message);

    /**
     * 要求符合指定验证规则
     *
     * @param tester 验证函数
     *
     * @return 当前 IValidator 对象
     */
    default IMPL require(Predicate<? super T> tester) { return require(tester, Value.NONE); }

    /**
     * 手动添加一条错误信息
     *
     * @param message 错误消息
     *
     * @return 当前 IValidator 对象
     */
    IMPL addErrorMessage(String message);
}
