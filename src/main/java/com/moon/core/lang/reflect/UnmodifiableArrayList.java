package com.moon.core.lang.reflect;

import com.moon.core.util.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author ZhangDongMin
 */
class UnmodifiableArrayList<T> extends ArrayList<T>
    implements List<T>, RandomAccess, Cloneable,
    java.io.Serializable, Unmodifiable<T> {

    private static final long serialVersionUID = 868345258112289218L;

    private boolean canModify = true;

    UnmodifiableArrayList() {
        super();
    }

    UnmodifiableArrayList(Collection<T> collection) {
        super(collection);
    }

    UnmodifiableArrayList(T[] elementData) {
        super(elementData.length);
        for (T item : elementData) {
            add(item);
        }
    }

    static <T> UnmodifiableArrayList<T> unmodifiable(T[] elementData) {
        return new UnmodifiableArrayList(elementData).flipToUnmodify();
    }

    static <T> UnmodifiableArrayList<T> ofCollect(Object collect) {
        if (collect instanceof Collection) {
            return new UnmodifiableArrayList<>((Collection) collect);
        } else if (collect == null) {
            return new UnmodifiableArrayList<>();
        } else if (collect.getClass().isArray()) {
            return new UnmodifiableArrayList((Object[]) collect);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean add(T t) {
        if (canModify) {
            return super.add(t);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public T set(int index, T element) {
        if (canModify) {
            return super.set(index, element);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void add(int index, T element) {
        if (canModify) {
            super.add(index, element);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public T remove(int index) {
        if (canModify) {
            return super.remove(index);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void clear() {
        if (canModify) {
            super.clear();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (canModify) {
            return super.addAll(index, c);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new UnmodifiableArrayList<>(super.subList(fromIndex, toIndex)).flipToUnmodify();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        if (canModify) {
            super.removeRange(fromIndex, toIndex);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean remove(Object o) {
        if (canModify) {
            return super.remove(o);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (canModify) {
            return super.addAll(c);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (canModify) {
            return super.removeAll(c);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (canModify) {
            return super.retainAll(c);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        if (canModify) {
            super.replaceAll(operator);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void sort(Comparator<? super T> c) {
        if (canModify) {
            super.sort(c);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        if (canModify) {
            return super.removeIf(filter);
        } else {
            return false;
        }
    }

    @Override
    public UnmodifiableArrayList flipToUnmodify() {
        this.canModify = false;
        return this;
    }
}
