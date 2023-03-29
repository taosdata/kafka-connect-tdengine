package com.moon.runner.core;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldDescriptor;
import com.moon.core.enums.ArrayOperator;
import com.moon.core.enums.Arrays2;
import com.moon.core.lang.BooleanUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.MapUtil;
import com.moon.core.util.ValidateUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 从输入数据中获取值
 *
 * @author moonsky
 */
class GetOrdinary implements AsGetter {

    final Object key;
    final String message;
    final int index;

    private AsGetter getter;

    GetOrdinary(Object key) {
        BooleanUtil.requireFalse(key instanceof AsRunner);
        this.key = key;
        this.message = "Variable of: " + key;
        if (key instanceof Integer) {
            index = ((Number) key).intValue();
        } else {
            index = -1;
        }
    }

    @Override
    public boolean isGetterOrdinary() { return true; }

    public AsGetter getGetter(Object data) {
        AsGetter getter = this.getter;
        if (getter == null) {
            getter = resetGetter(data);
        }
        return getter;
    }

    @Override
    public Object run(Object data) {
        try {
            return getGetter(data).run(data);
        } catch (Exception e) {
            return resetGetter(data).run(data);
        }
    }

    public Object getKey() { return key; }

    @Override
    public String toString() { return String.valueOf(key); }

    private final static String ARR_LENGTH = "length";

    private AsGetter resetGetter(Object data) {
        Objects.requireNonNull(data, message);
        AsGetter getter;
        if (data instanceof Map) {
            getter = new MapGetter(key);
        } else if (data instanceof List) {
            ValidateUtil.requireFalse(index < 0, message);
            getter = new ListGetter(index);
        } else if (data.getClass().isArray()) {
            if (index < 0 && ARR_LENGTH.equals(key)) {
                getter = ArrayLenGetter.LENGTH;
            } else {
                ValidateUtil.requireFalse(index < 0, message);
                getter = new ArrayGetter(index);
            }
        } else if (data instanceof ResultSet) {
            getter = index < 0 ? new ResultLabelGetter(key) : new ResultIndexGetter(index);
        } else {
            ValidateUtil.requireTrue(key instanceof String, message);
            getter = new FieldGetter(key);
        }
        this.getter = getter;
        return getter;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     *
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Object o) {
        return getter == null ? resetGetter(o).test(o) : getter.test(o) || resetGetter(o).test(o);
    }

    /*
     * -------------------------------------------------------------
     * classes
     * -------------------------------------------------------------
     */

    private static class ResultIndexGetter implements AsGetter {

        private final int index;

        private ResultIndexGetter(int index) { this.index = index; }

        @Override
        public Object run(Object data) {
            try {
                return ((ResultSet) data).getObject(index);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public boolean test(Object o) { return o instanceof ResultSet; }
    }

    private static class ResultLabelGetter implements AsGetter {

        private final String label;

        private ResultLabelGetter(Object label) {
            BooleanUtil.requireTrue(label instanceof CharSequence);
            this.label = label.toString();
        }

        @Override
        public Object run(Object data) {
            try {
                return ((ResultSet) data).getObject(label);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public boolean test(Object o) { return o instanceof ResultSet; }
    }

    private static class MapGetter implements AsGetter {

        final Object key;

        MapGetter(Object key) { this.key = key; }

        /**
         * 使用外部数据
         *
         * @param data
         *
         * @return
         */
        @Override
        public Object run(Object data) { return MapUtil.getByObject(data, key); }

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param o the input argument
         *
         * @return {@code true} if the input argument matches the predicate,
         * otherwise {@code false}
         */
        @Override
        public boolean test(Object o) { return o instanceof Map; }
    }

    private static class ListGetter implements AsGetter {

        final int index;

        ListGetter(int index) { this.index = index; }

        /**
         * 使用外部数据
         *
         * @param data
         *
         * @return
         */
        @Override
        public Object run(Object data) { return ListUtil.getByObject(data, index); }

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param o the input argument
         *
         * @return {@code true} if the input argument matches the predicate,
         * otherwise {@code false}
         */
        @Override
        public boolean test(Object o) { return o instanceof List; }
    }

    private enum ArrayLenGetter implements AsGetter {
        LENGTH;

        @Override
        public Object run(Object data) { return Arrays2.getOrObjects(data).length(data); }
    }

    private static class ArrayGetter implements AsGetter {

        final int index;
        final String message;
        ArrayOperator getter;

        ArrayGetter(int index) {
            this.index = index;
            this.message = String.valueOf(index);
        }

        /**
         * 使用外部数据
         *
         * @param data
         *
         * @return
         */
        @Override
        public Object run(Object data) {
            Objects.requireNonNull(data, message);
            if (getter == null || getter.test(data)) {
                getter = reset(data);
            }
            return getter.get(data, index);
        }

        private ArrayOperator reset(Object data) { return getter = Arrays2.getOrObjects(data.getClass()); }

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param o the input argument
         *
         * @return {@code true} if the input argument matches the predicate,
         * otherwise {@code false}
         */
        @Override
        public boolean test(Object o) {
            return getter == null ? reset(o).test(o) : getter.test(o) || reset(o).test(o);
        }
    }

    private static class FieldGetter implements AsGetter {

        final String field;
        FieldDescriptor getter;

        FieldGetter(Object field) { this.field = field.toString(); }

        /**
         * 使用外部数据
         *
         * @param data
         *
         * @return
         */
        @Override
        public Object run(Object data) {
            Objects.requireNonNull(data, field);
            if (getter == null) {
                getter = BeanInfoUtil.getFieldDescriptor(data.getClass(), field);
            }
            try {
                return getter.getValue(data, true);
            } catch (Exception e) {
                return reset(data).getValue(data, true);
            }
        }

        private FieldDescriptor reset(Object data) {
            getter = BeanInfoUtil.getFieldDescriptor(data.getClass(), field);
            return getter;
        }

        /**
         * Evaluates this predicate on the given argument.
         *
         * @param o the input argument
         *
         * @return {@code true} if the input argument matches the predicate,
         * otherwise {@code false}
         */
        @Override
        public boolean test(Object o) {
            return getter == null ? reset(o).getDeclaringClass().isInstance(o) : getter.getDeclaringClass()
                .isInstance(o) || reset(o).getDeclaringClass().isInstance(o);
        }
    }
}
