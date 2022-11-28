package com.taosdata.kafka.connect.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taosdata.kafka.connect.exception.SchemaException;

import static com.taosdata.kafka.connect.sink.SinkConstants.*;
import static com.taosdata.kafka.connect.sink.SinkConstants.SCHEMA_COLUMN_TARGET_TAG;

public class JSONUtils {

    private JSONUtils() {
    }

    public static void schemaObjectValidator(JSONObject schemaObject) {
        if (!schemaObject.containsKey(SCHEMA_NAME)) {
            throw new SchemaException(String.format("schema: %s cannot find %s",
                    JSON.toJSONString(schemaObject), SCHEMA_NAME));
        }
        String name = schemaObject.getString(SCHEMA_NAME);
//        if (!schemaObject.containsKey(SCHEMA_DATABASE)) {
//            throw new SchemaException(String.format("schema: %s cannot find %s",
//                    name, SCHEMA_DATABASE));
//        }
        if (!schemaObject.containsKey(SCHEMA_STABLE_NAME_SPECIFY) && !schemaObject.containsKey(SCHEMA_STABLE_NAME)) {
            throw new SchemaException(String.format("schema: %s cannot find stable name", name));
        }
        if (!schemaObject.containsKey(SCHEMA_TABLE_NAME)) {
            throw new SchemaException(String.format("schema: %s cannot find stable name", name));
        }
    }

    public static Column json2Col(JSONObject value) {
        Column column = new Column();
        if (value.containsKey(SCHEMA_COLUMN_SOURCE_TYPE)) {
            column.setSourceType(value.getString(SCHEMA_COLUMN_SOURCE_TYPE));
        }
        if (value.containsKey(SCHEMA_COLUMN_TARGET_TYPE)) {
            column.setTargetType(value.getString(SCHEMA_COLUMN_TARGET_TYPE));
        }
        if (value.containsKey(SCHEMA_COLUMN_TARGET_COLUMN)) {
            column.setTargetColumn(value.getString(SCHEMA_COLUMN_TARGET_COLUMN));
            column.setTag(false);
        }
        if (value.containsKey(SCHEMA_COLUMN_TARGET_TAG)) {
            column.setTargetColumn(value.getString(SCHEMA_COLUMN_TARGET_TAG));
            column.setTag(true);
        }
        if (value.containsKey(SCHEMA_COLUMN_OPTIONAL)) {
            column.setOptional(value.getBoolean(SCHEMA_COLUMN_OPTIONAL));
        }
        if (value.containsKey(SCHEMA_COLUMN_DEFAULT)) {
            column.setDefaultValue(value.get(SCHEMA_COLUMN_DEFAULT));
        }
        if (value.containsKey(SCHEMA_COLUMN_OTHER)) {
            column.setOther(value.getString(SCHEMA_COLUMN_OTHER));
        }
        return column;
    }

    public static String getString(Object object) {
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }
}
