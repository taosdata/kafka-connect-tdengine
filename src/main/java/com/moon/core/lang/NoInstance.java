package com.moon.core.lang;

/**
 * 无实例构造器
 *
 * @author moonsky
 */
@SuppressWarnings("all")
public abstract class NoInstance {

    protected NoInstance() { ThrowUtil.noInstanceError(); }
}
