package com.moon.runner.core;

import com.moon.core.lang.ParseSupportUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.runner.RunnerSetting;
import com.moon.runner.core.InvokeEnsure.EnsureArgs0;

import java.lang.reflect.Method;
import java.util.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
final class ParseGetter {
    private ParseGetter() { noInstanceError(); }

    private final static Map<Class, Set<String>> EXCLUDE_METHODS = new HashMap<>();

    static {
        EXCLUDE_METHODS.put(System.class, new HashSet() {{
            add("exist");
        }});
    }

    /**
     * 返回一个 valuer
     *
     * @param chars
     * @param indexer
     * @param len
     * @param current
     * @return
     */
    final static AsValuer parseVar(char[] chars, IntAccessor indexer, int len, int current) {
        String str = ParseSupportUtil.parseVar(chars, indexer, len, current);
        switch (str) {
            case "null":
                return DataConst.NULL;
            case "true":
                return DataConst.TRUE;
            case "false":
                return DataConst.FALSE;
            default:
                return new GetOrdinary(str);
        }
    }

    /**
     * 返回一个 getter
     *
     * @param chars
     * @param indexer
     * @param len
     * @return
     */
    private final static AsValuer parseDot(char[] chars, IntAccessor indexer, int len) {
        int curr = ParseUtil.nextVal(chars, indexer, len);
        ParseUtil.assertTrue(ParseUtil.isVar(curr), chars, indexer);
        return parseVar(chars, indexer, len, curr);
    }

    final static AsRunner parseDot(
        char[] chars, IntAccessor indexer, int len, RunnerSetting sets, AsRunner prev
    ) {
        AsValuer prevValuer = (AsValuer) prev;
        AsRunner handler = parseDot(chars, indexer, len);
        ParseUtil.assertTrue(handler.isValuer(), chars, indexer);
        AsRunner invoker = ParseInvoker.tryParseInvoker(chars,
            indexer, len, sets, handler.toString(), prevValuer);
        return invoker == null ? new GetLink(prevValuer, (AsValuer) handler)
            : assertNotExistInvoker(chars, indexer, len, invoker);
    }

    /**
     * 禁用某些静态方法调用
     *
     * @param chars
     * @param indexer
     * @param len
     * @param invoker
     * @return
     */
    final static AsRunner assertNotExistInvoker(
        char[] chars, IntAccessor indexer, int len, AsRunner invoker
    ) {
        if (invoker.isInvoker() && invoker instanceof EnsureArgs0) {
            Method m = ((EnsureArgs0) invoker).method;
            Asserts.disableIllegalCallers(chars, indexer,
                len, m.getDeclaringClass(), m.getName());
        }
        return invoker;
    }

    final static AsRunner parseNot(
        char[] chars, IntAccessor indexer, int len, RunnerSetting sets
    ) {
        AsRunner valuer, tryLinked;
        int curr = ParseUtil.nextVal(chars, indexer, len);
        switch (curr) {
            case Constants.FANG_L:
                valuer = parseFang(chars, indexer, len, sets);
                break;
            case Constants.YUAN_L:
                valuer = parseYuan(chars, indexer, len, sets);
                break;
            case Constants.HUA_L:
                valuer = ParseCurly.parse(chars, indexer, len, sets);
                break;
            case Constants.CALLER:
                valuer = ParseCall.parse(chars, indexer, len, sets);
                break;
            default:
                if (ParseUtil.isVar(curr)) {
                    valuer = parseVar(chars, indexer, len, curr);
                    ParseUtil.assertFalse(valuer == DataConst.NULL, chars, indexer);
                    valuer = valuer == DataConst.TRUE ? DataConst.FALSE : DataConst.TRUE;
                } else {
                    valuer = ParseUtil.throwErr(chars, indexer);
                }
                break;
        }
        tryLinked = tryParseLinked(chars, indexer, len, sets, valuer);
        return tryLinked == valuer && tryLinked.isConst() ? flip(chars, indexer, tryLinked)
            : new GetNot(tryLinked);
    }

    private static AsRunner flip(char[] chars, IntAccessor indexer, AsRunner valuer) {
        ParseUtil.assertTrue(valuer instanceof DataBool, chars, indexer);
        return ((DataBool) valuer).flip();
    }

    final static AsRunner tryParseLinked(
        char[] chars, IntAccessor indexer, int len, RunnerSetting sets, AsRunner valuer
    ) {
        final int index = indexer.get();
        AsRunner next = valuer;
        for (int curr; ; ) {
            curr = ParseUtil.nextVal(chars, indexer, len);
            if (curr == Constants.DOT) {
                next = parseDot(chars, indexer, len, sets, next);
            } else if (curr == Constants.FANG_L) {
                next = parseFangToComplex(chars, indexer, len, sets, next);
            } else {
                if (next == valuer) {
                    indexer.set(index);
                }
                return next;
            }
        }
    }

    /**
     * 返回一个getter
     *
     * @param chars
     * @param indexer
     * @param len
     * @return
     */
    final static GetFang parseFang(char[] chars, IntAccessor indexer, int len, RunnerSetting sets) {
        AsRunner handler = ParseCore.parse(chars, indexer, len, sets, Constants.FANG_R);
        ParseUtil.assertTrue(handler.isValuer(), chars, indexer);
        return new GetFang((AsValuer) handler);
    }

    /**
     * 参考{@link ParseCore#core(char[], IntAccessor, int, RunnerSetting, int, LinkedList, LinkedList, AsRunner)}
     * case FANG_L: 的详细步骤
     *
     * @param chars
     * @param indexer
     * @param len
     * @param prevHandler
     * @return
     */
    private final static AsRunner parseFangToComplex(
        char[] chars, IntAccessor indexer, int len, RunnerSetting sets, AsRunner prevHandler
    ) {
        AsRunner handler = ParseGetter.parseFang(chars, indexer, len, sets);
        ParseUtil.assertTrue(prevHandler.isValuer(), chars, indexer);
        return ((GetFang) handler).toComplex(prevHandler);
    }

    final static AsRunner parseYuan(char[] chars, IntAccessor indexer, int len, RunnerSetting sets) {
        return ParseCore.parse(chars, indexer, len, sets, Constants.YUAN_R);
    }
}
