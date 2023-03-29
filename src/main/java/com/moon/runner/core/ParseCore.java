package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.ReferenceUtil;
import com.moon.runner.RunnerSetting;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.IntPredicate;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.runner.core.Constants.*;

/**
 * 可能出现的位置：
 * 普通表达式、Map 或 List 的项、方法参数、方法执行对象
 *
 * @author moonsky
 */
class ParseCore {

    private ParseCore() { noInstanceError(); }

    private static final RunnerSetting DEFAULT_SETTINGS = RunnerSetting.of();

    static RunnerSetting toSettings(RunnerSetting argSettings) {
        return argSettings == null ? DEFAULT_SETTINGS : argSettings;
    }

    private final static Map<String, AsRunner> CACHE = ReferenceUtil.manageMap();

    private static final synchronized AsRunner putCache(String expression, AsRunner runner) {
        if (CACHE.get(expression) == null) {
            CACHE.put(expression, runner);
        }
        return runner;
    }

    /*
     * ----------------------------------------------------------------------
     * 对外入口 parse
     * ----------------------------------------------------------------------
     */

    final static AsRunner parse(String expression) {
        AsRunner runner = CACHE.get(expression);
        return runner == null ? (expression == null ? DataConst.NULL : parse(expression, null)) : runner;
    }

    final static AsRunner parse(String expression, RunnerSetting settings) {
        char[] chars = expression.trim().toCharArray();
        AsRunner runner = parse(chars, IntAccessor.of(), chars.length, toSettings(settings));
        return settings == null ? putCache(expression, runner) : runner;
    }

    /*
     * ----------------------------------------------------------------------
     * 内部使用 parse
     * ----------------------------------------------------------------------
     */

    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings
    ) {
        return parse(chars, indexer, len, settings, -1);
    }

    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, int end
    ) {
        return parse(chars, indexer, len, settings, end, -1);
    }

    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, int end0, int end1
    ) {
        return parse(chars, indexer, len, settings, end0, end1, -1);
    }

    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, int end0, int end1, int end2
    ) {
        return parse(chars, indexer, len, settings, end0, end1, end2, -1);
    }

    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, int end0, int end1, int end2, int end3
    ) {
        return parse(chars, indexer, len, settings, end0, end1, end2, end3, -1);
    }

    final static AsRunner parse(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSetting settings,
        int end0,
        int end1,
        int end2,
        int end3,
        int end4
    ) {
        return parse(chars, indexer, len, settings, end0, end1, end2, end3, end4, v -> false);
    }

    final static AsRunner parse(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSetting settings,
        int end0,
        int end1,
        int end2,
        int end3,
        int end4,
        IntPredicate tester
    ) {
        AsRunner runner = null;
        GetThree.Builder builder;
        LinkedList<AsRunner> values = new LinkedList<>(), methods = new LinkedList();
        for (int curr; indexer.get() < len; ) {
            curr = ParseUtil.nextVal(chars, indexer, len);
            if (curr == end0 || curr == end1 || curr == end2 || curr == end3 || curr == end4 || tester.test(curr)) {
                if (curr == YUAN_R) {
                    cleanTo(values, methods, Computes.YUAN_LEFT);
                }
                break;
            } else if (curr == ASK) {
                // ?
                builder = new GetThree.Builder(toRunner(values, methods));
                builder.setTrueRunner(parse(chars, indexer, len, settings, COLON));
                builder.setFalseRunner(parse(chars, indexer, len, settings, COLON, YUAN_R, HUA_R, FANG_R, COMMA));
                return builder.build();
            } else {
                runner = core(chars, indexer, len, settings, curr, values, methods, runner);
            }
        }
        return toRunner(values, methods);
    }

    /*
     * ----------------------------------------------------------------------
     * 本类使用
     * ----------------------------------------------------------------------
     */

    private final static AsRunner toRunner(
        LinkedList<AsRunner> values, LinkedList<AsRunner> methods
    ) {
        return GetCalc.valueOf(cleanTo(values, methods, null));
    }

    /**
     * 表达式解析核心
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param curr
     * @param values
     * @param methods
     * @param prevHandler
     *
     * @return
     */
    private final static AsRunner core(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSetting settings,
        int curr,
        LinkedList<AsRunner> values,
        LinkedList<AsRunner> methods,
        AsRunner prevHandler
    ) {
        AsRunner run;
        if (ParseUtil.isStr(curr)) {
            // 单引号或双引号
            values.add(run = ParseConst.parseStr(chars, indexer, curr));
        } else if (ParseUtil.isNum(curr)) {
            // 数字
            values.add(run = ParseConst.parseNum(chars, indexer, len, curr));
        } else if (ParseUtil.isVar(curr)) {
            // 变量名（关键字: null、true、false）
            values.add(run = ParseGetter.parseVar(chars, indexer, len, curr));
        } else {
            switch (curr) {
                case PLUS:
                    // +
                    run = casSymbol(values, methods, Computes.PLUS);
                    break;
                case MINUS:
                    // -
                    if (prevHandler == null || prevHandler.isHandler()) {
                        values.add(run = ParseOpposite.parse(chars, indexer, len, settings));
                    } else {
                        run = casSymbol(values, methods, Computes.MINUS);
                    }
                    break;
                case MULTI:
                    // *
                    run = casSymbol(values, methods, Computes.MULTI);
                    break;
                case DIVIDE:
                    // /
                    run = casSymbol(values, methods, Computes.DIVIDE);
                    break;
                case MOD:
                    // %
                    run = casSymbol(values, methods, Computes.MOD);
                    break;
                case NOT_OR:
                    // ^
                    run = casSymbol(values, methods, Computes.NOT_OR);
                    break;
                case EQ:
                    // ==
                    ParseUtil.assertTrue(chars[indexer.getAndIncrement()] == EQ, chars, indexer);
                    run = casSymbol(values, methods, Computes.EQ);
                    break;
                case GT:
                    // >、>=、>>、>>>
                    if (chars[indexer.get()] == GT) {
                        if (chars[indexer.incrementAndGet()] == GT) {
                            indexer.increment();
                            run = casSymbol(values, methods, Computes.UN_BIT_RIGHT);
                        } else {
                            run = casSymbol(values, methods, Computes.BIT_RIGHT);
                        }
                    } else {
                        run = toGtLtAndOr(chars, indexer, values, methods, EQ, Computes.GT_OR_EQ, Computes.GT);
                    }
                    break;
                case LT:
                    // <、<=、<<
                    if (chars[indexer.get()] == LT) {
                        indexer.increment();
                        run = casSymbol(values, methods, Computes.BIT_LEFT);
                    } else {
                        run = toGtLtAndOr(chars, indexer, values, methods, EQ, Computes.LT_OR_EQ, Computes.LT);
                    }
                    break;
                case AND:
                    // && 、&
                    run = toGtLtAndOr(chars, indexer, values, methods, AND, Computes.AND, Computes.BIT_AND);
                    break;
                case OR:
                    // || 、|
                    run = toGtLtAndOr(chars, indexer, values, methods, OR, Computes.OR, Computes.BIT_OR);
                    break;
                case NOT:
                    // !
                    if (chars[indexer.get()] == EQ) {
                        indexer.increment();
                        run = casSymbol(values, methods, Computes.NOT_EQ);
                    } else {
                        values.add(run = ParseGetter.parseNot(chars, indexer, len, settings));
                    }
                    break;
                case CALLER:
                    // @
                    values.add(run = ParseCall.parse(chars, indexer, len, settings));
                    break;
                case DOT:
                    // .
                    values.add(run = ParseGetter.parseDot(chars, indexer, len, settings, values.pollLast()));
                    break;
                case HUA_L:
                    // {
                    values.add(run = ParseCurly.parse(chars, indexer, len, settings));
                    break;
                case FANG_L:
                    // [
                    run = ParseGetter.parseFang(chars, indexer, len, settings);
                    if (prevHandler != null && prevHandler.isValuer()) {
                        ParseUtil.assertTrue(run.isValuer(), chars, indexer);
                        run = ((GetFang) run).toComplex(prevHandler);
                        values.pollLast();
                    }
                    values.add(run);
                    break;
                case YUAN_L:
                    // (
                    values.add(run = ParseGetter.parseYuan(chars, indexer, len, settings));
                    break;
                default:
                    // error
                    run = ParseUtil.throwErr(chars, indexer);
                    break;
            }
        }
        return run;
    }

    private final static AsCompute toGtLtAndOr(
        char[] chars,
        IntAccessor indexer,
        LinkedList<AsRunner> values,
        LinkedList<AsRunner> methods,
        int testTarget,
        Computes matchType,
        Computes defaultType
    ) {
        Computes type;
        if (chars[indexer.get()] == testTarget) {
            indexer.increment();
            type = matchType;
        } else {
            type = defaultType;
        }
        return casSymbol(values, methods, type);
    }

    private final static AsCompute casSymbol(
        LinkedList<AsRunner> values, LinkedList<AsRunner> methods, AsCompute computer
    ) {
        AsRunner prev = methods.peekFirst();
        int currPriority = computer.getPriority();
        if (isBoundary(prev) || currPriority > prev.getPriority()) {
            methods.offerFirst(computer);
        } else {
            while (isNotBoundary(prev = methods.pollFirst()) && prev.getPriority() >= currPriority) {
                values.add(prev);
            }
            if (prev != null) {
                methods.offerFirst(prev);
            }
            methods.offerFirst(computer);
        }
        return computer;
    }


    private final static LinkedList<AsRunner> cleanTo(
        LinkedList<AsRunner> values, LinkedList<AsRunner> methods, Object end
    ) {
        AsRunner computer;
        while ((computer = methods.pollFirst()) != end && computer != null) {
            values.add(computer);
        }
        return values;
    }

    private final static boolean isBoundary(AsRunner computer) {
        return computer == null || computer == Computes.YUAN_LEFT;
    }

    private final static boolean isNotBoundary(AsRunner computer) { return !isBoundary(computer); }
}
