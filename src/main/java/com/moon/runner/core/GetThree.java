package com.moon.runner.core;

import java.util.Objects;

/**
 * @author moonsky
 */
public class GetThree implements AsGetter {

    private final AsRunner<Boolean> assertRunner;
    private final AsRunner falseRunner;
    private final AsRunner trueRunner;

    public GetThree(AsRunner assertRunner, AsRunner trueRunner, AsRunner falseRunner) {
        this.assertRunner = assertRunner;
        this.falseRunner = falseRunner;
        this.trueRunner = trueRunner;
    }

    @Override
    public Object run(Object data) {
        return assertRunner.run(data) ? trueRunner.run(data) : falseRunner.run(data);
    }

    static class Builder {
        private final AsRunner assertRunner;
        private AsRunner trueRunner;
        private AsRunner falseRunner;

        public Builder(AsRunner assertRunner) { this.assertRunner = Objects.requireNonNull(assertRunner); }

        public Builder setTrueRunner(AsRunner trueRunner) {
            this.trueRunner = Objects.requireNonNull(trueRunner);
            return this;
        }

        public Builder setFalseRunner(AsRunner falseRunner) {
            this.falseRunner = Objects.requireNonNull(falseRunner);
            return this;
        }

        public AsRunner build() {
            if (assertRunner.isConst()) {
                if (assertRunner instanceof DataBool) {
                    return assertRunner == DataConst.TRUE
                        ? Objects.requireNonNull(trueRunner)
                        : Objects.requireNonNull(falseRunner);
                }
                throw new IllegalArgumentException("can not cast to boolean of value: " + assertRunner.toString());
            } else {
                return new GetThree(assertRunner, trueRunner, falseRunner);
            }
        }
    }
}
