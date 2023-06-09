package com.taosdata.kafka.connect.source;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

import java.util.HashMap;
import java.util.Map;

public class TDStruct extends Struct {
    private static final Log log = LogFactory.getLog(TDStruct.class);

    /**
     * Create a new Struct for this {@link Schema}
     *
     * @param schema the {@link Schema} for the Struct
     */
    public TDStruct(Schema schema) {
        super(schema);
    }

    @Override
    public String toString() {
        Map<String,Object> cols = new HashMap<>();
        Map<String, Object> tags = new HashMap<>();

        final Schema schema = super.schema();
        for (Field f : schema.fields()) {
            Object o = super.get(f);

            Schema field = f.schema();
            if (Schema.Type.STRUCT == field.type()){
                // tags
                TDStruct tagStruct = (TDStruct)o;
                Schema tagSchema = tagStruct.schema();
                for (Field tf : tagSchema.fields()) {
                    tags.put(tf.name(), tagStruct.get(tf));
                }
            }else {
                // cols
                cols.put(f.name(), o);
            }
        }

        if (!tags.isEmpty()){
            cols.put("tags", tags);
        }
        return JSONObject.toJSONString(cols);
    }
}
