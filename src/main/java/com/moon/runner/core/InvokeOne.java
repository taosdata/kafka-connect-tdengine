package com.moon.runner.core;

import com.moon.core.lang.reflect.MethodUtil;

import java.util.Objects;

/**
 * @author moonsky
 */
class InvokeOne extends InvokeBase {

    final AsValuer prevValuer;
    final AsRunner valuer;

    public InvokeOne(AsValuer prevValuer, AsRunner valuer, String methodName) {
        super(methodName);
        this.valuer = Objects.requireNonNull(valuer);
        this.prevValuer = Objects.requireNonNull(prevValuer);
    }

    @Override
    public Object run(Object data) {
        Object source = prevValuer.run(data);
        Object params = valuer.run(data);
        return MethodUtil.invoke(true, getMethod(source, params), source, params);
    }
}
