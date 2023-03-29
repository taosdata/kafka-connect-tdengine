package com.moon.core.enums;

import com.moon.core.model.IdName;
import com.moon.core.model.KeyValue;
import com.moon.core.model.getter.NameGetter;

/**
 * @author moonsky
 */
public interface EnumDescriptor extends NameGetter {

    /**
     * 枚举信息
     *
     * @return 枚举信息
     */
    String getText();

    /**
     * 枚举名字
     *
     * @return 枚举名字
     *
     * @see Enum#name()
     */
    String name();

    /**
     * 枚举名字
     * <p>
     * 与{@link #name()}相同
     *
     * @return 枚举名字
     *
     * @see IdName
     * @see KeyValue
     * @see com.moon.core.net.enums.StatusCode
     * @see Enum#name()
     */
    @Override
    default String getName() {
        return name();
    }
}
