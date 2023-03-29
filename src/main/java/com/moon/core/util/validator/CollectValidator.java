package com.moon.core.util.validator;

import com.moon.core.util.CollectUtil;
import com.moon.core.util.FilterUtil;
import com.moon.core.util.GroupUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 集合验证器：
 * 首先请参考{@link Validator}文档。CollectValidator 继承了 Validator；具备基本的对象验证功能。
 *
 * @author moonsky
 */
public final class CollectValidator<C extends Collection<E>, E> extends BaseValidator<C, CollectValidator<C, E>>
    implements ICollectValidator<C, E, CollectValidator<C, E>> {

    public CollectValidator(C value) { this(value, false); }

    public CollectValidator(C value, boolean nullable) { this(value, nullable, null, SEPARATOR, false); }

    CollectValidator(
        C value, boolean nullable, List<String> messages, String separator, boolean immediate
    ) { super(value, nullable, messages, separator, immediate); }

    public static <C extends Collection<E>, E> CollectValidator<C, E> of(C collect) {
        return new CollectValidator<>(collect);
    }

    public static <C extends Collection<E>, E> CollectValidator<C, E> ofNullable(C collect) {
        return new CollectValidator<>(collect, true);
    }

    @Override
    @SuppressWarnings("all")
    public CollectValidator<C, E> forEach(Consumer<Validator<E>> itemValidator) {
        C collect = getValue();
        if (collect != null) {
            for (E item : collect) {
                itemValidator.accept(new Validator<>(item, isNullable(),//
                    ensureMessages(), getSeparator(), isImmediate()));
            }
        }
        return current();
    }

    /*
     * -----------------------------------------------------
     * requires
     * -----------------------------------------------------
     */

    @Override
    public CollectValidator<C, E> requireAtLeastOf(int count, Predicate<? super E> tester, String message) {
        final C value = getValue();
        int amount = 0;
        for (E item : value) {
            if (tester.test(item) && (++amount >= count)) {
                return this;
            }
        }
        return amount < count ? createMsgAtLeast(message, count) : this;
    }

    @Override
    public CollectValidator<C, E> requireAtMostOf(int count, Predicate<? super E> tester, String message) {
        final C value = getValue();
        int amount = 0;
        for (E item : value) {
            if (tester.test(item) && (++amount > count)) {
                return createMsgAtMost(message, count);
            }
        }
        return amount > count ? createMsgAtMost(message, count) : this;
    }

    @Override
    public CollectValidator<C, E> requireCountOf(int count, Predicate<? super E> tester, String message) {
        final C value = getValue();
        int amount = 0;
        for (E item : value) {
            if (tester.test(item) && (++amount > count)) {
                return createMsgCountOf(message, count);
            }
        }
        return amount < count ? createMsgCountOf(message, count) : this;
    }

    /*
     * -----------------------------------------------------
     * group by
     * -----------------------------------------------------
     */

    public final <O> GroupValidator<Map<O, Collection<E>>, O, Collection<E>, E> groupBy(Function<? super E, O> grouper) {
        return new GroupValidator<>(GroupUtil.groupBy(value, grouper),
            isNullable(),
            ensureMessages(),
            getSeparator(),
            isImmediate());
    }

    /*
     * -----------------------------------------------------
     * filter
     * -----------------------------------------------------
     */

    public final CollectValidator<List<E>, E> filter(Predicate<? super E> filter) {
        List<E> filtered = FilterUtil.filter(value, filter, new ArrayList<>());
        return new CollectValidator<>(filtered, isNullable(), ensureMessages(), getSeparator(), isImmediate());
    }

    /**
     * 当验证通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    @Override
    public CollectValidator<C, E> ifValid(Consumer<? super C> consumer) { return super.ifValid(consumer); }

    /**
     * 当验证不通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    @Override
    public CollectValidator<C, E> ifInvalid(Consumer<? super C> consumer) { return super.ifInvalid(consumer); }

    /**
     * 直接添加一条错误消息
     *
     * @param message 消息内容
     *
     * @return 当前 Validator 实例
     */
    @Override
    public CollectValidator<C, E> addErrorMessage(String message) { return super.addErrorMessage(message); }

    /**
     * 可用于在后面条件验证前预先设置一部分默认值
     * <p>
     * 只能改变{@link #value}内部属性的值，不能重新设置 value
     *
     * @param consumer 预处理器
     *
     * @return 当前 Validator 实例
     */
    @Override
    public CollectValidator<C, E> preset(Consumer<? super C> consumer) { return super.preset(consumer); }

    /**
     * 设置是否立即终止，如果设置了即时终止，那么在设置之后第一个验证不通过会立即抛出异常
     * <p>
     * 否则在最后才抛出异常
     *
     * @param immediate 是否即时终止
     *
     * @return 当前 Validator 实例
     */
    @Override
    public CollectValidator<C, E> setImmediate(boolean immediate) { return super.setImmediate(immediate); }

    /**
     * 设置错误信息分隔符，不能为 null
     *
     * @param separator 错误消息分割符
     *
     * @return 当前 Validator 实例
     */
    @Override
    public CollectValidator<C, E> setSeparator(String separator) { return super.setSeparator(separator); }

    /**
     * 异常构造器
     *
     * @param exceptionBuilder 异常构造器
     *
     * @return 当前对象
     */
    @Override
    public CollectValidator<C, E> setExceptionBuilder(Function<String, ? extends RuntimeException> exceptionBuilder) {
        return super.setExceptionBuilder(exceptionBuilder);
    }

    /**
     * 条件验证
     * <p>
     * 在前置条件匹配的情况下会执行 when 和 end 之间的验证或其他逻辑
     *
     * @param tester          进入条件验证的函数
     * @param scopedValidator 符合验证条件下的验证函数
     *
     * @return 当前 IValidator 对象
     */
    @Override
    public CollectValidator<C, E> when(
        Predicate<? super C> tester, Consumer<CollectValidator<C, E>> scopedValidator
    ) { return super.when(tester, scopedValidator); }

    /**
     * 要求符合条件
     *
     * @param tester  断言函数
     * @param message 错误消息
     *
     * @return 当前 Validator 对象
     */
    @Override
    public CollectValidator<C, E> require(Predicate<? super C> tester, String message) {
        return super.require(tester, message);
    }

    /**
     * 要求所有项都符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireEvery(Predicate<? super E> tester) {
        return requireEvery(tester, requireEvery);
    }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireEvery(Predicate<? super E> tester, String message) {
        return requireAtLeastOf(CollectUtil.size(getValue()), tester, message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireAtLeast1(Predicate<? super E> tester) {
        return requireAtLeast1(tester, requireAtLeast);
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireAtLeast1(Predicate<? super E> tester, String message) {
        return requireAtLeastOf(1, tester, message);
    }

    /**
     * 要求至少指定数目项符合验证
     *
     * @param count  期望符合条件元素数量下限
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireAtLeastOf(int count, Predicate<? super E> tester) {
        return requireAtLeastOf(count, tester, requireAtLeast);
    }

    /**
     * 要求所有项都不符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireNone(Predicate<? super E> tester) {
        return requireNone(tester, requireNone);
    }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireNone(Predicate<? super E> tester, String message) {
        return requireAtMostOf(0, tester, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireAtMost1(Predicate<? super E> tester) {
        return requireAtMost1(tester, requireAtMost);
    }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireAtMost1(Predicate<? super E> tester, String message) {
        return requireAtMostOf(1, tester, message);
    }

    /**
     * 要求最多指定数目项符合验证
     *
     * @param count  期望符合条件数量上限
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireAtMostOf(int count, Predicate<? super E> tester) {
        return requireAtMostOf(count, tester, requireAtMost);
    }

    /**
     * 要求包含唯一项符合验证
     *
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireOnly(Predicate<? super E> tester) {
        return requireOnly(tester, requireOnly);
    }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester  检测函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireOnly(Predicate<? super E> tester, String message) {
        return requireCountOf(1, tester, message);
    }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param count  期望符合条件数量
     * @param tester 检测函数
     *
     * @return 当前验证对象
     */
    @Override
    public CollectValidator<C, E> requireCountOf(int count, Predicate<? super E> tester) {
        return requireCountOf(count, tester, requireCountOf);
    }

    /**
     * 要求符合指定验证规则
     *
     * @param tester 验证函数
     *
     * @return 当前 IValidator 对象
     */
    @Override
    public CollectValidator<C, E> require(Predicate<? super C> tester) {
        return require(tester, invalidValue);
    }
}
