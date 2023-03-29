package com.moon.core.enums;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.converter.TypeConverter;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;

import static com.moon.core.lang.BooleanUtil.toBoolean;
import static com.moon.core.lang.BooleanUtil.toBooleanValue;
import static com.moon.core.lang.ByteUtil.toByte;
import static com.moon.core.lang.ByteUtil.toByteValue;
import static com.moon.core.lang.CharUtil.toCharValue;
import static com.moon.core.lang.CharacterUtil.toCharacter;
import static com.moon.core.lang.DoubleUtil.toDouble;
import static com.moon.core.lang.DoubleUtil.toDoubleValue;
import static com.moon.core.lang.EnumUtil.toEnum;
import static com.moon.core.lang.FloatUtil.toFloat;
import static com.moon.core.lang.FloatUtil.toFloatValue;
import static com.moon.core.lang.IntUtil.toIntValue;
import static com.moon.core.lang.IntegerUtil.toInteger;
import static com.moon.core.lang.LongUtil.toLong;
import static com.moon.core.lang.LongUtil.toLongValue;
import static com.moon.core.lang.ShortUtil.toShort;
import static com.moon.core.lang.ShortUtil.toShortValue;
import static com.moon.core.math.BigDecimalUtil.toBigDecimal;
import static com.moon.core.math.BigIntegerUtil.toBigInteger;
import static com.moon.core.util.OptionalUtil.resolveOrNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author moonsky
 */
public enum Casters implements EnumDescriptor, BiFunction<Object, Class, Object>, TypeConverter {

    toBooleanValue(boolean.class) {
        @Override
        public Object createArr(int length) { return new boolean[length]; }

        @Override
        public Object cast(Object o) { return toBooleanValue(o); }
    },
    toBoolean(Boolean.class) {
        @Override
        public Object createArr(int length) { return new Boolean[length]; }

        @Override
        public Object cast(Object o) { return toBoolean(o); }
    },
    toCharValue(char.class) {
        @Override
        public Object createArr(int length) { return new char[length]; }

        @Override
        public Object cast(Object o) { return toCharValue(o); }
    },
    toCharacter(Character.class) {
        @Override
        public Object createArr(int length) { return new Character[length]; }

        @Override
        public Object cast(Object o) { return toCharacter(o); }
    },
    toByteValue(byte.class) {
        @Override
        public Object createArr(int length) { return new byte[length]; }

        @Override
        public Object cast(Object o) { return toByteValue(o); }
    },
    toByte(Byte.class) {
        @Override
        public Object createArr(int length) { return new Byte[length]; }

        @Override
        public Object cast(Object o) { return toByte(o); }
    },
    toShortValue(short.class) {
        @Override
        public Object createArr(int length) { return new short[length]; }

        @Override
        public Object cast(Object o) { return toShortValue(o); }
    },
    toShort(Short.class) {
        @Override
        public Object createArr(int length) { return new Short[length]; }

        @Override
        public Object cast(Object o) { return toShort(o); }
    },
    toIntValue(int.class) {
        @Override
        public Object createArr(int length) { return new int[length]; }

        @Override
        public Object cast(Object o) { return toIntValue(o); }
    },
    toInteger(Integer.class) {
        @Override
        public Object createArr(int length) { return new Integer[length]; }

        @Override
        public Object cast(Object o) { return toInteger(o); }
    },
    toLongValue(long.class) {
        @Override
        public Object createArr(int length) { return new long[length]; }

        @Override
        public Object cast(Object o) { return toLongValue(o); }
    },
    toLong(Long.class) {
        @Override
        public Object createArr(int length) { return new Long[length]; }

        @Override
        public Object cast(Object o) { return toLong(o); }
    },
    toFloatValue(float.class) {
        @Override
        public Object createArr(int length) { return new float[length]; }

        @Override
        public Object cast(Object o) { return toFloatValue(o); }
    },
    toFloat(Float.class) {
        @Override
        public Object createArr(int length) { return new Float[length]; }

        @Override
        public Object cast(Object o) { return toFloat(o); }
    },
    toDoubleValue(double.class) {
        @Override
        public Object createArr(int length) { return new double[length]; }

        @Override
        public Object cast(Object o) { return toDoubleValue(o); }
    },
    toDouble(Double.class) {
        @Override
        public Object createArr(int length) { return new Double[length]; }

        @Override
        public Object cast(Object o) { return toDouble(o); }
    },
    toBigInteger(BigInteger.class) {
        @Override
        public Object createArr(int length) { return new BigInteger[length]; }

        @Override
        public Object cast(Object o) { return toBigInteger(o); }
    },
    toBigDecimal(BigDecimal.class) {
        @Override
        public Object createArr(int length) { return new BigDecimal[length]; }

        @Override
        public Object cast(Object o) { return toBigDecimal(o); }
    },

    toObject(Object.class) {
        @Override
        public Object cast(Object o) { return o; }

        @Override
        public Object createArr(int length) { return new Object[length]; }
    },

    toString(String.class) {
        @Override
        public Object createArr(int length) { return new String[length]; }

        @Override
        public Object cast(Object o) { return o == null ? null : o.toString(); }
    },
    toStringBuffer(StringBuffer.class) {
        @Override
        public Object createArr(int length) { return new StringBuffer[length]; }

        @Override
        public Object cast(Object o) { return o == null ? null : new StringBuffer(o.toString()); }
    },
    toStringBuilder(StringBuilder.class) {
        @Override
        public Object createArr(int length) { return new StringBuilder[length]; }

        @Override
        public Object cast(Object o) { return o == null ? null : new StringBuilder(o.toString()); }
    },
    toOptional(Optional.class) {
        @Override
        public Object createArr(int length) { return new Optional[length]; }

        @Override
        public Object cast(Object o) {
            if (o == null) {
                return Optional.empty();
            } else if (o instanceof Optional) {
                return o;
            }
            return ofNullable(resolveOrNull(o));
        }
    },
    toOptionalInt(OptionalInt.class) {
        @Override
        public Object createArr(int length) { return new OptionalInt[length]; }

        private OptionalInt fromNumber(Number num) {
            int intVal = num.intValue();
            long value = num.longValue();
            if (intVal == value) {
                return OptionalInt.of(intVal);
            }
            throw new IllegalArgumentException("Can not cast to java.util.OptionalInt of value: " + num);
        }

        @Override
        public Object cast(Object o) {
            if (o == null) {
                return OptionalInt.empty();
            }
            if (o instanceof OptionalInt) {
                return o;
            } else if (o instanceof Integer) {
                return OptionalInt.of(((Integer) o).intValue());
            } else if (o instanceof Number) {
                return fromNumber((Number) o);
            } else if (o instanceof OptionalLong) {
                OptionalLong opt = (OptionalLong) o;
                return opt.isPresent() ? fromNumber(opt.getAsLong()) : OptionalInt.empty();
            } else if (o instanceof OptionalDouble) {
                OptionalDouble opt = (OptionalDouble) o;
                return opt.isPresent() ? fromNumber(opt.getAsDouble()) : OptionalInt.empty();
            }
            throw new IllegalArgumentException("Can not cast to java.util.OptionalInt of value: " + o);
        }
    },
    toOptionalLong(OptionalLong.class) {
        @Override
        public Object createArr(int length) { return new OptionalLong[length]; }

        private OptionalLong fromNumber(Number num) {
            long longVal = num.longValue();
            double value = num.doubleValue();
            if (longVal == 0) {
                if (value == 0) {
                    return OptionalLong.of(0);
                }
            } else if (value / longVal == 0 && value % longVal == 0) {
                return OptionalLong.of(longVal);
            }
            throw new IllegalArgumentException("Can not cast to java.util.OptionalLong of value: " + num);
        }

        @Override
        public Object cast(Object o) {
            if (o == null) {
                return OptionalLong.empty();
            }
            if (o instanceof OptionalLong) {
                return o;
            } else if (o instanceof Long) {
                return OptionalLong.of(((Long) o).intValue());
            } else if (o instanceof Number) {
                return fromNumber((Number) o);
            } else if (o instanceof OptionalInt) {
                OptionalInt opt = (OptionalInt) o;
                return opt.isPresent() ? fromNumber(opt.getAsInt()) : OptionalLong.empty();
            } else if (o instanceof OptionalDouble) {
                OptionalDouble opt = (OptionalDouble) o;
                return opt.isPresent() ? fromNumber(opt.getAsDouble()) : OptionalLong.empty();
            }
            throw new IllegalArgumentException("Can not cast to java.util.OptionalLong of value: " + o);
        }
    },
    toOptionalDouble(OptionalDouble.class) {
        @Override
        public Object createArr(int length) { return new OptionalDouble[length]; }

        private OptionalDouble fromNumber(Number num) {
            long longVal = num.longValue();
            double value = num.doubleValue();
            if (longVal == 0) {
                if (value == 0) {
                    return OptionalDouble.of(0);
                }
            } else if (value / longVal == 0 && value % longVal == 0) {
                return OptionalDouble.of(longVal);
            }
            throw new IllegalArgumentException("Can not cast to java.util.OptionalDouble of value: " + num);
        }

        @Override
        public Object cast(Object o) {
            if (o == null) {
                return OptionalDouble.empty();
            }
            if (o instanceof OptionalDouble) {
                return o;
            } else if (o instanceof Double) {
                return OptionalDouble.of(((Double) o).doubleValue());
            } else if (o instanceof Number) {
                return fromNumber((Number) o);
            } else if (o instanceof OptionalInt) {
                OptionalInt opt = (OptionalInt) o;
                return opt.isPresent() ? OptionalDouble.of(opt.getAsInt()) : OptionalDouble.empty();
            } else if (o instanceof OptionalLong) {
                OptionalLong opt = (OptionalLong) o;
                return opt.isPresent() ? OptionalDouble.of(opt.getAsLong()) : OptionalDouble.empty();
            }
            throw new IllegalArgumentException("Can not cast to java.util.OptionalDouble of value: " + o);
        }
    },
    toEnum(Enum.class) {
        @Override
        public Object createArr(int length) { return new Enum[length]; }

        @Override
        public Object cast(Object o) { return ThrowUtil.runtime("Unsupported."); }

        @Override
        public Object apply(Object o, Class type) { return toEnum(o, type); }
    },
    toClass(Class.class) {
        @Override
        public <T> T cast(Object o) {
            if (o instanceof Class) {
                return (T) o;
            } else if (o instanceof CharSequence) {
                Class type = ClassUtil.forNameOrNull(o.toString());
                return (T) (type == null ? o.getClass() : type);
            } else if (o == null) {
                return ThrowUtil.runtime(String.valueOf(null));
            } else {
                return (T) o.getClass();
            }
        }

        @Override
        public Object createArr(int length) { return new Class[length]; }
    };

    public final Class TYPE;

    private static class Cached {

        final static Map<Class, Casters> CACHE = new HashMap();
    }

    Casters(Class type) { Cached.CACHE.put(this.TYPE = type, this); }

    public abstract <T> T cast(Object o);

    public abstract Object createArr(int length);

    public Object toArray(Object... values) {
        if (values == null) {
            return null;
        }
        int length = values.length;
        Object array = Array.newInstance(TYPE, length);
        System.arraycopy(values, 0, array, 0, length);
        return array;
    }

    public static Casters getOrNull(Class type) { return Cached.CACHE.get(type); }

    public static TypeConverter getOrDefault(Class type, TypeConverter defaultConverter) {
        TypeConverter caster = getOrNull(type);
        return caster == null ? defaultConverter : caster;
    }

    @Override
    public Object convertTo(Object o) { return cast(o); }

    @Override
    public Object apply(Object o, Class aClass) { return cast(o); }

    @Override
    public final String getText() { return TYPE.getName(); }

    public final static <T> T to(Object o, Class type) { return (T) requireNonNull(getOrNull(type)).apply(o, type); }
}
