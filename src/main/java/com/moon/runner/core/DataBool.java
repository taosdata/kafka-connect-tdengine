package com.moon.runner.core;

/**
 * @author moonsky
 */
enum DataBool implements AsConst, AsFlip {
    TRUE {
        @Override
        public Object run(Object data) { return Boolean.TRUE; }

        @Override
        public DataBool flip() { return FALSE; }
    },
    FALSE {
        @Override
        public Object run(Object data) { return Boolean.FALSE; }

        @Override
        public DataBool flip() { return TRUE; }
    };

    @Override
    public boolean isBoolean() { return true; }

    @Override
    public String toString() { return name().toLowerCase(); }
}
