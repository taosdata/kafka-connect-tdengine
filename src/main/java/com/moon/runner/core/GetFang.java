package com.moon.runner.core;

/**
 * 从数据源获取数据
 *
 * @author moonsky
 */
class GetFang implements AsGetter {

    final AsValuer valuer;
    IGetter getter;

    GetFang(AsValuer valuer) { this.valuer = valuer; }

    AsRunner toComplex(AsRunner beforeValuer) { return new GetComplex((AsValuer) beforeValuer, this.valuer); }

    public IGetter getGetter(Object data, Object innerData) {
        IGetter getter = this.getter;
        if (getter == null || !getter.test(data)) {
            getter = IGetter.reset(data, innerData);
            this.getter = getter;
        }
        return getter;
    }

    @Override
    public Object run(Object data) {
        Object innerData = valuer.run(data);
        Object result = getGetter(data, innerData).apply(data, innerData);
        return result;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Object o) { return false; }

    @Override
    public String toString() { return "[" + valuer.toString() + "]"; }
}
