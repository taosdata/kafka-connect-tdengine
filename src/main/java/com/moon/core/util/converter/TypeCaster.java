package com.moon.core.util.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * type converter
 *
 * @author moonsky
 */
@FunctionalInterface
public interface TypeCaster {
    /**
     * register type converter
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    default <C> TypeCaster register(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
        throw new UnsupportedOperationException();
    }

    /**
     * register type converter if absent
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    default <C> TypeCaster registerIfAbsent(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to type
     *
     * @param value
     * @param type
     * @param <T>
     * @return
     */
    <T> T toType(Object value, Class<T> type);

    /**
     * value to boolean value
     *
     * @param value
     * @return
     */
    default boolean toBooleanValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Boolean}
     *
     * @param value
     * @return
     */
    default Boolean toBoolean(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to char value
     *
     * @param value
     * @return
     */
    default char toCharValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Character}
     *
     * @param value
     * @return
     */
    default Character toCharacter(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to byte value
     *
     * @param value
     * @return
     */
    default byte toByteValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Byte}
     *
     * @param value
     * @return
     */
    default Byte toByte(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to short value
     *
     * @param value
     * @return
     */
    default short toShortValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Short}
     *
     * @param value
     * @return
     */
    default Short toShort(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to int value
     *
     * @param value
     * @return
     */
    default int toIntValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Integer}
     *
     * @param value
     * @return
     */
    default Integer toInteger(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to long value
     *
     * @param value
     * @return
     */
    default long toLongValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Long}
     *
     * @param value
     * @return
     */
    default Long toLong(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to float value
     *
     * @param value
     * @return
     */
    default float toFloatValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Float}
     *
     * @param value
     * @return
     */
    default Float toFloat(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to double value
     *
     * @param value
     * @return
     */
    default double toDoubleValue(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Double}
     *
     * @param value
     * @return
     */
    default Double toDouble(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link BigInteger}
     *
     * @param value
     * @return
     */
    default BigInteger toBigInteger(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link BigDecimal}
     *
     * @param value
     * @return
     */
    default BigDecimal toBigDecimal(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Date}
     *
     * @param value
     * @return
     */
    default Date toDate(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link java.sql.Date}
     *
     * @param value
     * @return
     */
    default java.sql.Date toSqlDate(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Timestamp}
     *
     * @param value
     * @return
     */
    default Timestamp toTimestamp(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Time}
     *
     * @param value
     * @return
     */
    default Time toSqlTime(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Calendar}
     *
     * @param value
     * @return
     */
    default Calendar toCalendar(Object value) { throw new UnsupportedOperationException(); }
    /**
     * value to {@link LocalDate}
     *
     * @param value
     * @return
     */
    default LocalDate toLocalDate(Object value) { throw new UnsupportedOperationException(); }
    /**
     * value to {@link LocalTime}
     *
     * @param value
     * @return
     */
    default LocalTime toLocalTime(Object value) { throw new UnsupportedOperationException(); }
    /**
     * value to {@link LocalDateTime}
     *
     * @param value
     * @return
     */
    default LocalDateTime toLocalDateTime(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link String}
     *
     * @param value
     * @return
     */
    default String toString(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link StringBuilder}
     *
     * @param value
     * @return
     */
    default StringBuilder toStringBuilder(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link StringBuffer}
     *
     * @param value
     * @return
     */
    default StringBuffer toStringBuffer(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Collection}
     *
     * @param value
     * @param <T>
     * @return
     */
    default <T extends Optional> T toOptional(Object value) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Enum}
     *
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    default <T extends Enum<T>> T toEnum(Object value, Class<T> clazz) { throw new UnsupportedOperationException(); }

    /**
     * value to java bean
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T toBean(Map map, Class<T> clazz) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link java.lang.reflect.Array}
     *
     * @param value
     * @param arrayType
     * @return
     */
    default <T> T toArray(Object value, Class<T> arrayType) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link java.lang.reflect.Array}
     *
     * @param value
     * @param componentType
     * @return
     */
    default <T> T[] toTypeArray(Object value, Class<T> componentType) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Map}
     *
     * @param value
     * @param mapClass
     * @param <T>
     * @return
     */
    default <T extends Map> T toMap(Object value, Class<T> mapClass) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link List}
     *
     * @param value
     * @param listType
     * @param <T>
     * @return
     */
    default <T extends List> T toList(Object value, Class<T> listType) { throw new UnsupportedOperationException(); }

    /**
     * value to {@link Collection}
     *
     * @param value
     * @param collectType
     * @param <T>
     * @return
     */
    default <T extends Collection> T toCollection(Object value, Class<T> collectType) {
        throw new UnsupportedOperationException();
    }
}
