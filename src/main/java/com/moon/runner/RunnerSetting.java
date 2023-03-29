package com.moon.runner;

import com.moon.core.lang.ParseSupportUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ValidateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class RunnerSetting implements IRunnerSetting {

    protected Supplier<List> arrCreator;
    protected Supplier<Map> objCreator;

    private final Map<String, Class> callers = new HashMap<>();
    private final Map<String, RunnerFunction> functions = new HashMap<>();

    public RunnerSetting() { this(ArrayList::new, HashMap::new); }

    public RunnerSetting(Supplier<List> arrCreator, Supplier<Map> objCreator) {
        this.arrCreator = arrCreator;
        this.objCreator = objCreator;
    }

    /**
     * 获取静态方法执行类
     *
     * @param name
     *
     * @return
     */
    @Override
    public final Class getCaller(String name) { return callers.get(name); }

    @Override
    public RunnerFunction getFunction(String name) { return functions.get(name); }

    @Override
    public final Supplier<List> getArrCreator() { return arrCreator; }

    @Override
    public final Supplier<Map> getObjCreator() { return objCreator; }

    public RunnerSetting setArrCreator(Supplier<List> arrCreator) {
        this.arrCreator = arrCreator;
        return this;
    }

    public RunnerSetting setObjCreator(Supplier<Map> objCreator) {
        this.objCreator = objCreator;
        return this;
    }

    public RunnerSetting addFunction(RunnerFunction runner) {
        this.functions.put(checkName(runner.functionName()), runner);
        return this;
    }

    public RunnerSetting addFunctions(List<RunnerFunction> runners) {
        IteratorUtil.forEach(runners, runner -> addFunction(runner));
        return this;
    }

    public RunnerSetting addFunction(String namespace, RunnerFunction fun) {
        this.functions.put(toNsName(namespace, fun.functionName()), fun);
        return this;
    }

    public RunnerSetting addFunctions(String namespace, List<RunnerFunction> runners) {
        IteratorUtil.forEach(runners, fun -> addFunction(namespace, fun));
        return this;
    }

    public RunnerSetting addCaller(Class clazz) { return addCaller(clazz.getSimpleName(), clazz); }

    public RunnerSetting addCallers(Class... classes) {
        for (Class type : classes) {
            addCaller(type);
        }
        return this;
    }

    public RunnerSetting addCaller(String name, Class staticCallerClass) {
        this.callers.put(name, staticCallerClass);
        return this;
    }

    public RunnerSetting addCallers(Map<String, Class> callers) {
        this.callers.putAll(callers);
        return this;
    }

    public RunnerSetting removeCaller(String name) {
        this.callers.remove(name);
        return this;
    }

    public RunnerSetting removeCallers(String... names) {
        for (String name : names) {
            this.callers.remove(name);
        }
        return this;
    }

    public final static RunnerSetting of() { return new RunnerSetting(); }

    static String toNsName(String ns, String name) { return checkName(ns) + '.' + checkName(name); }

    static String checkName(String name) {
        char curr = name.charAt(0);
        ValidateUtil.requireTrue(ParseSupportUtil.isVar(curr), name);
        for (int i = 1, len = name.length(); i < len; i++) {
            ValidateUtil.requireTrue(ParseSupportUtil.isVar(curr = name.charAt(i)) || ParseSupportUtil.isNum(curr), name);
        }
        return name;
    }
}
