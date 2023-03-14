package com.taosdata.kafka.connect.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.moon.runner.RunnerUtil;
import com.taosdata.jdbc.utils.StringUtils;
import com.taosdata.kafka.connect.exception.RecordException;
import com.taosdata.kafka.connect.exception.SchemaException;
import com.taosdata.kafka.connect.sink.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.taosdata.kafka.connect.sink.JSONUtils.*;
import static com.taosdata.kafka.connect.sink.SinkConstants.*;

/**
 * @author taosdata
 */
public class TestSchema {
    @Test
    Map<String, Schema> testSchema() throws IOException {

        String fileName = "/Users/chang/Desktop/cmcc.json";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            //process the line
            stringBuilder.append(line);
        }
        Map<String, String> map = new HashMap<>();
        map.put(SCHEMA_STRING, stringBuilder.toString());

        String schemaStr = map.get(SCHEMA_STRING);
        JSONObject jsonObject = JSON.parseObject(schemaStr);

        JSONArray jsonArray = jsonObject.getJSONArray(SCHEMAS);
        Map<String, Schema> schemas = Maps.newHashMap();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject schemaObject = jsonArray.getJSONObject(i);
            schemaObjectValidator(schemaObject);

            Schema schema = new Schema();
            Map<String, StableSchema> stableSchemaMap = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : schemaObject.entrySet()) {
                switch (entry.getKey()) {
                    case SCHEMA_NAME: {
                        if (entry.getValue() == null) {
                            throw new SchemaException(String.format("schema: %s. %s's value is null",
                                    schemaStr, SCHEMA_NAME));
                        }
                        schema.setName(String.valueOf(entry.getValue()));
                        break;
                    }
                    case SCHEMA_DATABASE: {
                        schema.setDatabase(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLE_NAME: {
                        schema.setStableName(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLE_NAME_DEFAULT: {
                        schema.setDefaultStable(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLES_CONDITION: {
                        try {
                            JSONObject conditionObject =
                                    JSON.parseObject(String.valueOf(entry.getValue()));
                            Map<String, StableCondition> conditionMap = new HashMap<>();
                            for (Map.Entry<String, Object> conditionEntry :
                                    conditionObject.entrySet()) {
                                JSONObject condition =
                                        JSON.parseObject(conditionEntry.getValue().toString());
                                conditionMap.put(conditionEntry.getKey(),
                                        new StableCondition(condition.getString("key"),
                                                condition.getString("cmp"),
                                                condition.get("value")));
                            }
                            schema.setCondition(conditionMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case SCHEMA_STABLES: {
                        JSONObject stableObject = (JSONObject) JSON.toJSON(entry.getValue());
                        for (String stName : stableObject.keySet()) {
                            StableSchema stableSchema = new StableSchema();
                            stableSchemaMap.put(stName, stableSchema);
                            JSONObject tableObject = stableObject.getJSONObject(stName);
                            Map<String, Index> indexMap = Maps.newHashMap();
                            stableSchema.setIndexMap(indexMap);
                            for (Map.Entry<String, Object> tableEntry : tableObject.entrySet()) {
                                switch (tableEntry.getKey()) {
                                    case SCHEMA_TABLE_NAME: {
                                        if (tableEntry.getValue() == null) {
                                            throw new SchemaException(String.format("schema: %s. %s's value is null",
                                                    schemaStr, SCHEMA_TABLE_NAME));
                                        }
                                        JSONArray objects = JSON.parseArray(String.valueOf(tableEntry.getValue()));
                                        stableSchema.setTableName(objects.toArray(new String[0]));
                                        break;
                                    }
                                    case SCHEMA_DELIMITER: {
                                        stableSchema.setDelimiter(getString(tableEntry.getValue()));
                                        break;
                                    }
                                    case SCHEMA_STABLES_FILTER: {
                                        stableSchema.setFilters(JSON.parseArray(getString(tableEntry.getValue())).toJavaList(String.class));
                                        break;
                                    }
                                    default: {
                                        String key = tableEntry.getKey();
                                        String[] split = key.split(Schema.SEPARATOR);
                                        Map<String, Index> tmp = indexMap;
                                        for (int j = 0; j < split.length - 1; j++) {
                                            String name = split[j];
                                            if (tmp.containsKey(name)) {
                                                Index index = tmp.get(name);
                                                tmp = index.getIndexMap();
                                                continue;
                                            }
                                            Index index = new Index(name);
                                            tmp.put(name, index);
                                            tmp = index.getIndexMap();
                                        }
                                        String name = split[split.length - 1];
                                        Index index = new Index(name);
                                        JSONObject value = (JSONObject) JSON.toJSON(tableEntry.getValue());
                                        index.setColumn(json2Col(value));
                                        tmp.put(name, index);
                                    }
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        // other undefined schema
                    }
                }
            }
            if (null == schema.getStableName() && null == schema.getDefaultStable() && null == schema.getCondition()) {
                throw new SchemaException(String.format("schema: %s. %s and %s cannot be both empty ",
                        JSON.toJSONString(schema), SCHEMA_STABLE_NAME, SCHEMA_STABLE_NAME_DEFAULT));
            }
            if (!Strings.isNullOrEmpty(schema.getDefaultStable())
                    && !stableSchemaMap.containsKey(schema.getDefaultStable())) {
                throw new SchemaException(String.format("default stableName: %s cannot found in schema: %s.",
                        schema.getDefaultStable(), JSON.toJSONString(schema)));
            }
            schema.setStableSchemaMap(stableSchemaMap);
            schemas.put(schema.getName(), schema);
        }
        return schemas;
    }

    @Test
    void parseJson() throws IOException {
        String recordString = "{\n" +
                "    \"version\": \"1.0.0\",\n" +
                "    \"msg\": 0,\n" +
                "    \"type\": 201,\n" +
                "    \"server\": \"netprocess\",\n" +
                "    \"address\": \"**.**.**.**\",\n" +
                "    \"time\": \"1677136870139\",\n" +
                "    \"content\": [{\n" +
                "        \"base_list\": \"****\",\n" +
                "        \"third_bsl\": \"****\",\n" +
                "        \"epoch\": 1677136887,\n" +
                "        \"total_sat_num\": 37,\n" +
                "        \"gps_num\": 7,\n" +
                "        \"glo_num\": 3,\n" +
                "        \"cmp_num\": 17,\n" +
                "        \"gal_num\": 7,\n" +
                "        \"qzs_num\": 3,\n" +
                "        \"subnet_name\": \"***\"\n" +
                "    }]\n" +
                "}";
        recordString ="{\"version\":\"1.0.0\",\"msg\":0,\"type\":203,\"server\":\"netprocess\"," +
                "\"address\":\"10.42.80.191\",\"time\":\"1678343038910\",\"content\":[{\"subnet_id\":20,\"local_time\":\"2023-03-09 14:00:00\",\"i95\":3.794199}]}";
        recordString = "{\"msg\":\"0\",\"server\":\"gnssdata\",\"address\":\"100.110.52.27\"," +
                "\"time\":\"1678329394061\",\"type\":306,\"version\":\"1.0.0\"," +
                "\"content\":[{\"decode_latency\":134,\"mountpoint\":\"RBSNXY09\"},{\"decode_latency\":135,\"mountpoint\":\"RBSNHZ04\"},{\"decode_latency\":134,\"mountpoint\":\"RBSNAK06\"},{\"decode_latency\":385,\"mountpoint\":\"RBSNSL07\"},{\"decode_latency\":269,\"mountpoint\":\"RBHESJ09\"},{\"decode_latency\":34,\"mountpoint\":\"RBTJBD14\"},{\"decode_latency\":271,\"mountpoint\":\"RBSNWN01\"},{\"decode_latency\":1,\"mountpoint\":\"RZSDYT09\"},{\"decode_latency\":58,\"mountpoint\":\"RBBJTZ03\"},{\"decode_latency\":0,\"mountpoint\":\"RZHALY10\"},{\"decode_latency\":1,\"mountpoint\":\"RZSDZZ02\"},{\"decode_latency\":151,\"mountpoint\":\"RBSNYL10\"},{\"decode_latency\":1,\"mountpoint\":\"RZSDJI05\"},{\"decode_latency\":0,\"mountpoint\":\"RZFJZZ09\"},{\"decode_latency\":0,\"mountpoint\":\"RZHAKF01\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDLY07\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDTA03\"},{\"decode_latency\":1,\"mountpoint\":\"RZHAZK01\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDWH12\"},{\"decode_latency\":31,\"mountpoint\":\"RZGDSZ01\"},{\"decode_latency\":16,\"mountpoint\":\"RZHAJZ02\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDQD12\"},{\"decode_latency\":1,\"mountpoint\":\"RZHASM06\"},{\"decode_latency\":16,\"mountpoint\":\"RZFJNP01\"},{\"decode_latency\":151,\"mountpoint\":\"RBSNYA11\"},{\"decode_latency\":4,\"mountpoint\":\"RZSDDY05\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDWH03\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDLY16\"},{\"decode_latency\":1,\"mountpoint\":\"RZHASQ02\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDWF14\"},{\"decode_latency\":460,\"mountpoint\":\"RBSNYL01\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDZB06\"},{\"decode_latency\":23,\"mountpoint\":\"RZSDQD03\"},{\"decode_latency\":0,\"mountpoint\":\"RZHALY01\"},{\"decode_latency\":9,\"mountpoint\":\"RZHAXC01\"},{\"decode_latency\":0,\"mountpoint\":\"RZSDLC09\"},{\"decode_latency\":153,\"mountpoint\":\"RBSNBJ06\"},{\"decode_latency\":0,\"mountpoint\":\"RZHAAY02\"},{\"decode_latency\":21,\"mountpoint\":\"RZSDDZ04\"},{\"decode_latency\":2,\"mountpoint\":\"RZHAZM09\"},{\"decode_latency\":384,\"mountpoint\":\"RBSNYA02\"},{\"decode_latency\":4,\"mountpoint\":\"RZHAXY09\"},{\"decode_latency\":0,\"mountpoint\":\"RZHAZZ06\"},{\"decode_latency\":4,\"mountpoint\":\"RZHANY09\"},{\"decode_latency\":177,\"mountpoint\":\"RBHEXT04\"},{\"decode_latency\":1,\"mountpoint\":\"RZGDFS01\"},{\"decode_latency\":17,\"mountpoint\":\"RZSDWF05\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDGZ03\"},{\"decode_latency\":284,\"mountpoint\":\"RNHISZ29\"},{\"decode_latency\":0,\"mountpoint\":\"RNYNQJ01\"},{\"decode_latency\":515,\"mountpoint\":\"RNYNYX09\"},{\"decode_latency\":460,\"mountpoint\":\"RNHISY02\"},{\"decode_latency\":389,\"mountpoint\":\"RNGXLZ13\"},{\"decode_latency\":1,\"mountpoint\":\"RNJXJJ09\"},{\"decode_latency\":1,\"mountpoint\":\"RSAHHN03\"},{\"decode_latency\":0,\"mountpoint\":\"RSJSNT10\"},{\"decode_latency\":553,\"mountpoint\":\"RNJXSR02\"},{\"decode_latency\":611,\"mountpoint\":\"RNGXHZ08\"},{\"decode_latency\":604,\"mountpoint\":\"RNGXNN13\"},{\"decode_latency\":482,\"mountpoint\":\"RNHISZ10\"},{\"decode_latency\":108,\"mountpoint\":\"RNJXNC02\"},{\"decode_latency\":1,\"mountpoint\":\"RSJSYC07\"},{\"decode_latency\":2,\"mountpoint\":\"RSJSXZ05\"},{\"decode_latency\":45,\"mountpoint\":\"RSJSNJ01\"},{\"decode_latency\":12,\"mountpoint\":\"RSJSNT01\"},{\"decode_latency\":0,\"mountpoint\":\"RSJSYC16\"},{\"decode_latency\":1,\"mountpoint\":\"RSJSSQ09\"},{\"decode_latency\":17,\"mountpoint\":\"RSJSYZ04\"},{\"decode_latency\":2,\"mountpoint\":\"RSJSLY08\"},{\"decode_latency\":45,\"mountpoint\":\"RSJSXZ14\"},{\"decode_latency\":2,\"mountpoint\":\"RSJSHA06\"},{\"decode_latency\":459,\"mountpoint\":\"RNJXSR11\"},{\"decode_latency\":608,\"mountpoint\":\"RNHISZ01\"},{\"decode_latency\":613,\"mountpoint\":\"RNHISZ38\"},{\"decode_latency\":134,\"mountpoint\":\"RBHEBD11\"},{\"decode_latency\":150,\"mountpoint\":\"RBHEBD02\"},{\"decode_latency\":176,\"mountpoint\":\"RBHEHD06\"},{\"decode_latency\":177,\"mountpoint\":\"RBHECD10\"},{\"decode_latency\":0,\"mountpoint\":\"RZFJND03\"},{\"decode_latency\":0,\"mountpoint\":\"RZFJND12\"},{\"decode_latency\":1,\"mountpoint\":\"RZFJQZ08\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNYY14\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNSY10\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNSY01\"},{\"decode_latency\":21,\"mountpoint\":\"RZHNYI01\"},{\"decode_latency\":0,\"mountpoint\":\"RZFJFZ09\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNHY02\"},{\"decode_latency\":3,\"mountpoint\":\"RZHNHY11\"},{\"decode_latency\":5,\"mountpoint\":\"RZHNYY05\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNCD08\"},{\"decode_latency\":1,\"mountpoint\":\"RZHNYI10\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNXT01\"},{\"decode_latency\":3,\"mountpoint\":\"RZHNHY20\"},{\"decode_latency\":1,\"mountpoint\":\"RZGDYF05\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDQY04\"},{\"decode_latency\":2,\"mountpoint\":\"RZFJFZ18\"},{\"decode_latency\":2,\"mountpoint\":\"RZHNCD17\"},{\"decode_latency\":1,\"mountpoint\":\"RZFJLY04\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDJY01\"},{\"decode_latency\":175,\"mountpoint\":\"RBHECZ09\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDHY12\"},{\"decode_latency\":3,\"mountpoint\":\"RZGDSW04\"},{\"decode_latency\":1,\"mountpoint\":\"RZFJLY13\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDZQ12\"},{\"decode_latency\":3,\"mountpoint\":\"RZFJSM09\"},{\"decode_latency\":32,\"mountpoint\":\"RZGDZS01\"},{\"decode_latency\":31,\"mountpoint\":\"RZGDQY13\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDZQ03\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNLD09\"},{\"decode_latency\":32,\"mountpoint\":\"RZGDZH02\"},{\"decode_latency\":175,\"mountpoint\":\"RBHEHS10\"},{\"decode_latency\":0,\"mountpoint\":\"RZFJNP10\"},{\"decode_latency\":524,\"mountpoint\":\"RNJXFZ07\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNCZ15\"},{\"decode_latency\":16,\"mountpoint\":\"RZHNYZ04\"},{\"decode_latency\":3,\"mountpoint\":\"RZGDMZ07\"},{\"decode_latency\":0,\"mountpoint\":\"RNJXGZ06\"},{\"decode_latency\":255,\"mountpoint\":\"RBHEHS01\"},{\"decode_latency\":459,\"mountpoint\":\"RBHEZJ20\"},{\"decode_latency\":570,\"mountpoint\":\"RNJXYC01\"},{\"decode_latency\":4,\"mountpoint\":\"RZHNHH09\"},{\"decode_latency\":603,\"mountpoint\":\"RNGXLZ04\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDYJ01\"},{\"decode_latency\":665,\"mountpoint\":\"RNJXYC10\"},{\"decode_latency\":177,\"mountpoint\":\"RBHEZJ02\"},{\"decode_latency\":5,\"mountpoint\":\"RSAHHF10\"},{\"decode_latency\":499,\"mountpoint\":\"RNJXJD05\"},{\"decode_latency\":0,\"mountpoint\":\"RNJXJA17\"},{\"decode_latency\":541,\"mountpoint\":\"RNGXLB08\"},{\"decode_latency\":456,\"mountpoint\":\"RNGXCZ12\"},{\"decode_latency\":2,\"mountpoint\":\"RSZJTZ06\"},{\"decode_latency\":34,\"mountpoint\":\"RSHLHE01\"},{\"decode_latency\":5,\"mountpoint\":\"RSHLQQ19\"},{\"decode_latency\":0,\"mountpoint\":\"RSZJHU03\"},{\"decode_latency\":176,\"mountpoint\":\"RBHECD01\"},{\"decode_latency\":177,\"mountpoint\":\"RBHETS09\"},{\"decode_latency\":585,\"mountpoint\":\"RNGXHC29\"},{\"decode_latency\":7,\"mountpoint\":\"RSHLJM10\"},{\"decode_latency\":3,\"mountpoint\":\"RNGXGG08\"},{\"decode_latency\":0,\"mountpoint\":\"RSZJWZ12\"},{\"decode_latency\":563,\"mountpoint\":\"RNGXYL14\"},{\"decode_latency\":255,\"mountpoint\":\"RBHEZJ11\"},{\"decode_latency\":0,\"mountpoint\":\"RNSCYA05\"},{\"decode_latency\":183,\"mountpoint\":\"RNGXFC03\"},{\"decode_latency\":29,\"mountpoint\":\"RSXJKL01\"},{\"decode_latency\":45,\"mountpoint\":\"RSAHCZ15\"},{\"decode_latency\":492,\"mountpoint\":\"RNGXBS29\"},{\"decode_latency\":12,\"mountpoint\":\"RSHLYC06\"},{\"decode_latency\":1,\"mountpoint\":\"RSAHBB01\"},{\"decode_latency\":456,\"mountpoint\":\"RNSCNC04\"},{\"decode_latency\":4,\"mountpoint\":\"RSAHMA01\"},{\"decode_latency\":2,\"mountpoint\":\"RSHLHE10\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDMZ25\"},{\"decode_latency\":676,\"mountpoint\":\"RNGXGL03\"},{\"decode_latency\":32,\"mountpoint\":\"RSAHHF01\"},{\"decode_latency\":0,\"mountpoint\":\"RNGXBS10\"},{\"decode_latency\":1,\"mountpoint\":\"RSXJTC10\"},{\"decode_latency\":42,\"mountpoint\":\"RSAHCI03\"},{\"decode_latency\":8,\"mountpoint\":\"RSXJAL10\"},{\"decode_latency\":535,\"mountpoint\":\"RNGXNN22\"},{\"decode_latency\":522,\"mountpoint\":\"RNGXQZ09\"},{\"decode_latency\":13,\"mountpoint\":\"RSHLHH26\"},{\"decode_latency\":428,\"mountpoint\":\"RNYNNJ04\"},{\"decode_latency\":25,\"mountpoint\":\"RSHLDQ12\"},{\"decode_latency\":0,\"mountpoint\":\"RSZJJX08\"},{\"decode_latency\":508,\"mountpoint\":\"RNSCDY02\"},{\"decode_latency\":56,\"mountpoint\":\"RSAHAQ07\"},{\"decode_latency\":490,\"mountpoint\":\"RNGXGL12\"},{\"decode_latency\":15,\"mountpoint\":\"RNJXJA08\"},{\"decode_latency\":4,\"mountpoint\":\"RSZJNB06\"},{\"decode_latency\":389,\"mountpoint\":\"RNGXLZ22\"},{\"decode_latency\":2,\"mountpoint\":\"RSAHTL03\"},{\"decode_latency\":1,\"mountpoint\":\"RSHLDX06\"},{\"decode_latency\":26,\"mountpoint\":\"RSXJKT03\"},{\"decode_latency\":516,\"mountpoint\":\"RNGXGL21\"},{\"decode_latency\":31,\"mountpoint\":\"RSHLHH17\"},{\"decode_latency\":426,\"mountpoint\":\"RNGXBH02\"},{\"decode_latency\":0,\"mountpoint\":\"RSAHCZ06\"},{\"decode_latency\":2,\"mountpoint\":\"RSXJHM03\"},{\"decode_latency\":1,\"mountpoint\":\"RSHLJM01\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJTL02\"},{\"decode_latency\":35,\"mountpoint\":\"RSXJAL01\"},{\"decode_latency\":128,\"mountpoint\":\"RNGXGL30\"},{\"decode_latency\":56,\"mountpoint\":\"RSXJYL07\"},{\"decode_latency\":424,\"mountpoint\":\"RNGXYL05\"},{\"decode_latency\":5,\"mountpoint\":\"RSZJZS07\"},{\"decode_latency\":13,\"mountpoint\":\"RSAHBZ07\"},{\"decode_latency\":5,\"mountpoint\":\"RSHLSH07\"},{\"decode_latency\":23,\"mountpoint\":\"RSXJBE07\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJBY16\"},{\"decode_latency\":33,\"mountpoint\":\"RSXJHM12\"},{\"decode_latency\":33,\"mountpoint\":\"RSXJTC01\"},{\"decode_latency\":34,\"mountpoint\":\"RSXJLJ11\"},{\"decode_latency\":1,\"mountpoint\":\"RSXJAK02\"},{\"decode_latency\":571,\"mountpoint\":\"RNYNQJ10\"},{\"decode_latency\":460,\"mountpoint\":\"RNGXNN04\"},{\"decode_latency\":409,\"mountpoint\":\"RNJXGZ24\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJBY25\"},{\"decode_latency\":540,\"mountpoint\":\"RNYNDQ07\"},{\"decode_latency\":0,\"mountpoint\":\"RSHLMD16\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJBY07\"},{\"decode_latency\":3,\"mountpoint\":\"RSHLMD07\"},{\"decode_latency\":384,\"mountpoint\":\"RBHEQH03\"},{\"decode_latency\":40,\"mountpoint\":\"RSXJKS04\"},{\"decode_latency\":34,\"mountpoint\":\"RSAHHS08\"},{\"decode_latency\":99,\"mountpoint\":\"RNGXWZ03\"},{\"decode_latency\":3,\"mountpoint\":\"RSXJBY34\"},{\"decode_latency\":27,\"mountpoint\":\"RSZJJH04\"},{\"decode_latency\":45,\"mountpoint\":\"RSHLDX15\"},{\"decode_latency\":515,\"mountpoint\":\"RNSCLZ03\"},{\"decode_latency\":535,\"mountpoint\":\"RNJXGZ15\"},{\"decode_latency\":462,\"mountpoint\":\"RNYNDH06\"},{\"decode_latency\":1,\"mountpoint\":\"RSZJWZ03\"},{\"decode_latency\":389,\"mountpoint\":\"RNYNPE07\"},{\"decode_latency\":508,\"mountpoint\":\"RNGXHC10\"},{\"decode_latency\":532,\"mountpoint\":\"RNSCGY09\"},{\"decode_latency\":520,\"mountpoint\":\"RNSCBZ03\"},{\"decode_latency\":635,\"mountpoint\":\"RNYNWS06\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJLJ02\"},{\"decode_latency\":0,\"mountpoint\":\"RSHLDX24\"},{\"decode_latency\":0,\"mountpoint\":\"RSHLHH08\"},{\"decode_latency\":535,\"mountpoint\":\"RNGXLB17\"},{\"decode_latency\":424,\"mountpoint\":\"RNSCDZ01\"},{\"decode_latency\":4,\"mountpoint\":\"RSZJLS01\"},{\"decode_latency\":491,\"mountpoint\":\"RNYNLC03\"},{\"decode_latency\":31,\"mountpoint\":\"RSZJLS10\"},{\"decode_latency\":2,\"mountpoint\":\"RSXJYL16\"},{\"decode_latency\":26,\"mountpoint\":\"RSZJHZ08\"},{\"decode_latency\":329,\"mountpoint\":\"RNSCMY03\"},{\"decode_latency\":176,\"mountpoint\":\"RBGSGN04\"},{\"decode_latency\":4,\"mountpoint\":\"RZHNXX07\"},{\"decode_latency\":0,\"mountpoint\":\"RZHNYZ13\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMHH04\"},{\"decode_latency\":1,\"mountpoint\":\"RZHNCZ06\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMEE19\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMCF29\"},{\"decode_latency\":17,\"mountpoint\":\"RZNMCF38\"},{\"decode_latency\":16,\"mountpoint\":\"RZHNYZ22\"},{\"decode_latency\":1,\"mountpoint\":\"RZNMAL07\"},{\"decode_latency\":1,\"mountpoint\":\"RZNMBY12\"},{\"decode_latency\":1,\"mountpoint\":\"RZNMTL08\"},{\"decode_latency\":1,\"mountpoint\":\"RZNMAL16\"},{\"decode_latency\":1,\"mountpoint\":\"RZGZGY02\"},{\"decode_latency\":3,\"mountpoint\":\"RZNMCF01\"},{\"decode_latency\":1,\"mountpoint\":\"RZGZQD12\"},{\"decode_latency\":175,\"mountpoint\":\"RBJLSP04\"},{\"decode_latency\":1,\"mountpoint\":\"RZHNHH18\"},{\"decode_latency\":0,\"mountpoint\":\"RZGDSG19\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMXL40\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMXL22\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZAS04\"},{\"decode_latency\":1,\"mountpoint\":\"RZGZBJ02\"},{\"decode_latency\":341,\"mountpoint\":\"RBGSJC02\"},{\"decode_latency\":1,\"mountpoint\":\"RZNMTL26\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZQN12\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMTL17\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMAL25\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZZY12\"},{\"decode_latency\":5,\"mountpoint\":\"RZNMXL13\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZZY03\"},{\"decode_latency\":152,\"mountpoint\":\"RBJLLY02\"},{\"decode_latency\":5,\"mountpoint\":\"RZNMBY03\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMWL05\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMXL04\"},{\"decode_latency\":150,\"mountpoint\":\"RBJLCC03\"},{\"decode_latency\":631,\"mountpoint\":\"RBGSZY04\"},{\"decode_latency\":3,\"mountpoint\":\"RZNMBT08\"},{\"decode_latency\":2,\"mountpoint\":\"RZNMXL31\"},{\"decode_latency\":428,\"mountpoint\":\"RBGSJQ08\"},{\"decode_latency\":2,\"mountpoint\":\"RZNMWL14\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMAL34\"},{\"decode_latency\":31,\"mountpoint\":\"RZGZBJ11\"},{\"decode_latency\":1,\"mountpoint\":\"RZNMXA14\"},{\"decode_latency\":32,\"mountpoint\":\"RZNMXA05\"},{\"decode_latency\":32,\"mountpoint\":\"RZNMCF10\"},{\"decode_latency\":508,\"mountpoint\":\"RNSCLS19\"},{\"decode_latency\":3,\"mountpoint\":\"RZGZTR06\"},{\"decode_latency\":3,\"mountpoint\":\"RZXZRK44\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZQN03\"},{\"decode_latency\":0,\"mountpoint\":\"RZXZSN13\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMHL37\"},{\"decode_latency\":275,\"mountpoint\":\"RNYNWS15\"},{\"decode_latency\":0,\"mountpoint\":\"RNSCAB17\"},{\"decode_latency\":137,\"mountpoint\":\"RNYNLJ06\"},{\"decode_latency\":480,\"mountpoint\":\"RNGXHC01\"},{\"decode_latency\":0,\"mountpoint\":\"RZNMHL19\"},{\"decode_latency\":256,\"mountpoint\":\"RBGSQY03\"},{\"decode_latency\":539,\"mountpoint\":\"RNSCGZ26\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZQD03\"},{\"decode_latency\":379,\"mountpoint\":\"RNSCGA03\"},{\"decode_latency\":558,\"mountpoint\":\"RNYNZT11\"},{\"decode_latency\":3,\"mountpoint\":\"RSSHCM01\"},{\"decode_latency\":13,\"mountpoint\":\"RSLNJZ02\"},{\"decode_latency\":51,\"mountpoint\":\"RSSXLL07\"},{\"decode_latency\":34,\"mountpoint\":\"RSAHLA11\"},{\"decode_latency\":56,\"mountpoint\":\"RSAHXC08\"},{\"decode_latency\":50,\"mountpoint\":\"RSLNBX02\"},{\"decode_latency\":3,\"mountpoint\":\"RSLNTL06\"},{\"decode_latency\":0,\"mountpoint\":\"RSNXWZ07\"},{\"decode_latency\":27,\"mountpoint\":\"RSAHLA02\"},{\"decode_latency\":0,\"mountpoint\":\"RSLNPJ02\"},{\"decode_latency\":0,\"mountpoint\":\"RZGZQX03\"},{\"decode_latency\":0,\"mountpoint\":\"RNSCAB08\"},{\"decode_latency\":0,\"mountpoint\":\"RSAHFY04\"},{\"decode_latency\":61,\"mountpoint\":\"RSSXYZ19\"},{\"decode_latency\":438,\"mountpoint\":\"RNSCLE04\"},{\"decode_latency\":0,\"mountpoint\":\"RSNXSZ01\"},{\"decode_latency\":0,\"mountpoint\":\"RSSXLF03\"},{\"decode_latency\":7,\"mountpoint\":\"RSSXSZ06\"},{\"decode_latency\":47,\"mountpoint\":\"RSLNDL02\"},{\"decode_latency\":390,\"mountpoint\":\"RNYNDL02\"},{\"decode_latency\":33,\"mountpoint\":\"RSSXJZ05\"},{\"decode_latency\":420,\"mountpoint\":\"RNYNKM04\"},{\"decode_latency\":5,\"mountpoint\":\"RSAHWH04\"},{\"decode_latency\":1,\"mountpoint\":\"RSLNFS03\"},{\"decode_latency\":2,\"mountpoint\":\"RSCQYO04\"},{\"decode_latency\":0,\"mountpoint\":\"RSSXDT07\"},{\"decode_latency\":741,\"mountpoint\":\"RNSCCD04\"},{\"decode_latency\":514,\"mountpoint\":\"RNSCDZ10\"},{\"decode_latency\":33,\"mountpoint\":\"RSCQYY04\"},{\"decode_latency\":490,\"mountpoint\":\"RNYNDL11\"},{\"decode_latency\":41,\"mountpoint\":\"RSSXYC03\"},{\"decode_latency\":111,\"mountpoint\":\"RNYNHH02\"},{\"decode_latency\":374,\"mountpoint\":\"RNYNHH11\"},{\"decode_latency\":0,\"mountpoint\":\"RSSXYC12\"},{\"decode_latency\":381,\"mountpoint\":\"RNYNKM13\"},{\"decode_latency\":4,\"mountpoint\":\"RSSXCZ11\"},{\"decode_latency\":493,\"mountpoint\":\"RNSCGZ17\"},{\"decode_latency\":381,\"mountpoint\":\"RNSCGZ08\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJKS22\"},{\"decode_latency\":45,\"mountpoint\":\"RSLNSY04\"},{\"decode_latency\":1,\"mountpoint\":\"RSSXJC08\"},{\"decode_latency\":518,\"mountpoint\":\"RNYNCX01\"},{\"decode_latency\":2,\"mountpoint\":\"RSSXTY06\"},{\"decode_latency\":411,\"mountpoint\":\"RNYNZT02\"},{\"decode_latency\":1,\"mountpoint\":\"RSCQXS01\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJHT06\"},{\"decode_latency\":0,\"mountpoint\":\"RSQHXN01\"},{\"decode_latency\":0,\"mountpoint\":\"RNYNPE16\"},{\"decode_latency\":0,\"mountpoint\":\"RSXJKS13\"},{\"decode_latency\":434,\"mountpoint\":\"RNYNBS07\"},{\"decode_latency\":477,\"mountpoint\":\"RNYNLC12\"},{\"decode_latency\":0,\"mountpoint\":\"RSLNYK02\"},{\"decode_latency\":5,\"mountpoint\":\"RSLNLY01\"},{\"decode_latency\":9,\"mountpoint\":\"RSLNDL11\"},{\"decode_latency\":2,\"mountpoint\":\"RZGDLC01\"},{\"decode_latency\":174,\"mountpoint\":\"RBJLJL07\"},{\"decode_latency\":406,\"mountpoint\":\"RBJLBC04\"},{\"decode_latency\":176,\"mountpoint\":\"RBJLYB11\"},{\"decode_latency\":176,\"mountpoint\":\"RBJLBS08\"},{\"decode_latency\":31,\"mountpoint\":\"RZHBHG02\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBHG11\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBSZ02\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBJZ01\"},{\"decode_latency\":24,\"mountpoint\":\"RZHBWH06\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBYC09\"},{\"decode_latency\":25,\"mountpoint\":\"RZHBXG06\"},{\"decode_latency\":32,\"mountpoint\":\"RZHBJZ10\"},{\"decode_latency\":4,\"mountpoint\":\"RZHNCS03\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBSU07\"},{\"decode_latency\":24,\"mountpoint\":\"RZHBJM13\"},{\"decode_latency\":24,\"mountpoint\":\"RZHNZZ03\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBJM04\"},{\"decode_latency\":21,\"mountpoint\":\"RZHNCS21\"},{\"decode_latency\":1,\"mountpoint\":\"RZHBSY21\"},{\"decode_latency\":18,\"mountpoint\":\"RZHBXY08\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBES03\"},{\"decode_latency\":8,\"mountpoint\":\"RZHBES12\"},{\"decode_latency\":268,\"mountpoint\":\"RBJLSY05\"},{\"decode_latency\":24,\"mountpoint\":\"RZHBSY03\"},{\"decode_latency\":0,\"mountpoint\":\"RZHBES21\"},{\"decode_latency\":56,\"mountpoint\":\"RZXZCD19\"},{\"decode_latency\":3,\"mountpoint\":\"RZSCDY06\"},{\"decode_latency\":335,\"mountpoint\":\"RBJLTH01\"},{\"decode_latency\":0,\"mountpoint\":\"RZXZCD28\"},{\"decode_latency\":0,\"mountpoint\":\"RZSCGZ39\"},{\"decode_latency\":31,\"mountpoint\":\"RZXZNQ33\"},{\"decode_latency\":8,\"mountpoint\":\"RZXZRK35\"},{\"decode_latency\":46,\"mountpoint\":\"RZXZAL13\"},{\"decode_latency\":2,\"mountpoint\":\"RZXZLZ18\"},{\"decode_latency\":3,\"mountpoint\":\"RZHBXY17\"},{\"decode_latency\":2,\"mountpoint\":\"RZXZRK17\"},{\"decode_latency\":1,\"mountpoint\":\"RZXZRK26\"},{\"decode_latency\":0,\"mountpoint\":\"RZXZAL22\"},{\"decode_latency\":16,\"mountpoint\":\"RZHBSY12\"},{\"decode_latency\":0,\"mountpoint\":\"RSNXGY04\"},{\"decode_latency\":254,\"mountpoint\":\"RNYNCX10\"},{\"decode_latency\":0,\"mountpoint\":\"RSHLDQ03\"},{\"decode_latency\":0,\"mountpoint\":\"RZXZNQ06\"},{\"decode_latency\":3,\"mountpoint\":\"RSXJKZ07\"},{\"decode_latency\":0,\"mountpoint\":\"RSQHHX34\"},{\"decode_latency\":5,\"mountpoint\":\"RSQHHX16\"},{\"decode_latency\":3,\"mountpoint\":\"RSQHHX52\"},{\"decode_latency\":2,\"mountpoint\":\"RZXZLZ09\"},{\"decode_latency\":2,\"mountpoint\":\"RSXJAK20\"},{\"decode_latency\":45,\"mountpoint\":\"RSQHHX43\"},{\"decode_latency\":2,\"mountpoint\":\"RSQHHX07\"},{\"decode_latency\":0,\"mountpoint\":\"RSQHYS23\"},{\"decode_latency\":0,\"mountpoint\":\"RSQHYS14\"},{\"decode_latency\":12,\"mountpoint\":\"RSQHHX25\"},{\"decode_latency\":1,\"mountpoint\":\"RZXZAL04\"},{\"decode_latency\":45,\"mountpoint\":\"RNSCGZ35\"},{\"decode_latency\":2,\"mountpoint\":\"RSQHYS05\"},{\"decode_latency\":0,\"mountpoint\":\"RZFJSM18\"},{\"decode_latency\":22,\"mountpoint\":\"RSXJHT15\"},{\"decode_latency\":609,\"mountpoint\":\"RNSCYB04\"}],\"desc\":\"station normal data\"}";
        // recordString = "{\n" +
        //         "    \"version\": \"1.0.0\",\n" +
        //         "    \"msg\": 0,\n" +
        //         "    \"type\": 202,\n" +
        //         "    \"server\": \"netprocess\",\n" +
        //         "    \"address\": \"**.**.**.**\",\n" +
        //         "    \"time\": \"1677136818155\",\n" +
        //         "    \"content\": [{\n" +
        //         "        \"base_list\": \"****\",\n" +
        //         "        \"epoch\": 1677136835,\n" +
        //         "        \"total_sat_num\": 39,\n" +
        //         "        \"gps_num\": 7,\n" +
        //         "        \"glo_num\": 5,\n" +
        //         "        \"cmp_num\": 18,\n" +
        //         "        \"gal_num\": 7,\n" +
        //         "        \"qzs_num\": 2,\n" +
        //         "        \"subnet_name\": \"***\"\n" +
        //         "    }]\n" +
        //         "}";
        Map<String, Schema> schemaMap = testSchema();
        Schema schema = schemaMap.get("devSchema");
        try {
            List<JsonSql> list = schemaHandler(schema, recordString);
            if (null != list && list.size() > 0) {
                StringBuilder sb = new StringBuilder("insert into ");
                for (JsonSql json : list) {
                    sb.append("`").append(json.gettName()).append("`");
                    sb.append(" using ").append(json.getStName()).append(" (");
                    StringBuilder tv = new StringBuilder(" tags (");
                    int i = 0;
                    for (Map.Entry<String, String> entry : json.getTag().entrySet()) {
                        sb.append(entry.getKey());
                        tv.append(entry.getValue());
                        if (i == json.getTag().size() - 1) {
                            sb.append(") ");
                            tv.append(") ");
                        } else {
                            sb.append(", ");
                            tv.append(", ");
                        }
                        i++;
                    }
                    sb.append(tv).append(" (");

                    StringBuilder cv = new StringBuilder(" values (");
                    i = 0;
                    for (Map.Entry<String, String> entry : json.getCols().entrySet()) {
                        sb.append(entry.getKey());
                        String v = entry.getValue();
                        cv.append(v);

                        if (i == json.getCols().size() - 1) {
                            sb.append(") ");
                            cv.append(") ");
                        } else {
                            sb.append(", ");
                            cv.append(", ");
                        }
                        i++;
                    }
                    sb.append(cv);
                }
                System.out.println(sb);
            }
        } catch (RecordException | JSONException e) {
            e.printStackTrace();
        }
    }

    private List<JsonSql> schemaHandler(Schema schema, String recordString) {
        if (null == recordString) {
            throw new RecordException("record is null");
        }
        Object obj = JSON.parse(recordString);
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            JsonSql jsonSql = convertJSONObject(schema, jsonObject);
            if (null != jsonSql) {
                return Collections.singletonList(jsonSql);
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            List<JsonSql> jsonSqlList = new ArrayList<>();
            jsonArray.forEach(o -> {
                JSONObject jsonObject = (JSONObject) JSON.toJSON(o);
                JsonSql jsonSql = convertJSONObject(schema, jsonObject);
                if (null != jsonSql) {
                    jsonSqlList.add(jsonSql);
                }
            });
            return jsonSqlList;
        }
        return Collections.EMPTY_LIST;
    }

    JsonSql convertJSONObject(Schema schema, JSONObject jsonObject) {
        JsonSql sql = new JsonSql();

        String stableName = schema.getStableName();
        String defaultStable = schema.getDefaultStable();
        AtomicReference<String> name = new AtomicReference<>();

        if (null != schema.getCondition()) {
            schema.getCondition().forEach((stb, condition) -> {
                switch (condition.getCmp()) {
                    case "=":
                        if (condition.getValue().equals(jsonObject.get(condition.getKey()))) {
                            name.set(stb);
                        }
                        break;
                    case "<>":
                        if (!condition.getValue().equals(jsonObject.get(condition.getKey()))) {
                            name.set(stb);
                        }
                        break;
                    case "in":
                        Object v= jsonObject.getString(condition.getKey());
                        if (StringUtils.isNumeric(jsonObject.getString(condition.getKey()))) {
                            v = Integer.parseInt((String)v);
                        }
                        if (((JSONArray) condition.getValue()).indexOf(v) > -1) {
                            name.set(stb);
                        }
                        break;
                }
            });
        } else {
            if (!Strings.isNullOrEmpty(stableName)) {
                String[] split = stableName.split(Schema.SEPARATOR);
                String s = findValue(split, jsonObject);
                if (s != null) {
                    name.set(s);
                } else {
                    if (Strings.isNullOrEmpty(defaultStable)) {
                        String msg = String.format("record : %s could not be found a suitable configuration in schema: %s."
                                , JSON.toJSONString(jsonObject), schema.getName());
                        throw new RecordException(msg);
                    }
                    name.set(defaultStable);
                }
            } else {
                if (Strings.isNullOrEmpty(defaultStable)) {
                    String msg = String.format("record : %s could not be found a suitable configuration in schema: %s."
                            , JSON.toJSONString(jsonObject), schema.getName());
                    throw new RecordException(msg);
                }
                name.set(defaultStable);
            }
        }
        sql.setStName(name.get());

        StableSchema stableScheme = schema.getStableSchemaMap().get(name.toString());
        if (null == stableScheme) {
            String msg = String.format("record : %s could not be found a suitable configuration in schema: %s."
                    , JSON.toJSONString(jsonObject), schema.getName());
            throw new RecordException(msg);
        }
        for (Map.Entry<String, Index> entry : stableScheme.getIndexMap().entrySet()) {
            if (jsonObject.get(entry.getKey()) instanceof JSONArray) {
                int i = ((JSONArray) (jsonObject.get(entry.getKey()))).size() - 1;
                convert(entry.getValue(), ((JSONArray) (jsonObject.get(entry.getKey()))).get(i), sql);
            } else {
                convert(entry.getValue(), jsonObject.get(entry.getKey()), sql);
            }
        }

        if (stableScheme.getDelimiter() != null) {
            sql.settName(Arrays.stream(stableScheme.getTableName()).map(c -> sql.getAll().get(c))
                    .collect(Collectors.joining(stableScheme.getDelimiter())));
        } else {
            sql.settName(sql.getAll().get(stableScheme.getTableName()[0]));
        }
        // Except for the ts column, if there is no other column, it is not inserted
        if (sql.getCols().size() == 1) {
            return null;
        }
        if (null != stableScheme.getFilters() && stableScheme.getFilters().size() > 0) {
            for (String filter: stableScheme.getFilters()) {
                if (!(Boolean) RunnerUtil.run(MatchKeyUtil.replaceKey(filter, sql.getAll()))) {
                    return null;
                }
            }
        }

        return sql;
    }

    private String findValue(String[] strings, JSONObject jsonObject) {
        try {
            if (strings.length == 1) {
                return jsonObject.getString(strings[0]);
            }
            String key = strings[0];
            if (jsonObject.containsKey(key)) {
                JSONObject json = jsonObject.getJSONObject(key);
                strings = Arrays.copyOfRange(strings, 1, strings.length);
                findValue(strings, json);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private void convert(Index index, Object object, JsonSql sql) {
        if (index.getIndexMap().isEmpty()) {
            Column column = index.getColumn();
            Map<String, String> tmp;
            Map<String, String> all = sql.getAll();
            if (column.getOther() != null) {
                all.put(column.getOther(), String.valueOf(object));
            } else {
                if (column.isTag()) {
                    tmp = sql.getTag();
                } else {
                    tmp = sql.getCols();
                }
                if (null == object) {
                    if (null != column.getDefaultValue()) {
                        tmp.put(column.getTargetColumn(), checkAndConvertString(column.getDefaultValue()));
                        all.put(column.getTargetColumn(), String.valueOf(column.getDefaultValue()));
                    } else {
                        if (!column.isOptional()) {
                            throw new RecordException("The " + index.getName() + " field is blank");
                        }
                    }
                } else {
                    if (column.isTypeEquals()) {
                        tmp.put(column.getTargetColumn(), checkAndConvertString(object));
                        all.put(column.getTargetColumn(), String.valueOf(object));
                    } else {
                        if ("string".equalsIgnoreCase(column.getTargetType())) {
                            tmp.put(column.getTargetColumn(), "'" + object + "'");
                            all.put(column.getTargetColumn(), String.valueOf(object));
                        }
                    }
                }
            }
        } else {
            for (Map.Entry<String, Index> entry : index.getIndexMap().entrySet()) {
                convert(entry.getValue(), ((JSONObject) object).get(entry.getKey()), sql);
            }
        }
    }

    private String checkAndConvertString(Object o) {
        if (o == null) return null;
        if (StringUtils.isNumeric(String.valueOf(o))) {
            return String.valueOf(o);
        }
        if (o instanceof String) return "'" + o + "'";
        return String.valueOf(o);
    }
}
