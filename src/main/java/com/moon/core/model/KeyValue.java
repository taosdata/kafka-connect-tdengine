package com.moon.core.model;

import com.moon.core.enums.EnumDescriptor;
import com.moon.core.model.getter.KeyGetter;
import com.moon.core.model.getter.KeyValueGetter;
import com.moon.core.model.getter.ValueGetter;
import com.moon.core.model.supplier.KeySupplier;
import com.moon.core.model.supplier.ValueSupplier;
import com.moon.core.util.ComparatorUtil;

import java.util.Objects;

/**
 * @author moonsky
 */
public final class KeyValue implements ValueSupplier<String>,
                                       KeySupplier<String>,
                                       KeyGetter,
                                       ValueGetter,
                                       KeyValueGetter,
                                       Comparable<KeyValue> {

    private String key;
    private String value;

    public KeyValue() { }

    public static KeyValue of() {return new KeyValue();}

    public KeyValue(String key, String value) {
        this.value = value;
        this.key = key;
    }

    public static KeyValue of(String key, String value) { return new KeyValue(key, value); }

    public static <T extends Enum<T>> KeyValue of(T enumItem) {
        if (enumItem instanceof EnumDescriptor) {
            EnumDescriptor item = (EnumDescriptor) enumItem;
            return of(item.getName(), item.getText());
        } else {
            return of(enumItem.name(), enumItem.name());
        }
    }

    public static KeyValue of(IdName keyValue) {
        return keyValue == null ? of() : of(keyValue.getId(), keyValue.getName());
    }

    public IdName toIDName() { return IdName.of(this); }

    @Override
    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    @Override
    public final String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyValue keyValue = (KeyValue) o;
        return Objects.equals(key, keyValue.key) && Objects.equals(value, keyValue.value);
    }

    @Override
    public int hashCode() { return Objects.hash(key, value); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('{');
        sb.append("key='").append(key).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(KeyValue o) {
        return ComparatorUtil.comparing(this, o, KeyGetter::getKey, ValueGetter::getValue);
    }
}
