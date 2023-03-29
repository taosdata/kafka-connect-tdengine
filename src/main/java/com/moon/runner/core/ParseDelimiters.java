package com.moon.runner.core;

import com.moon.core.lang.ref.ReferenceUtil;
import com.moon.core.util.ValidateUtil;
import com.moon.runner.RunnerSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
final class ParseDelimiters {
    private ParseDelimiters() { noInstanceError(); }

    private final static Map<String, AsRunner> CACHE = ReferenceUtil.manageMap();

    final static AsRunner parse(String expression, String[] delimiters) {
        AsRunner parsed = CACHE.get(expression);
        if (parsed == null) {
            parsed = parseCore(expression, delimiters, null);
            synchronized (CACHE) {
                if (CACHE.get(expression) == null) {
                    CACHE.put(expression, parsed);
                }
            }
        }
        return parsed;
    }

    final static AsRunner parse(String expression, String[] delimiters, RunnerSetting settings) {
        return parseCore(expression, delimiters, settings);
    }

    private static AsRunner parseCore(String str, String[] ds, RunnerSetting sets) {
        String begin = ValidateUtil.requireNotBlank(ds[0]);
        String ender = ValidateUtil.requireNotBlank(ds[1]);
        final int length = str.length(),
            beginLen = begin.length(), endLen = ender.length();
        int from = str.indexOf(begin), to = str.indexOf(ender);
        int one = 0, temp, size;
        if (from == 0 && to + endLen == length && str.indexOf(begin, from + 1) < 0) {
            return ParseCore.parse(str.substring(from + beginLen, to), sets);
        } else if (from < 0) {
            return DataConst.get(str);
        } else {
            List<AsRunner> list = new ArrayList<>();
            for (; from > 0; ) {
                if (from > one) {
                    list.add(DataConst.get(str.substring(one, from)));
                }
                if (to > (temp = from + beginLen)) {
                    list.add(ParseCore.parse(str.substring(temp, to), sets));
                }
                one = to + endLen;
                from = str.indexOf(begin, one);
                to = str.indexOf(ender, from);
            }
            if (one < length) {
                list.add(DataConst.get(str.substring(one, length)));
            }
            if ((size = list.size()) == 0) {
                return DataConst.get(null);
            } else {
                AsRunner[] arr = list.toArray(new AsRunner[size]);
                AsRunner handler = new InRunner(arr);
                for (AsRunner current : arr) {
                    if (!current.isConst()) {
                        return handler;
                    }
                }
                return DataConst.get(handler.run());
            }
        }
    }

    private static class InRunner implements AsGetter {
        final AsRunner[] gets;

        private InRunner(AsRunner[] gets) { this.gets = gets; }

        @Override
        public Object run(Object data) {
            AsRunner[] getters = this.gets;
            int length = getters.length;
            StringBuilder builder = new StringBuilder(length * 16);
            Object item;
            for (int i = 0; i < length; i++) {
                item = getters[i].run(data);
                builder.append(item);
            }
            return builder.toString();
        }
    }
}
