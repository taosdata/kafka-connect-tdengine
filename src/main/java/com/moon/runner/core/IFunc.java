package com.moon.runner.core;

import com.moon.runner.RunnerFunction;

/**
 * @author moonsky
 */
final class IFunc {

    static class Non implements AsInvoker {

        protected final RunnerFunction fn;

        Non(RunnerFunction fn) { this.fn = fn; }

        @Override
        public Object run(Object data) { return fn.apply(); }
    }

    static class One extends Non {

        protected final AsRunner runner;

        One(RunnerFunction fn, AsRunner runner) {
            super(fn);
            this.runner = runner;
        }

        @Override
        public Object run(Object data) { return fn.apply(runner.run(data)); }
    }

    static class Two extends One {

        protected final AsRunner runner0;

        Two(RunnerFunction fn, AsRunner runner, AsRunner runner0) {
            super(fn, runner);
            this.runner0 = runner0;
        }

        @Override
        public Object run(Object data) { return fn.apply(runner.run(data), runner0.run()); }
    }

    static class Three extends Two {

        protected final AsRunner runner1;

        Three(RunnerFunction fn, AsRunner runner, AsRunner runner0, AsRunner runner1) {
            super(fn, runner, runner0);
            this.runner1 = runner1;
        }

        @Override
        public Object run(Object data) { return fn.apply(runner.run(data), runner0.run(), runner1.run()); }
    }

    static class Multi extends Non {

        private final AsRunner[] runners;

        Multi(RunnerFunction fn, AsRunner[] runners) {
            super(fn);
            this.runners = runners;
        }

        @Override
        public Object run(Object data) {
            AsRunner[] runners = this.runners;
            int length = runners.length;
            Object[] params = new Object[length];
            for (int i = 0; i < length; i++) {
                params[i] = runners[i].run(data);
            }
            return fn.apply(params);
        }
    }
}
