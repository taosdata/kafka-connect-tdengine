package com.moon.core.lang.ref;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.HashMap;

/**
 * 推荐使用{@link Table}、{@link TableImpl}
 *
 * @author moonsky
 */
@Deprecated
public class HashLocation<X, Y, Z> extends DefaultLocation<X, Y, Z> {

    public HashLocation() { super(HashMap::new); }

    public final static <X, Y, Z> HashLocation<X, Y, Z> of() { return new HashLocation<>(); }

    public final static <X, Y, Z> HashLocation<X, Y, Z> ofManaged() { return new HashLocation<>(); }
}
