package com.moon.core.lang.ref;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.WeakHashMap;

/**
 * 推荐使用{@link Table}、{@link TableImpl}
 *
 * @author moonsky
 */
@Deprecated
public class WeakLocation<X, Y, Z> extends DefaultLocation<X, Y, Z> {

    public WeakLocation() { super(WeakHashMap::new); }

    public final static <X, Y, Z> WeakLocation<X, Y, Z> of() { return new WeakLocation<>(); }

    public final static <X, Y, Z> WeakLocation<X, Y, Z> ofManaged() { return new WeakLocation<>(); }
}
