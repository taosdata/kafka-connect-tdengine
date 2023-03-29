package com.moon.core.util;

import com.moon.core.util.function.TableConsumer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.EMPTY_SET;

/**
 * @author moonsky
 */
public class TableImpl<X, Y, Z> implements Table<X, Y, Z> {

    protected transient Map<X, Map<Y, Z>> table;

    protected transient Supplier<Map<X, Map<Y, Z>>> containerCreator;

    protected transient Function<Object, Map<Y, Z>> rowCreator;

    public TableImpl() { this(x -> new HashMap<>()); }

    public TableImpl(Function<Object, Map<Y, Z>> rowCreator) { this(HashMap::new, rowCreator); }

    public TableImpl(Supplier<Map<X, Map<Y, Z>>> containerCreator, Function<Object, Map<Y, Z>> rowCreator) {
        this.containerCreator = containerCreator;
        this.rowCreator = rowCreator;
    }

    public static <X, Y, Z> TableImpl<X, Y, Z> newHashTable() { return new TableImpl<>(); }

    public static <X, Y, Z> TableImpl<X, Y, Z> newLinkedHashTable() {
        return new TableImpl<>(() -> new LinkedHashMap<>(), x -> new LinkedHashMap<>());
    }

    public static <X, Y, Z> TableImpl<X, Y, Z> newWeakHashTable() {
        return new TableImpl<>(() -> new WeakHashMap<>(), x -> new WeakHashMap<>());
    }

    private Map ensureTable() {
        Map<X, Map<Y, Z>> table = this.table;
        if (table == null) {
            table = containerCreator.get();
            this.table = table;
        }
        return table;
    }

    private Map<Y, Z> ensureRow(Object x) {
        Map<X, Map<Y, Z>> table = this.ensureTable();
        Map row = table.get(x);
        if (row == null) {
            row = rowCreator.apply(x);
            table.put((X) x, row);
        }
        return row;
    }

    private Map<Y, Z> nullableRow(Object x) {
        Map<X, Map<Y, Z>> table = this.table;
        return table == null ? null : table.get(x);
    }

    @Override
    public Z put(X x, Y y, Z z) { return ensureRow(x).put(y, z); }

    @Override
    public Z putIfAbsent(X x, Y y, Z z) { return ensureRow(x).putIfAbsent(y, z); }

    @Override
    public Z get(Object x, Object y) {
        Map<Y, Z> row = nullableRow(x);
        return row == null ? null : row.get(y);
    }

    @Override
    public Map<Y, Z> put(X x, Map<? extends Y, ? extends Z> map) {
        Map<X, Map<Y, Z>> table = this.ensureTable();
        return table.put(x, (Map) map);
    }

    @Override
    public void putAll(X x, Map<? extends Y, ? extends Z> map) {
        Map row = ensureRow(x);
        row.putAll(map);
    }

    @Override
    public Map<Y, Z> get(Object x) { return nullableRow(x); }

    @Override
    public void putAll(Table<? extends X, ? extends Y, ? extends Z> table) {
        if (table != null) {
            X x;
            Map row, inputRow;
            Map<X, Map<Y, Z>> present = this.ensureTable();
            for (Map.Entry<? extends X, ? extends Map<? extends Y, ? extends Z>> entry : table.rowsEntrySet()) {
                inputRow = entry.getValue();
                x = entry.getKey();
                row = present.get(x);
                if (row == null) {
                    if (inputRow == null) {
                        present.put(x, null);
                    } else {
                        row = rowCreator.apply(x);
                        row.putAll(inputRow);
                        present.put(x, row);
                    }
                } else {
                    if (inputRow != null) {
                        row.putAll(inputRow);
                    }
                }
            }
        }
    }

    @Override
    public Z remove(Object x, Object y) {
        Map<Y, Z> row = nullableRow(x);
        if (row == null) {
            return null;
        } else {
            return row.remove(y);
        }
    }

    @Override
    public Map<Y, Z> remove(Object x) { return table == null ? null : table.remove(x); }

    @Override
    public boolean containsValue(Object value) {
        Map<X, Map<Y, Z>> table = this.table;
        if (table == null) {
            return false;
        } else {
            for (Map.Entry<X, Map<Y, Z>> xMapEntry : table.entrySet()) {
                Map<Y, Z> row = xMapEntry.getValue();
                if (row.containsValue(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<X> keys() { return table == null ? EMPTY_SET : table.keySet(); }

    @Override
    public Collection<Map<Y, Z>> rows() { return table == null ? EMPTY_SET : table.values(); }

    @Override
    public Set<Map.Entry<X, Map<Y, Z>>> rowsEntrySet() { return table == null ? EMPTY_SET : table.entrySet(); }

    @Override
    public void forEach(TableConsumer<X, Y, Z> consumer) {
        Set<Map.Entry<X, Map<Y, Z>>> entries = rowsEntrySet();
        for (Map.Entry<X, Map<Y, Z>> entry : entries) {
            Map<Y, Z> row = entry.getValue();
            X x = entry.getKey();
            if (row != null) {
                row.forEach((key, value) -> consumer.accept(x, key, value));
            }
        }
    }

    @Override
    public void clear() { this.table = null; }

    @Override
    public int sizeOfRows() { return table == null ? 0 : table.size(); }

    @Override
    public int maxSizeOfColumns() { return obtainSize(0, Math::max); }

    @Override
    public int minSizeOfColumns() { return obtainSize(Integer.MAX_VALUE, Math::min); }

    @Override
    public int size() { return obtainSize(0, TableImpl::sum); }

    public TableImpl<X, Y, Z> sandbox(
        Function<Object, Map<Y, Z>> rowCreator,
        Consumer<? super TableImpl<? extends X, ? extends Y, ? extends Z>> consumer
    ) {
        Function supplier = this.rowCreator;
        this.rowCreator = rowCreator;
        consumer.accept(this);
        this.rowCreator = supplier;
        return this;
    }

    private int obtainSize(int initialize, IntCalculator calculator) {
        Map<X, Map<Y, Z>> table = this.table;
        if (table == null || table.isEmpty()) {
            return 0;
        } else {
            int size = initialize;
            for (Map.Entry<X, Map<Y, Z>> xMapEntry : table.entrySet()) {
                Map row = xMapEntry.getValue();
                if (row != null && !row.isEmpty()) {
                    size = calculator.calc(size, row.size());
                }
            }
            return size;
        }
    }

    private static int sum(int v1, int v2) { return v1 + v2; }

    interface IntCalculator {

        int calc(int prev, int size);
    }
}
