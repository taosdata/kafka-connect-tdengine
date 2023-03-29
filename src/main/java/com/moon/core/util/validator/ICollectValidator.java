package com.moon.core.util.validator;

import com.moon.core.util.CollectUtil;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
interface ICollectValidator<C extends Collection<E>, E, IMPL extends ICollectValidator<C, E, IMPL>>
    extends IValidator<C, IMPL> {

    /**
     * 依次验证集合中每一个单项
     *
     * @param itemValidator 单项验证器
     *
     * @return 当前对象
     */
    IMPL forEach(Consumer<Validator<E>> itemValidator);

    /**
     * 要求至少指定数目项符合验证，使用指定错误信息
     *
     * @param count   期望最低数值
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    IMPL requireAtLeastOf(int count, Predicate<? super E> tester, String message);

    /**
     * 要求最多指定数目项符合验证，使用指定错误信息
     *
     * @param count   期望最高数值
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    IMPL requireAtMostOf(int count, Predicate<? super E> tester, String message);

    /*
     * -----------------------------------------------------
     * implemented
     * -----------------------------------------------------
     */

    /*
     * -----------------------------------------------------
     * at least
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    default IMPL requireEvery(Predicate<? super E> tester) { return requireEvery(tester, "requireEvery"); }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireEvery(Predicate<? super E> tester, String message) {
        return requireAtLeastOf(CollectUtil.size(getValue()), tester, message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    default IMPL requireAtLeast1(Predicate<? super E> tester) {
        return requireAtLeast1(tester, "requireAtLeast1");
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireAtLeast1(Predicate<? super E> tester, String message) {
        return requireAtLeastOf(1, tester, message);
    }

    /**
     * 要求至少指定数目项符合验证
     *
     * @param tester 检测函数
     * @param count  期望符合条件元素数量下限
     *
     * @return 当前验证对象
     */
    default IMPL requireAtLeastOf(int count, Predicate<? super E> tester) {
        return requireAtLeastOf(count, tester, "requireAtLeastCountOf");
    }

    /*
     * -----------------------------------------------------
     * at most
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都不符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    default IMPL requireNone(Predicate<? super E> tester) { return requireNone(tester, "requireNone"); }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireNone(Predicate<? super E> tester, String message) {
        return requireAtMostOf(0, tester, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    default IMPL requireAtMost1(Predicate<? super E> tester) { return requireAtMost1(tester, "requireAtMost1"); }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireAtMost1(Predicate<? super E> tester, String message) {
        return requireAtMostOf(1, tester, message);
    }

    /**
     * 要求最多指定数目项符合验证
     *
     * @param tester 检测函数
     * @param count  期望符合条件数量上限
     *
     * @return 当前验证对象
     */
    default IMPL requireAtMostOf(int count, Predicate<? super E> tester) {
        return requireAtMostOf(count, tester, "requireAtMostCountOf");
    }

    /*
     * -----------------------------------------------------
     * count of
     * -----------------------------------------------------
     */

    /**
     * 要求包含唯一项符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    default IMPL requireOnly(Predicate<? super E> tester) { return requireOnly(tester, "requireOnly"); }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireOnly(Predicate<? super E> tester, String message) { return requireCountOf(1, tester, message); }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param tester 检测函数
     * @param count  期望符合条件数量
     *
     * @return 当前验证对象
     */
    default IMPL requireCountOf(int count, Predicate<? super E> tester) {
        return requireCountOf(count, tester, "requireCountOf");
    }

    /**
     * 要求包含指定数目项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param count   期望符合条件数量
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    IMPL requireCountOf(int count, Predicate<? super E> tester, String message);
}
