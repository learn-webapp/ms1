package com.webapp.ms1.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonUtil {

    public static Map<String, String> parseJsonToMap(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // Convert the JSON string to a Map
        Map<String, Object> map = objectMapper.readValue(json, Map.class);

        // Convert the values to Strings and return the map
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
    }
}
