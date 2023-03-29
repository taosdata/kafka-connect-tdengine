package com.moon.core.enums;

import com.moon.core.lang.EnumUtil;

/**
 * @author moonsky
 */
public enum Level {
    L0,
    L1,
    L2,
    L3,
    L4,
    L5,
    L6,
    L7,
    L8,
    L9;

    public final static Level LOWEST = Level.L0;
    public final static Level HIGHEST = Level.L9;

    public boolean isAfter(Level level) {
        return EnumUtil.isAfter(this, level);
    }

    public boolean isBefore(Level level) {
        return EnumUtil.isBefore(this, level);
    }
}
