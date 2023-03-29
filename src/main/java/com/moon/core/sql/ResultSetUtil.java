package com.moon.core.sql;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldDescriptor;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.ReferenceUtil;
import com.moon.core.lang.reflect.ConstructorUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ResultSetUtil {

    private static Map<Object, Object> CACHE = ReferenceUtil.manageMap();

    private ResultSetUtil() { noInstanceError(); }

    public static Map<String, Object> rowToMap(ResultSet set, String... columnsLabel) {
        try {
            String column;
            Map<String, Object> ret = new HashMap<>();
            for (int i = 0; i < columnsLabel.length; i++) {
                column = columnsLabel[i];
                ret.put(column, set.getObject(column));
            }
            return ret;
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    /**
     * 将当前行映射成一个 Map 对象
     *
     * @param set 结果集
     *
     * @return 当前行数据
     */
    public static Map<String, Object> rowToMap(ResultSet set) { return rowToMap(set, getColumnsLabel(set)); }

    /**
     * 将剩余行每行映射成一个 Map 对象，所有数据用一个 List 返回
     *
     * @param set sql 结果集
     *
     * @return map 对象
     */
    public static List<Map<String, Object>> restToMap(ResultSet set) {
        try {
            String[] columns = getColumnsLabel(set);
            List<Map<String, Object>> ret = new ArrayList<>();
            while (set.next()) {
                ret.add(rowToMap(set, columns));
            }
            return ret;
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    /**
     * 将当前行用一个数组包装返回
     *
     * @param set sql 结果集
     *
     * @return 数据数组
     */
    public static Object[] rowToArray(ResultSet set) {
        try {
            int count = getColumnsCount(set);
            Object[] ret = new Object[count];
            for (int i = 0; i < count; i++) {
                ret[i] = set.getObject(i + 1);
            }
            return ret;
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    /**
     * 将所有剩余行都用数组包装，放进 List 返回
     *
     * @param set sql 结果集
     *
     * @return 数组集合
     */
    public static List<Object[]> restToArray(ResultSet set) {
        try {
            int count = getColumnsCount(set);
            List<Object[]> ret = new ArrayList<>();
            while (set.next()) {
                Object[] row = new Object[count];
                for (int i = 0; i < count; i++) {
                    row[i] = set.getObject(i + 1);
                }
                ret.add(row);
            }
            return ret;
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    /**
     * 将当前行按列名和字段名对应放进一个实体里面
     *
     * @param set  sql 结果集
     * @param bean 将要放入数据的实例
     * @param <T>  泛型
     *
     * @return 实例 bean
     */
    public static <T> T rowToBean(ResultSet set, T bean) {
        BeanInfoUtil.getFieldDescriptorsMap(bean.getClass())
            .forEach((name, descriptor) -> descriptor.ifSetterPresent(getConsumer(set, bean)));
        return bean;
    }

    /**
     * 将当前行按列名和字段名对应放进一个类的实例里面
     *
     * @param set  sql 结果集
     * @param type 将要转化的类型
     * @param <T>  泛型
     *
     * @return 转换后的 type 实例
     */
    public static <T> T rowToInstance(ResultSet set, Class<T> type) {
        T bean = ConstructorUtil.newInstance(type);
        Consumer<FieldDescriptor> consumer = getConsumer(set, bean);
        BeanInfoUtil.getFieldDescriptorsMap(type).forEach((name, descriptor) -> descriptor.ifSetterPresent(consumer));
        return bean;
    }

    /**
     * 将所有行按列名和字段名对应放进一个类的实例
     *
     * @param set  sql 结果集
     * @param type 将要转化的类型
     * @param <T>  泛型
     *
     * @return 转换后的 type 实例
     */
    public static <T> List<T> restToInstance(ResultSet set, Class<T> type) {
        try {
            Map<String, FieldDescriptor> descMap = BeanInfoUtil.getFieldDescriptorsMap(type);
            List<T> ret = new ArrayList<>();
            while (set.next()) {
                T bean = ConstructorUtil.newInstance(type);
                Consumer<FieldDescriptor> consumer = getConsumer(set, bean);
                descMap.forEach((name, desc) -> desc.ifSetterPresent(consumer));
                ret.add(bean);
            }
            return ret;
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }


    /**
     * 获取当前 ResultSet 的列名数组
     *
     * @param set sql 结果集
     *
     * @return 列名列表
     */
    public static String[] getColumnsLabel(ResultSet set) {
        try {
            String[] arr = (String[]) CACHE.get(set);
            if (arr == null) {
                ResultSetMetaData resultSetMetaData = set.getMetaData();
                int length = resultSetMetaData.getColumnCount();
                arr = new String[length];
                for (int i = 0; i < length; i++) {
                    arr[i] = resultSetMetaData.getColumnLabel(i + 1);
                }
            }
            return arr;
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    /**
     * 获取当前 ResultSet 的列数目
     *
     * @param set sql 结果集
     *
     * @return 列数
     */
    public static int getColumnsCount(ResultSet set) {
        try {
            return set.getMetaData().getColumnCount();
        } catch (Exception e) {
            return ThrowUtil.runtime(e);
        }
    }

    private static <T> Consumer<FieldDescriptor> getConsumer(ResultSet set, T bean) {
        return desc -> {
            try {
                desc.setValue(bean, set.getObject(desc.getName()));
            } catch (SQLException e) {
                ThrowUtil.runtime(e);
            }
        };
    }
}
