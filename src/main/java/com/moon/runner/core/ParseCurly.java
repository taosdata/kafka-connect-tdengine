package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.runner.RunnerSetting;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.runner.core.Constants.*;
import static com.moon.runner.core.ParseSetting.getArrCreator;
import static com.moon.runner.core.ParseSetting.getObjCreator;

/**
 * @author moonsky
 */
final class ParseCurly {

    private ParseCurly() { noInstanceError(); }

    /**
     * 花括号检测，可能是：
     * Map
     * List
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     *
     * @return
     */
    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings
    ) {
        int curr = ParseUtil.nextVal(chars, indexer, len);
        AsRunner handler = tryEmpty(chars, indexer, len, curr);
        if (handler == null) {
            LinkedList<BiConsumer> creators = new LinkedList<>();
            CreateType type = getType(chars, indexer, len, curr);
            handler = type == CreateType.LIST ? parseList(chars, indexer, len, settings, curr, creators) : parseMap(
                chars, indexer, len, settings, curr, creators);
        }
        return handler;
    }

    private final static AsRunner parseList(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSetting settings,
        int curr,
        final LinkedList<BiConsumer> creators
    ) {
        AsRunner valuer;
        outer:
        for (int next = curr; ; curr = next) {
            inner:
            switch (next) {
                case HUA_R:
                    return createAsGetter(creators, settings, CreateType.LIST);
                case COMMA:
                    valuer = DataConst.NULL;
                    break inner;
                case SINGLE:
                case DOUBLE:
                    valuer = ParseConst.parseStr(chars, indexer, next);
                    break inner;
                default:
                    valuer = ParseCore.parse(chars, indexer.decrement(), len, settings, COMMA, HUA_R);
                    if ((next = chars[indexer.get() - 1]) == HUA_R) {
                        creators.add(new ListAdder(valuer));
                        continue outer;
                    }
                    break inner;
            }
            creators.add(new ListAdder(valuer));
            next = ParseUtil.nextVal(chars, indexer, len);
            if (next == COMMA && (valuer != DataConst.NULL || (curr != COMMA && curr != YUAN_L))) {
                next = ParseUtil.nextVal(chars, indexer, len);
            }
        }
    }

    private static class ListAdder implements BiConsumer<List, Object> {

        final AsRunner valuer;

        private ListAdder(AsRunner valuer) { this.valuer = valuer == null ? DataConst.NULL : valuer; }

        @Override
        public void accept(List list, Object data) { list.add(valuer.run(data)); }
    }

    private final static AsRunner parseMap(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSetting settings,
        int curr,
        final LinkedList<BiConsumer> creators
    ) {
        AsRunner key;
        for (int next = curr, index; ; ) {
            switch (next) {
                case HUA_R:
                    return createAsGetter(creators, settings, CreateType.MAP);
                case SINGLE:
                case DOUBLE:
                    key = ParseConst.parseStr(chars, indexer, next);
                    break;
                default:
                    if (ParseUtil.isNum(next)) {
                        key = ParseConst.parseNum(chars, indexer, len, next);
                    } else if (ParseUtil.isVar(next)) {
                        key = ParseGetter.parseVar(chars, indexer, len, next);
                        if (key.isGetter()) {
                            key = DataConst.get(key.toString());
                        }
                    } else {
                        key = ParseUtil.throwErr(chars, indexer);
                    }
                    break;
            }
            ParseUtil.assertTrue(ParseUtil.nextVal(chars, indexer, len) == COLON, chars, indexer);

            index = indexer.get();
            next = ParseUtil.nextVal(chars, indexer, len);
            ParseUtil.assertTrue(next != COMMA && next != HUA_R, chars, indexer);
            indexer.set(index);

            creators.add(new MapPutter((AsConst) key, ParseCore.parse(chars, indexer, len, settings, COMMA, HUA_R)));
            if ((next = chars[indexer.get() - 1]) == COMMA) {
                next = ParseUtil.nextVal(chars, indexer, len);
            }
        }
    }

    private static class MapPutter implements BiConsumer<Map, Object> {

        final AsConst key;
        final AsRunner valuer;

        private MapPutter(AsConst key, AsRunner valuer) {
            this.key = key;
            this.valuer = valuer;
        }

        @Override
        public void accept(Map map, Object data) { map.put(key.run(), valuer.run(data)); }
    }

    private static AsGetter createAsGetter(
        LinkedList<BiConsumer> creators, RunnerSetting settings, CreateType type
    ) {
        return creators.isEmpty() ? type : new GetCurly(creators.toArray(new BiConsumer[creators.size()]),
            type.apply(settings));
    }

    private final static CreateType getType(char[] chars, IntAccessor indexer, int len, int curr) {
        final int index = indexer.get();
        CreateType type;
        switch (curr) {
            case COMMA:
            case HUA_L:
            case FANG_L:
            case YUAN_L:
                type = CreateType.LIST;
                break;
            case SINGLE:
            case DOUBLE:
                ParseConst.parseStr(chars, indexer, curr);
                type = doNext(chars, indexer, curr);
                break;
            default:
                if (ParseUtil.isNum(curr)) {
                    ParseConst.parseNum(chars, indexer, len, curr);
                    type = doNext(chars, indexer, curr);
                } else if (ParseUtil.isVar(curr)) {
                    ParseGetter.parseVar(chars, indexer, len, curr);
                    type = doNext(chars, indexer, curr);
                } else {
                    type = CreateType.MAP;
                }
                break;
        }
        indexer.set(index);
        return type;
    }

    private final static CreateType doNext(char[] chars, IntAccessor indexer, int len) {
        return ParseUtil.nextVal(chars, indexer, len) == COLON ? CreateType.MAP : CreateType.LIST;
    }

    private final static AsRunner tryEmpty(char[] chars, IntAccessor indexer, int len, int curr) {
        if (curr == COLON) {
            int next = ParseUtil.nextVal(chars, indexer, len);
            ParseUtil.assertTrue(next == HUA_R, chars, indexer);
            return CreateType.MAP;
        } else if (curr == HUA_R) {
            return CreateType.LIST;
        }
        return null;
    }

    /*
     * --------------------------------------------------------------
     * 构造器
     * --------------------------------------------------------------
     */

    private enum CreateType implements AsGetter, Supplier, Function<RunnerSetting, Supplier> {
        MAP {
            @Override
            public Supplier apply(RunnerSetting settings) { return getObjCreator(settings); }

            @Override
            public Object get() { return new HashMap<>(); }

            @Override
            public boolean test(Object o) { return true; }

            @Override
            public Object run(Object data) { return get(); }
        },
        LIST {
            @Override
            public Supplier apply(RunnerSetting settings) { return getArrCreator(settings); }

            @Override
            public Object get() { return new ArrayList<>(); }

            @Override
            public boolean test(Object o) { return true; }

            @Override
            public Object run(Object data) { return get(); }
        }
    }
}
