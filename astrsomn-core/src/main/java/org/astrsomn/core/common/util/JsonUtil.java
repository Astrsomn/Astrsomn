package org.astrsomn.core.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import java.util.Collections;
import java.util.List;

public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 解析 JSON 字符串为 List
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            // 构造泛型类型：List<clazz>
            JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON array", e);
        }
    }

    /**
     * 重载：处理 Object 输入 (兼容你的调用写法)
     */
    public static <T> List<T> parseArray(Object input, Class<T> clazz) {
        if (input == null) {
            return null;
        }
        if (input instanceof String) {
            return parseArray((String) input, clazz);
        }
        // 如果输入已经是对象（比如 JsonNode 或其他），先转为字符串再解析，或者直接转换
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            // convertValue 可以处理从 Object 到 Target 的转换，无需先转字符串
            return MAPPER.convertValue(input, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON array from object", e);
        }
    }
}