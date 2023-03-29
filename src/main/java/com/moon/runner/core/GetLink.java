package com.moon.runner.core;

import java.util.Objects;

/**
 * 连接前后获取值
 *
 * @author moonsky
 */
class GetLink implements AsGetter {
    final AsValuer prevGetter;
    final AsValuer valuer;

    GetLink(AsValuer prevGetter, AsValuer key) {
        this.prevGetter = Objects.requireNonNull(prevGetter);
        this.valuer = key;
    }

    @Override
    public Object run(Object data) { return valuer.run(prevGetter.run(data)); }

    @Override
    public boolean test(Object o) { return false; }

    @Override
    public String toString() { return prevGetter.toString() + " " + valuer.toString(); }
}
