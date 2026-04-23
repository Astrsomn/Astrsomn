package com.astrsomn.vector.chroma.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;
import com.astrsomn.core.common.utils.StringUtils;

import java.util.Map;

/**
 * 从向量源 {@code CONFIG_JSON} 解析 Chroma 专用选项（与 {@link com.astrsomn.vector.qdrant.internal.QdrantConfigSupport} 同模式）。
 */
public final class ChromaConfigSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ChromaConfigSupport() {}

    public static boolean readUseTls(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return false;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {});
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

    /**
     * 未配置时默认 {@link ChromaApiVersion#V2}，以适配 Chroma 0.7+；仅旧实例可显式配置 {@code "apiVersion": "V1"}。
     */
    public static ChromaApiVersion readApiVersion(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return ChromaApiVersion.V2;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {});
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

    /** 未配置时返回 {@code null}，由 LangChain4j 使用默认租户名。 */
    public static String readTenantName(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return null;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {});
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
