package com.moon.runner.core;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
final class ConstPriorities {

    private ConstPriorities() { noInstanceError(); }

    final static int BIT_AND = 4;
    final static int BIT_OR = 4;
    final static int NOT_OR = 4;
    final static int BIT_LEFT = 4;
    final static int BIT_RIGHT = 4;
    final static int UN_BIT_RIGHT = 4;
    final static int PLUS = 5;
    final static int MINUS = 5;
    final static int MULTI = 6;
    final static int DIVIDE = 6;
    final static int MOD = 6;

    final static int AND = 1;
    final static int OR = 1;

    final static int EQ = 3;
    final static int NOT_EQ = 3;
    final static int GT = 3;
    final static int LT = 3;
    final static int GT_OR_EQ = 3;
    final static int LT_OR_EQ = 3;

    final static int MAX = 99;
}
