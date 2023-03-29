package com.moon.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author moonsky
 */
public class StringKeyHashMap<V> extends HashMap<String, V> implements PropertiesSupplier<V> {

    private static final long serialVersionUID = 362498820763181268L;

    public StringKeyHashMap(int initialCapacity, float loadFactor) { super(initialCapacity, loadFactor); }

    public StringKeyHashMap(int initialCapacity) { super(initialCapacity); }

    public StringKeyHashMap() { }

    public StringKeyHashMap(Map<? extends String, ? extends V> m) { super(m); }

    public StringKeyHashMap(Map<? extends String, ? extends V>... maps) {
        super(MapUtil.sizeOfAll(maps));
        putAll(maps);
    }

    /*
     * --------------------------------------------------------------------------------
     * methods
     * --------------------------------------------------------------------------------
     */

    public void putAll(Map<? extends String, ? extends V>... maps) { MapUtil.putAll(this, maps); }

    public Iterator<String> propertyNames() { return keySet().iterator(); }
}
