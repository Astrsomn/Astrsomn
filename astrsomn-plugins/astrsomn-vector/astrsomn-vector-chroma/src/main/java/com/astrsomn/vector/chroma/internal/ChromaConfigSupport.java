package com.astrsomn.vector.chroma.internal;

import com.astrsomn.common.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;

import java.util.Map;


public final class ChromaConfigSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ChromaConfigSupport() {
    }

    public static boolean readUseTls(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return false;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {
            });
            Object v = m.get(ChromaVecConstants.CONFIG_USE_TLS);
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

    
    public static ChromaApiVersion readApiVersion(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return ChromaApiVersion.V2;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {
            });
            Object v = m.get(ChromaVecConstants.CONFIG_API_VERSION);
            if (v == null) {
                return ChromaApiVersion.V2;
            }
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) {
                return ChromaApiVersion.V2;
            }
            String u = s.toUpperCase();
            if ("V1".equals(u) || "1".equals(s)) {
                return ChromaApiVersion.V1;
            }
            if ("V2".equals(u) || "2".equals(s)) {
                return ChromaApiVersion.V2;
            }
            throw new IllegalArgumentException("Invalid Chroma apiVersion in CONFIG_JSON: " + s + " (expected V1 or V2)");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return ChromaApiVersion.V2;
        }
    }

    
    public static String readTenantName(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return null;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {
            });
            Object v = m.get(ChromaVecConstants.CONFIG_TENANT_NAME);
            if (v == null) {
                return null;
            }
            String s = String.valueOf(v).trim();
            return s.isEmpty() ? null : s;
        } catch (Exception e) {
            return null;
        }
    }
}
