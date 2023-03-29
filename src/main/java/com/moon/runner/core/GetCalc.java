package com.moon.runner.core;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author moonsky
 */
class GetCalc implements AsGetter {
    final AsRunner[] runners;

    private GetCalc(AsRunner[] runners) { this.runners = runners; }

    final static AsRunner valueOf(List<AsRunner> runners) {
        int size = runners.size();
        if (size == 0) {
            return DataConst.NULL;
        }
        if (size == 1 && runners.get(0).isValuer()) {
            return runners.get(0);
        }
        return preRun(toArr(runners));
    }

    private static AsRunner[] toArr(List<AsRunner> runners) { return runners.toArray(new AsRunner[runners.size()]); }

    private static AsRunner preRun(AsRunner[] runnerArr) {
        LinkedList<AsRunner> result = new LinkedList();
        final int length = runnerArr.length;
        AsRunner right, left;
        AsRunner operator;
        for (int i = 0; i < length; i++) {
            operator = runnerArr[i];
            if (operator.isConst()) {
                result.offerFirst(operator);
            } else if (operator.isHandler()) {
                right = result.pollFirst();
                left = result.pollFirst();
                result.offerFirst(
                    DataConst.get(operator.exe(right, left, null))
                );
            } else {
                if (result.isEmpty()) {
                    return new GetCalc(runnerArr);
                }
                for (; i < length; i++) {
                    result.offerLast(runnerArr[i]);
                }
                return new GetCalc(toArr(result));
            }
        }
        return result.pollFirst();
    }

    @Override
    public Object run(Object data) { return use1(data); }

    private Object use1(Object data) {
        Deque<AsRunner> result = new LinkedList();
        AsRunner[] runners = this.runners;
        final int length = runners.length;
        AsRunner right, left;
        AsRunner operator;
        for (int i = 0; i < length; i++) {
            operator = runners[i];
            if (operator.isValuer()) {
                result.offerFirst(operator);
            } else if (operator.isHandler()) {
                right = result.pollFirst();
                left = result.pollFirst();
                result.offerFirst(
                    DataConst.temp(
                        operator.exe(right, left, data)
                    )
                );
            } else {
                throw new IllegalArgumentException(
                    "type of: " + operator.getClass()
                );
            }
        }
        return result.pollFirst().run();
    }

    private Object use0(Object data) {
        AsRunner[] handlers = this.runners;
        final int length = handlers.length;
        Deque result = new LinkedList();
        AsRunner operator;
        Object right, left;
        for (int i = 0; i < length; i++) {
            operator = handlers[i];
            if (operator.isValuer()) {
                result.offerFirst(operator.run(data));
            } else if (operator.isHandler()) {
                right = result.pollFirst();
                left = result.pollFirst();
                result.offerFirst(
                    operator.exe(right, left)
                );
            } else {
                throw new IllegalArgumentException(
                    "type of: " + operator.getClass()
                );
            }
        }
        return result.pollFirst();
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Object o) { return false; }

    @Override
    public String toString() { return Arrays.toString(runners); }
}
