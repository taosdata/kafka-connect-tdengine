package com.moon.runner.core;

import com.moon.runner.IRunnerSetting;
import com.moon.runner.RunnerFunction;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
enum ParseSetting implements IRunnerSetting {
    VALUE;

    @Override
    public Class getCaller(String name) { return IGetLoad.of(name); }

    @Override
    public RunnerFunction getFunction(String name) { return IGetFun.tryLoad(name); }

    public static Supplier<List> getArrCreator(IRunnerSetting customer) {
        return customer == null ? VALUE.getArrCreator() : customer.getArrCreator();
    }

    public static Supplier<Map> getObjCreator(IRunnerSetting customer) {
        return customer == null ? VALUE.getObjCreator() : customer.getObjCreator();
    }

    public static Class getCaller(IRunnerSetting customer, String name) {
        if (customer == null) {
            return VALUE.getCaller(name);
        }
        Class type = customer.getCaller(name);
        return type == null ? VALUE.getCaller(name) : type;
    }

    public static RunnerFunction getFunction(IRunnerSetting customer, String name) {
        if (customer == null) {
            return VALUE.getFunction(name);
        }
        RunnerFunction type = customer.getFunction(name);
        return type == null ? VALUE.getFunction(name) : type;
    }}
