package com.moon.runner.core;

/**
 * @author moonsky
 */
class GetComplex implements AsGetter {

    final AsValuer beforeItem;
    final AsValuer afterItem;

    IGetter getter;

    GetComplex(AsValuer beforeItem, AsValuer afterItem) {
        this.beforeItem = beforeItem;
        this.afterItem = afterItem;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Object o) { return true; }

    @Override
    public boolean isGetterComplex() { return true; }

    @Override
    public Object run(Object data) {
        Object prevData = beforeItem.run(data);
        Object afterData = afterItem.run(data);
        IGetter getter = this.getter;
        if (getter == null) {
            this.getter = getter = IGetter.reset(prevData, afterData);
        }
        return getter.apply(prevData, afterData);
    }

    @Override
    public String toString() { return beforeItem.toString() + " " + afterItem.toString(); }
}
