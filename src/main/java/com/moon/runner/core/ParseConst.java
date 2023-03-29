package com.moon.runner.core;

import com.moon.core.lang.ParseSupportUtil;
import com.moon.core.lang.ref.IntAccessor;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
class ParseConst {
    private ParseConst() { noInstanceError(); }

    final static AsValuer parseStr(char[] chars, IntAccessor indexer, int endChar) {
        return DataConst.get(ParseSupportUtil.parseStr(chars, indexer, endChar));
    }

    final static AsValuer parseNum(char[] chars, IntAccessor indexer, int len, int current) {
        return DataConst.get(ParseSupportUtil.parseNum(chars, indexer, len, current));
    }
}
