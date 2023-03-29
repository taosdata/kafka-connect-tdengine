package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.runner.RunnerSetting;

import java.util.ArrayList;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.runner.core.Constants.*;

/**
 * 方法调用参数解析
 *
 * @author moonsky
 */
final class ParseParams {
    private ParseParams() { noInstanceError(); }

    /**
     * 从左括号的下一个字符开始解析，右括号为止
     * 两个连续逗号之间默认有一个 null 值，
     * 最后一个逗号后如果是右圆括号，则忽略
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @return
     */
    final static AsRunner[] parse(char[] chars, IntAccessor indexer, int len, RunnerSetting settings) {
        int curr = ParseUtil.nextVal(chars, indexer, len);
        List params = new ArrayList();
        AsRunner runner;
        outer:
        for (int next = curr; ; curr = next) {
            switch (next) {
                case YUAN_R:
                    AsValuer[] runners = new AsValuer[params.size()];
                    return (AsValuer[]) params.toArray(runners);
                case SINGLE:
                case DOUBLE:
                    runner = ParseConst.parseStr(chars, indexer, next);
                    break;
                default:
                    runner = ParseCore.parse(chars, indexer.decrement(), len, settings, COMMA, YUAN_R);
                    if ((next = chars[indexer.get() - 1]) == YUAN_R) {
                        params.add(runner);
                        continue outer;
                    }
                    break;
            }
            params.add(runner);
            next = ParseUtil.nextVal(chars, indexer, len);
            if (next == COMMA && (runner != DataConst.NULL || (curr != COMMA && curr != YUAN_L))) {
                next = ParseUtil.nextVal(chars, indexer, len);
            }
        }
    }
}
