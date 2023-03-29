package com.moon.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface IRunnerSetting {

    /**
     * 数组构造器
     *
     * @return
     */
    default Supplier<List> getArrCreator() { return ArrayList::new; }

    /**
     * 对象构造器
     *
     * @return
     */
    default Supplier<Map> getObjCreator() { return HashMap::new; }

    /**
     * 静态方法执行器
     *
     * @param name
     *
     * @return
     */
    Class getCaller(String name);

    /**
     * 函数执行器
     *
     * @param name
     *
     * @return
     */
    RunnerFunction getFunction(String name);
}
