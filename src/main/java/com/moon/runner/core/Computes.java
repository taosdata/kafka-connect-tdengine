package com.moon.runner.core;

import java.util.Objects;

/**
 * @author moonsky
 */
enum Computes implements AsCompute {
    /**
     * 这个符号仅用于提升优先级的标记，没有计算意义
     */
    YUAN_LEFT(ConstPriorities.MAX),

    BIT_LEFT(ConstPriorities.BIT_LEFT) {
        @Override
        public Object exe(Object o2, Object o1) { return ((Number) o1).intValue() << ((Number) o2).intValue(); }
    },
    UN_BIT_RIGHT(ConstPriorities.UN_BIT_RIGHT) {
        @Override
        public Object exe(Object o2, Object o1) { return ((Number) o1).intValue() >>> ((Number) o2).intValue(); }
    },
    BIT_RIGHT(ConstPriorities.BIT_RIGHT) {
        @Override
        public Object exe(Object o2, Object o1) { return ((Number) o1).intValue() >> ((Number) o2).intValue(); }
    },
    BIT_AND(ConstPriorities.BIT_AND) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Number || o2 instanceof Number) {
                return ((Number) o1).intValue() & ((Number) o2).intValue();
            }
            return ((Boolean) o1).booleanValue() & ((Boolean) o2).booleanValue();
        }
    },
    BIT_OR(ConstPriorities.BIT_OR) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Number || o2 instanceof Number) {
                return ((Number) o1).intValue() | ((Number) o2).intValue();
            }
            return ((Boolean) o1).booleanValue() | ((Boolean) o2).booleanValue();
        }
    },
    NOT_OR(ConstPriorities.NOT_OR) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Number || o2 instanceof Number) {
                return ((Number) o1).intValue() ^ ((Number) o2).intValue();
            }
            return ((Boolean) o1).booleanValue() ^ ((Boolean) o2).booleanValue();
        }
    },

    PLUS(ConstPriorities.PLUS) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Number && o2 instanceof Number) {
                if (o1 instanceof Integer && o2 instanceof Integer) {
                    return ((Number) o1).intValue() + ((Number) o2).intValue();
                }
                return ((Number) o1).doubleValue() + ((Number) o2).doubleValue();
            }
            return String.valueOf(o1) + o2;
        }
    },
    MINUS(ConstPriorities.MINUS) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Double || o2 instanceof Double || o1 instanceof Float || o2 instanceof Float) {
                return ((Number) o1).doubleValue() - ((Number) o2).doubleValue();
            }
            return ((Number) o1).intValue() - ((Number) o2).intValue();
        }
    },
    MULTI(ConstPriorities.MULTI) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Double || o2 instanceof Double || o1 instanceof Float || o2 instanceof Float) {
                return ((Number) o1).doubleValue() * ((Number) o2).doubleValue();
            }
            return ((Number) o1).intValue() * ((Number) o2).intValue();
        }
    },
    DIVIDE(ConstPriorities.DIVIDE) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Double || o2 instanceof Double || o1 instanceof Float || o2 instanceof Float) {
                return ((Number) o1).doubleValue() / ((Number) o2).doubleValue();
            }
            return ((Number) o1).intValue() / ((Number) o2).intValue();
        }
    },
    MOD(ConstPriorities.MOD) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 instanceof Double || o2 instanceof Double || o1 instanceof Float || o2 instanceof Float) {
                return ((Number) o1).doubleValue() % ((Number) o2).doubleValue();
            }
            return ((Number) o1).intValue() % ((Number) o2).intValue();
        }
    },
    AND(ConstPriorities.AND) {
        /**
         * 计算
         *
         * @param right
         * @param left
         * @param data
         * @return
         */
        @Override
        public Object exe(AsRunner right, AsRunner left, Object data) {
            return (Boolean) left.run(data) && (Boolean) right.run(data);
        }
    },
    OR(ConstPriorities.OR) {
        /**
         * 计算
         *
         * @param right
         * @param left
         * @param data
         * @return
         */
        @Override
        public Object exe(AsRunner right, AsRunner left, Object data) {
            return (Boolean) left.run(data) || (Boolean) right.run(data);
        }
    },
    NOT_EQ(ConstPriorities.NOT_EQ) {
        @Override
        public Object exe(Object right, Object left) {
            if (right == left) {
                return false;
            }
            if (right instanceof Number && left instanceof Number) {
                if (right instanceof Double ||
                    right instanceof Float ||
                    left instanceof Double ||
                    left instanceof Float) {
                    return ((Number) right).doubleValue() == ((Number) left).doubleValue();
                }
                return ((Number) right).intValue() == ((Number) left).intValue();
            }
            return Boolean.valueOf(!Objects.equals(right, left));
        }
    },
    EQ(ConstPriorities.EQ) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 == o2) {
                return true;
            }
            if (o1 instanceof Number && o2 instanceof Number) {
                if (o1 instanceof Double || o1 instanceof Float || o2 instanceof Double || o2 instanceof Float) {
                    return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
                }
                return ((Number) o1).intValue() == ((Number) o2).intValue();
            }
            return Boolean.valueOf(Objects.equals(o1, o2));
        }
    },
    GT(ConstPriorities.GT) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 == o2 || o1 == null) {
                return Boolean.FALSE;
            }
            if (o2 == null) {
                return Boolean.TRUE;
            }
            if (o1 instanceof Number && o2 instanceof Number) {
                if (o1 instanceof Double || o1 instanceof Float || o2 instanceof Double || o2 instanceof Float) {
                    return ((Number) o1).doubleValue() > ((Number) o2).doubleValue();
                }
                return ((Number) o1).intValue() > ((Number) o2).intValue();
            }
            return ((Comparable) o1).compareTo(o2) > 0;
        }
    },
    LT(ConstPriorities.LT) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 == o2 || o2 == null) {
                return Boolean.FALSE;
            }
            if (o1 == null) {
                return Boolean.TRUE;
            }
            if (o1 instanceof Number && o2 instanceof Number) {
                if (o1 instanceof Double || o1 instanceof Float || o2 instanceof Double || o2 instanceof Float) {
                    return ((Number) o1).doubleValue() < ((Number) o2).doubleValue();
                }
                return ((Number) o1).intValue() < ((Number) o2).intValue();
            }
            return ((Comparable) o1).compareTo(o2) < 0;
        }
    },
    GT_OR_EQ(ConstPriorities.GT_OR_EQ) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 == o2 || o2 == null) {
                return true;
            }
            if (o1 == null) {
                return false;
            }
            if (o1 instanceof Number && o2 instanceof Number) {
                if (o1 instanceof Double || o1 instanceof Float || o2 instanceof Double || o2 instanceof Float) {
                    return ((Number) o1).doubleValue() >= ((Number) o2).doubleValue();
                }
                return ((Number) o1).intValue() >= ((Number) o2).intValue();
            }
            return ((Comparable) o1).compareTo(o2) >= 0;
        }
    },
    LT_OR_EQ(ConstPriorities.LT_OR_EQ) {
        @Override
        public Object exe(Object o2, Object o1) {
            if (o1 == o2 || o1 == null) {
                return true;
            }
            if (o2 == null) {
                return false;
            }
            if (o1 instanceof Number && o2 instanceof Number) {
                if (o1 instanceof Double || o1 instanceof Float || o2 instanceof Double || o2 instanceof Float) {
                    return ((Number) o1).doubleValue() <= ((Number) o2).doubleValue();
                }
                return ((Number) o1).intValue() <= ((Number) o2).intValue();
            }
            return ((Comparable) o1).compareTo(o2) <= 0;
        }
    };

    private final int priority;

    Computes(int priority) { this.priority = priority; }

    @Override
    public int getPriority() { return priority; }}
