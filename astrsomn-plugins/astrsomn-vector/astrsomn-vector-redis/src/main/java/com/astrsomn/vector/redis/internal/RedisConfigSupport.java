package com.astrsomn.vector.redis.internal;

import com.astrsomn.common.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Helpers to read optional configuration values from the data source's {@code configJson}.
 */
public final class RedisConfigSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RedisConfigSupport() {
    }

    /**
     * @return {@code true} if the Redis connection should use SSL/TLS
     */
    public static boolean readUseSsl(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return false;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {
            });
            Object v = m.get(RedisVecConstants.CONFIG_USE_SSL);
            if (v instanceof Boolean) {
                return (Boolean) v;
            }
            if (v instanceof String) {
                return Boolean.parseBoolean(((String) v).trim());
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return the vector index algorithm — "HNSW" or "FLAT"; defaults to "HNSW"
     */
    public static String readIndexType(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return "HNSW";
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {
            });
            Object v = m.get(RedisVecConstants.CONFIG_INDEX_TYPE);
            if (v instanceof String) {
                String s = ((String) v).trim();
                if ("FLAT".equalsIgnoreCase(s) || "HNSW".equalsIgnoreCase(s)) {
                    return s.toUpperCase();
                }
            }
            return "HNSW";
        } catch (Exception e) {
            return "HNSW";
        }
    }
}
