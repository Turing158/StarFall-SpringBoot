package com.starfall.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonOperate {
    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectWriter objectWriter= mapper.writerWithDefaultPrettyPrinter();
        String json = null;
        try {
            json = objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static <T> T toObject(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T object = null;
        try {
            object = mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return object;
    }
}
