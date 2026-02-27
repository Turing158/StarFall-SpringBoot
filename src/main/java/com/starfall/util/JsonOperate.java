package com.starfall.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonOperate {
    public static String toJson(Object object) {
        return toJson(object,true,JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object object,boolean needTab) {
        return toJson(object,needTab,JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object object,boolean needTab, JsonInclude.Include include) {
        ObjectMapper mapper = new ObjectMapper();
        if(needTab){
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        mapper.setSerializationInclusion(include);
        ObjectWriter objectWriter= mapper.writerWithDefaultPrettyPrinter();
        String json = null;
        try {
            json = objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static <T> T toObject(String json, Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        T object = null;
        try {
            object = mapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return object;
    }

}
