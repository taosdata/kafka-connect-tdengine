package com.moon.core.util.validator;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author moonsky
 */
interface IGroupValidator<M extends Map<K, C>, C extends Collection<E>, K, E, IMPL extends IGroupValidator<M, C, K, E, IMPL>>
    extends IKeyedValidator<M, K, C, IMPL> {

    /**
     * 匹配验证每一项
     *
     * @param consumer 单项验证器
     *
     * @return 当前验证对象
     */
    IMPL forEach(BiConsumer<? super K, CollectValidator<C, E>> consumer);
}
