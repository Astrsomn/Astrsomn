package com.astrsomn.common.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

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

    /**
     * 将 Map 转换为 JSON 字符串
     * 专门针对基础封装设计，处理了转义、嵌套和常见数据类型
     */
    public static String toJson(Map<String, Object> map) {
        if (map == null) return "null";

        StringBuilder sb = new StringBuilder();
        serializeMap(map, sb);
        return sb.toString();
    }

    public static void serializeMap(Map<?, ?> map, StringBuilder sb) {
        sb.append("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");

            // 处理 Key (强制转为字符串并转义)
            String key = String.valueOf(entry.getKey());
            sb.append("\"").append(escape(key)).append("\":");

            // 处理 Value
            serializeValue(entry.getValue(), sb);

            first = false;
        }
        sb.append("}");
    }

    public static void serializeValue(Object value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append("\"").append(escape((String) value)).append("\"");
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Map) {
            serializeMap((Map<?, ?>) value, sb);
        } else if (value instanceof Iterable) {
            serializeIterable((Iterable<?>) value, sb);
        } else if (value.getClass().isArray()) {
            serializeArray(value, sb);
        } else {
            // 对于未知对象，调用 toString 并转义
            sb.append("\"").append(escape(value.toString())).append("\"");
        }
    }

    public static void serializeIterable(Iterable<?> iterable, StringBuilder sb) {
        sb.append("[");
        boolean first = true;
        for (Object item : iterable) {
            if (!first) sb.append(",");
            serializeValue(item, sb);
            first = false;
        }
        sb.append("]");
    }

    public static void serializeArray(Object array, StringBuilder sb) {
        sb.append("[");
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(",");
            serializeValue(java.lang.reflect.Array.get(array, i), sb);
        }
        sb.append("]");
    }

    /**
     * 关键：对字符串进行 JSON 标准转义
     */
    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    // 过滤控制字符，确保 JSON 格式合法
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u").append("0000", 0, 4 - hex.length()).append(hex);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}