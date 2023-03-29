package com.moon.core.lang.ref;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 推荐使用{@link Table}、{@link TableImpl}
 *
 * @author moonsky
 */
@Deprecated
public class DefaultLocation<X, Y, Z> implements Location<X, Y, Z> {

    private final Supplier<Map> creator;

    private Map value;

    public DefaultLocation(Supplier<Map> creator) { this.creator = creator; }

    public static final <X, Y, Z> DefaultLocation<X, Y, Z> of(Supplier<Map> creator) {
        return new DefaultLocation<>(creator);
    }

    private Map<Y, Z> onlyGetX(X x) { return value == null ? null : (Map) value.get(x); }

    private Map<Y, Z> ensureGetX(X x) {
        Map map = this.value, res;
        if (map == null) {
            this.value = map = createSub();
            map.put(x, res = createSub());
        } else if ((res = (Map) map.get(x)) == null) {
            map.put(x, res = createSub());
        }
        return res;
    }

    /**
     * 设置一个值
     *
     * @param x first key
     * @param y second key
     * @param z value
     *
     * @return this
     */
    @Override
    public DefaultLocation<X, Y, Z> put(X x, Y y, Z z) {
        ensureGetX(x).put(y, z);
        return this;
    }

    /**
     * 放置所有
     *
     * @param x   namespace
     * @param map values entry
     *
     * @return this
     */
    @Override
    public DefaultLocation<X, Y, Z> putAll(X x, Map<? extends Y, ? extends Z> map) {
        ensureGetX(x).putAll(map);
        return this;
    }

    /**
     * 获取一个值
     *
     * @param x namespace
     * @param y key
     *
     * @return value
     */
    @Override
    public Z get(X x, Y y) {
        Map onlyX = onlyGetX(x);
        return onlyX == null ? null : (Z) onlyX.get(y);
    }

    /**
     * 清空
     *
     * @return this
     */
    @Override
    public DefaultLocation<X, Y, Z> clear() {
        this.value = null;
        return this;
    }

    /**
     * 清空
     *
     * @param x namespace
     *
     * @return this
     */
    @Override
    public DefaultLocation<X, Y, Z> clear(X x) {
        Map onlyX = onlyGetX(x);
        if (onlyX != null) { value.remove(x); }
        return this;
    }

    private Map createSub() { return creator.get(); }
}
