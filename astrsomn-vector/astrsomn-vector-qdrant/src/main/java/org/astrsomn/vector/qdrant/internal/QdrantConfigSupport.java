package org.astrsomn.vector.qdrant.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.astrsomn.core.common.utils.StringUtils;

import java.util.Map;

public final class QdrantConfigSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private QdrantConfigSupport() {}

    public static boolean readUseTls(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return false;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {});
            Object v = m.get(QdrantVecConstants.CONFIG_USE_TLS);
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
     * 是否在 {@link io.qdrant.client.QdrantGrpcClient.Builder#build()} 时做客户端与服务端版本兼容检查。
     * 缺省为 {@code false}，避免在测试连接或网络异常时于 build 阶段额外阻塞；若需严格校验可在 {@code CONFIG_JSON} 中设
     * {@code "checkCompatibility": true}。
     */
    public static boolean readCheckCompatibility(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return false;
        }
        try {
            Map<String, Object> m = MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {});
            Object v = m.get(QdrantVecConstants.CONFIG_CHECK_COMPATIBILITY);
            if (v == null) {
                return false;
            }
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
}
