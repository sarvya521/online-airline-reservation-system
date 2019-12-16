package com.oars.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil {
    private JsonUtil() {

    }
    public static String convertToJson(Object obj) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        Gson GSON = new GsonBuilder().create();
        return GSON.toJson(obj);
    }
}
