package com.moon.core.util;

import com.moon.core.enums.Const;
import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.moon.core.lang.StringUtil.stringify;
import static com.moon.core.lang.ThrowUtil.unsupported;
import static java.util.Collections.EMPTY_SET;

/**
 * @author moonsky
 */
public class PropertiesGroup extends HashMap<String, Object> implements PropertiesSupplier {

    private static final long serialVersionUID = 362498820763181266L;

    private final static PropertiesGroup EMPTY = new EmptyGroup();
    private final static boolean ORDERED = false;

    private Object defaultValue;
    private boolean hasDefaultValue = false;

    private final PropertiesGroup top;
    private final PropertiesGroup parent;
    private final Map<String, Object> ordered;

    private PropertiesGroup(PropertiesGroup top, PropertiesGroup parent, boolean ordered) {
        this.ordered = ordered ? new LinkedHashMap<>() : null;
        this.parent = parent == null ? this : parent;
        this.top = top == null ? this : top;
    }

    private PropertiesGroup(
        Map<? extends String, ?> properties, PropertiesGroup top, PropertiesGroup parent, boolean ordered
    ) {
        this(top, parent, ordered);
        putAll(properties);
    }

    private PropertiesGroup(Map<? extends String, ?> properties, PropertiesGroup top, PropertiesGroup parent) {
        this(properties, top, parent, ORDERED);
    }

    public PropertiesGroup() { this(ORDERED); }

    public PropertiesGroup(boolean ordered) { this(null, ordered); }

    public PropertiesGroup(Map<? extends String, ?> properties) { this(properties, null, null); }

    public PropertiesGroup(Map<? extends String, ?> properties, boolean ordered) {
        this(properties, null, null, ordered);
    }

    public final static PropertiesGroup of() { return of(ORDERED); }

    public final static PropertiesGroup of(boolean ordered) { return new PropertiesGroup(ordered); }

    public final static PropertiesGroup of(Map<? extends String, ?> properties) {
        return new PropertiesGroup(properties);
    }

    private final boolean isOrdered() { return ordered != null; }

    /*
     * ----------------------------------------------------------------------------
     * default
     * ----------------------------------------------------------------------------
     */

    private Object setDefault(Object value) {
        Object old = this.defaultValue;
        this.hasDefaultValue = true;
        this.defaultValue = value;
        return old;
    }

    private Object getDefault() { return defaultValue; }

    private boolean isHasDefault() { return hasDefaultValue; }

    /*
     * ----------------------------------------------------------------------------
     * overrides
     * ----------------------------------------------------------------------------
     */

    @Override
    public Object get(Object key) {
        Object value;
        if (key instanceof String) {
            String nowKey = key.toString();
            int index = nowKey.indexOf('.');
            value = index < 0 ? superGet(key) : ensureGetChild(nowKey.substring(0, index), false)
                .get(nowKey.substring(index + 1));
        } else {
            value = superGet(key);
        }
        return formatValue(value);
    }

    private Object superPut(String key, Object value) {
        return isOrdered() ? ordered.put(key, value) : super.put(key, value);
    }

    private Object superGet(Object key) { return isOrdered() ? ordered.get(key) : super.get(key); }

    private Object simplePut(String key, Object value) {
        PropertiesGroup group = getChildOrNull(key);
        return group == EMPTY ? superPut(key, value) : group.setDefault(value);
    }

    @Override
    public Object put(String key, Object value) {
        int index = key.indexOf('.');
        return index < 0 ? simplePut(key, value) : ensureGetChild(key.substring(0, index))
            .put(key.substring(index + 1), value);
    }

    @Override
    public void putAll(Map<? extends String, ?> properties) {
        if (properties != null) { properties.forEach(this::put); }
    }

    private PropertiesGroup createEmptyChild() { return new PropertiesGroup(top, this, isOrdered()); }

    private PropertiesGroup ensureGetChild(String key) { return ensureGetChild(key, true); }

    private PropertiesGroup ensureGetChild(String key, boolean create) {
        PropertiesGroup group = getChildOrNull(key);
        if (group == EMPTY && create) {
            superPut(key, group = createEmptyChild());
        }
        return group;
    }

    private PropertiesGroup getChildOrNull(String key) {
        Object present = superGet(key);
        if (present == null) {
            return EMPTY;
        }
        if (present instanceof PropertiesGroup) {
            return (PropertiesGroup) present;
        } else {
            PropertiesGroup group = createEmptyChild();
            group.setDefault(present);
            superPut(key, group);
            return group;
        }
    }

    private Object formatValue(Object value) {
        if (value instanceof PropertiesGroup) {
            PropertiesGroup group = (PropertiesGroup) value;
            return group.isHasDefault() ? group.getDefault() : group;
        }
        return value;
    }

    /*
     * ----------------------------------------------------------------------------
     * get group
     * ----------------------------------------------------------------------------
     */

    public PropertiesGroup getParent() { return parent; }

    public PropertiesGroup getTop() { return top; }

    public PropertiesGroup getChildGroup(String key) { return ensureGetChild(key); }

    public Set<PropertiesGroup> getChildrenGroups() {
        Set groups = new HashSet();
        forEach((key, value) -> {
            if (value instanceof PropertiesGroup) {
                groups.add(value);
            } else {
                PropertiesGroup group = createEmptyChild();
                group.setDefault(value);
                groups.add(group);
            }
        });
        return groups;
    }

    /*
     * ----------------------------------------------------------------------------
     * get value
     * ----------------------------------------------------------------------------
     */

    public Object getByKeys(String... keys) {
        final int length = keys == null ? 0 : keys.length;
        if (length > 0) {
            Object value;
            PropertiesGroup group = this;
            int last = length - 1;
            for (int i = 0; i < last; i++) {
                value = group.superGet(keys[i]);
                if (value instanceof PropertiesGroup) {
                    group = (PropertiesGroup) value;
                } else if (value == null) {
                    return null;
                } else {
                    ThrowUtil.runtime(JoinerUtil.join(keys));
                }
            }
            return formatValue(group.superGet(keys[last]));
        }
        return isHasDefault() ? getDefault() : null;
    }

    public String getString(String... keys) { return stringify(getByKeys(keys)); }

    public <T> T getAndTransform(Function<String, T> transformer, String... keys) {
        return transformer.apply(getString(keys));
    }

    private static class EmptyGroup extends PropertiesGroup {

        private EmptyGroup() { }

        private EmptyGroup(Map<? extends String, ?> m) { }

        @Override
        public Object get(Object key) { return null; }

        @Override
        public Object put(String key, Object value) { return null; }

        @Override
        public void putAll(Map<? extends String, ?> properties) { }

        @Override
        public PropertiesGroup getParent() { return this; }

        @Override
        public PropertiesGroup getTop() { return this; }

        @Override
        public PropertiesGroup getChildGroup(String key) { return this; }

        @Override
        public Set<PropertiesGroup> getChildrenGroups() { return EMPTY_SET; }

        @Override
        public Object getByKeys(String... keys) { return null; }

        @Override
        public String getString(String... keys) { return null; }

        @Override
        public <T> T getAndTransform(Function<String, T> transformer, String... keys) {
            return transformer.apply(null);
        }

        @Override
        public String getString(String key) { return null; }

        @Override
        public String getOrEmpty(String key) { return Const.EMPTY; }

        @Override
        public String getOrNull(String key) { return null; }

        @Override
        public int getInt(String key) { return 0; }

        @Override
        public int getOrZero(String key) { return 0; }

        @Override
        public int getOrOne(String key) { return 1; }

        @Override
        public int getOrDefaultAsInt(String key, int defaultValue) { return 0; }

        @Override
        public long getLong(String key) { return 0; }

        @Override
        public long getOrDefaultAsLong(String key, long defaultValue) { return 0; }

        @Override
        public boolean getBoolean(String key) { return false; }

        @Override
        public boolean getOrTrue(String key) { return true; }

        @Override
        public boolean getOrFalse(String key) { return false; }

        @Override
        public boolean getOrDefaultAsBoolean(String key, boolean defaultVal) { return defaultVal; }

        @Override
        public Object getAndTransform(String key, Function transformer) { return transformer.apply(null); }

        @Override
        public int size() { return 0; }

        @Override
        public boolean isEmpty() { return true; }

        @Override
        public boolean containsKey(Object key) { return false; }

        @Override
        public Object remove(Object key) { return null; }

        @Override
        public void clear() { }

        @Override
        public boolean containsValue(Object value) { return false; }

        @Override
        public Set<String> keySet() { return EMPTY_SET; }

        @Override
        public Collection<Object> values() { return EMPTY_SET; }

        @Override
        public Set<Entry<String, Object>> entrySet() { return EMPTY_SET; }

        @Override
        public Object getOrDefault(Object key, Object defaultValue) { return null; }

        @Override
        public Object putIfAbsent(String key, Object value) { return null; }

        @Override
        public boolean remove(Object key, Object value) { return false; }

        @Override
        public boolean replace(String key, Object oldValue, Object newValue) { return false; }

        @Override
        public Object replace(String key, Object value) { return null; }

        @Override
        public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) { return null; }

        @Override
        public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
            return null;
        }

        @Override
        public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
            return null;
        }

        @Override
        public Object merge(
            String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction
        ) {
            return unsupported();
        }

        @Override
        public void forEach(BiConsumer<? super String, ? super Object> action) { }

        @Override
        public void replaceAll(BiFunction<? super String, ? super Object, ?> function) { }

        @Override
        public Object clone() { return this; }

        @Override
        public boolean equals(Object o) { return o == this; }

        @Override
        public int hashCode() { return System.identityHashCode(this); }

        @Override
        public String toString() { return "{}"; }
    }
}
