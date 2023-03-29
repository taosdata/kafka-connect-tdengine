package com.moon.core.lang.ref;

import com.moon.core.lang.ThrowUtil;

import java.util.Map;

import static com.moon.core.lang.StackTraceUtil.getSkipCallerTypeName;

/**
 * @author moonsky
 */
@Deprecated
final class WeakMapManager implements Runnable {

    private static WeakMapManager CURRENT;

    private int index = -1;
    private long sss = System.currentTimeMillis();
    private final static int amount = 60 * 1000;
    private final static int length = 1024;
    private final Map[] maps = new Map[length];

    /**
     * 只能被{@link ReferenceUtil}访问
     */
    private WeakMapManager() {
        if (!getSkipCallerTypeName().equals(ReferenceUtil.class.getName())) {
            ThrowUtil.rejectAccessError();
        }
    }

    final static synchronized <K, V, M extends Map<K, V>> M manage(M m) {
        WeakMapManager current = CURRENT;
        current = current == null ? new WeakMapManager() : current;
        int idx = current.index;
        if (++idx > length) {
            CURRENT = current = new WeakMapManager();
            idx = ++current.index;
        }
        current.maps[idx] = m;
        return m;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        if (now - sss > amount) {
            for (int i = 0, to = index; i < to; i++) {
                maps[i].size();
            }
            sss = System.currentTimeMillis();
        }
    }
}
