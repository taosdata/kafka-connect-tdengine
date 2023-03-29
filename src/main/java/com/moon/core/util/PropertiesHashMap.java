package com.moon.core.util;

import java.util.Map;

import static com.moon.core.lang.StringUtil.stringify;

/**
 * @author moonsky
 */
public class PropertiesHashMap extends StringKeyHashMap<String> {

    private static final long serialVersionUID = 362498820763181265L;

    public PropertiesHashMap(int initialCapacity, float loadFactor) { super(initialCapacity, loadFactor); }

    public PropertiesHashMap(int initialCapacity) { super(initialCapacity); }

    public PropertiesHashMap() { }

    public PropertiesHashMap(Map<? extends String, ? extends String> m) { super(m); }

    public PropertiesHashMap(Map<? extends String, ? extends String>... maps) { super(maps); }

    public final static PropertiesHashMap fromObjectMap(Map map) {
        if (map == null) { return new PropertiesHashMap(); }
        PropertiesHashMap props = new PropertiesHashMap(map.size());
        map.forEach((key, value) -> props.put(stringify(key), stringify(value)));
        return props;
    }
}
