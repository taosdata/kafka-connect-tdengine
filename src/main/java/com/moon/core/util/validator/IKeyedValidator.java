package com.moon.core.util.validator;

import com.moon.core.util.MapUtil;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
interface IKeyedValidator<M extends Map<K, V>, K, V, IMPL extends IKeyedValidator<M, K, V, IMPL>>
    extends IValidator<M, IMPL> {

    /**
     * 要求包含指定数目项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param count   期望符合条件的元素数量
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    IMPL requireCountOf(int count, BiPredicate<? super K, ? super V> tester, String message);

    /**
     * 要求至少指定数目项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param count   期望符合条件的元素数量最小值
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    IMPL requireAtLeastOf(int count, BiPredicate<? super K, ? super V> tester, String message);

    /**
     * 要求最多指定数目项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param count   期望符合条件的元素数量最大值
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    IMPL requireAtMostOf(int count, BiPredicate<? super K, ? super V> tester, String message);

    /*
     * -----------------------------------------------------
     * implemented
     * -----------------------------------------------------
     */

    /**
     * 要求存在指定 KEY
     *
     * @param key 指定 key
     *
     * @return 当前验证对象
     */
    default IMPL requirePresentKey(K key) {
        return requirePresentKey(key, "Require present key of: {}");
    }

    /**
     * 要求存在指定 KEY
     *
     * @param key     指定 key
     * @param message 错误消息模板
     *
     * @return 当前验证对象
     */
    IMPL requirePresentKey(K key, String message);

    /**
     * 要求在存在某个键对应映射值应该符合验证，使用指定错误信息模板
     *
     * @param key     存在指定键时验证对应项目
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireValueOf(K key, Predicate<? super V> tester, String message) {
        M map = getValue();
        if (!tester.test(map.get(key))) {
            addErrorMessage(message);
        }
        return (IMPL) this;
    }

    /**
     * 要求在存在某个键对应映射值应该符合验证
     *
     * @param key    存在指定键时验证对应项目
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    default IMPL requireValueOf(K key, Predicate<? super V> tester) {
        return requireValueOf(key, tester, Value.NONE);
    }

    /*
     * -----------------------------------------------------
     * at least
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    default IMPL requireEvery(BiPredicate<? super K, ? super V> tester) { return requireEvery(tester, Value.NONE); }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireEvery(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtLeastOf(MapUtil.size(getValue()), tester, message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    default IMPL requireAtLeast1(BiPredicate<? super K, ? super V> tester) {
        return requireAtLeast1(tester, Value.NONE);
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireAtLeast1(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtLeastOf(1, tester, message);
    }

    /**
     * 要求至少指定数目项符合验证
     *
     * @param tester 验证函数
     * @param count  符合验证条件的最小元素数量
     *
     * @return 当前验证对象
     */
    default IMPL requireAtLeastOf(int count, BiPredicate<? super K, ? super V> tester) {
        return requireAtLeastOf(count, tester, Value.NONE);
    }

    /*
     * -----------------------------------------------------
     * at most
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都不符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    default IMPL requireNone(BiPredicate<? super K, ? super V> tester) { return requireNone(tester, Value.NONE); }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireNone(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtMostOf(0, tester, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    default IMPL requireAtMost1(BiPredicate<? super K, ? super V> tester) {
        return requireAtMost1(tester, Value.NONE);
    }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireAtMost1(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtMostOf(1, tester, message);
    }

    /**
     * 要求最多指定数目项符合验证
     *
     * @param tester 验证函数
     * @param count  符合验证条件的最大元素数量
     *
     * @return 当前验证对象
     */
    default IMPL requireAtMostOf(int count, BiPredicate<? super K, ? super V> tester) {
        return requireAtMostOf(count, tester, Value.NONE);
    }

    /*
     * -----------------------------------------------------
     * count of
     * -----------------------------------------------------
     */

    /**
     * 要求包含唯一项符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    default IMPL requireOnly(BiPredicate<? super K, ? super V> tester) { return requireOnly(tester, Value.NONE); }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    default IMPL requireOnly(BiPredicate<? super K, ? super V> tester, String message) {
        return requireCountOf(1, tester, message);
    }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param tester 验证函数
     * @param count  符合验证条件的元素数量
     *
     * @return 当前验证对象
     */
    default IMPL requireCountOf(int count, BiPredicate<? super K, ? super V> tester) {
        return requireCountOf(count, tester, Value.NONE);
    }
}
