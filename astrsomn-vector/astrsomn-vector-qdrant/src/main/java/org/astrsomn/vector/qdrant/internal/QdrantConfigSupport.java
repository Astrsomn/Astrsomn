package org.astrsomn.vector.qdrant.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.astrsomn.core.common.util.StringUtils;

import java.util.Collections;
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
}
