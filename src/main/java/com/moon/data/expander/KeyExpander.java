package com.moon.data.expander;

import com.moon.core.lang.StringUtil;

/**
 * 当 ID 为数字类型时，如果过长传给前端可能出现精度问题
 * <p>
 * JavaScript 只能精确显示 17 位以下的数字，
 * 虽然新的{@code ECMA}规范支持{@code Bigint}，但主键这种常用的场合还是建议使用{@code string}
 *
 * @author benshaoye
 */
public interface KeyExpander<ID extends Number> {

    /**
     * ID
     *
     * @return ID
     */
    ID getId();

    /**
     * stringify id
     *
     * @return stringify id
     */
    default String getKey() { return StringUtil.stringify(getId()); }
}
