package com.astrsomn.api.runtime.common.langchain.extension.vector.support;

import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.common.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public final class AiVecSourceConnectionProperties {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String databaseName;
    private final String token;

    private AiVecSourceConnectionProperties(
            String host,
            int port,
            String username,
            String password,
            String databaseName,
            String token) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.token = token;
    }

    public static AiVecSourceConnectionProperties from(AiVecSourceEntity entity) {
        Objects.requireNonNull(entity, "entity");
        Map<String, Object> json = parseConfigJson(entity.getConfigJson());
        String host = firstNonBlank(entity.getHost(), json, AiVecDriverEnum.ParamEnum.HOST);
        String portRaw = firstNonBlank(entity.getPort(), json, AiVecDriverEnum.ParamEnum.PORT);
        int port = parsePort(portRaw);
        String username = firstNonBlank(entity.getUsername(), json, AiVecDriverEnum.ParamEnum.USERNAME);
        String password = firstNonBlank(entity.getPassword(), json, AiVecDriverEnum.ParamEnum.PASSWORD);
        String databaseName = firstNonBlank(entity.getDatabaseName(), json, AiVecDriverEnum.ParamEnum.DATABASE_NAME);
        String token = firstNonBlank(entity.getToken(), json, AiVecDriverEnum.ParamEnum.TOKEN);
        return new AiVecSourceConnectionProperties(host, port, username, password, databaseName, token);
    }

    private static Map<String, Object> parseConfigJson(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(configJson.trim(), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("CONFIG_JSON is not valid JSON for vector source", e);
        }
    }

    private static String firstNonBlank(String entityValue, Map<String, Object> json, AiVecDriverEnum.ParamEnum key) {
        if (StringUtils.isNotBlank(entityValue)) {
            return entityValue.trim();
        }
        return stringFromJson(json, key.getCode());
    }

    private static String stringFromJson(Map<String, Object> json, String key) {
        if (json == null || key == null) {
            return null;
        }
        Object v = json.get(key);
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return v.toString();
        }
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    private static int parsePort(String portRaw) {
        if (StringUtils.isBlank(portRaw)) {
            return 0;
        }
        try {
            return Integer.parseInt(portRaw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port: " + portRaw, e);
        }
    }

    public String getHost() {
        return host;
    }


    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getToken() {
        return token;
    }


    public String resolvedHost() {
        return StringUtils.isNotBlank(host) ? host : "localhost";
    }


    public int resolvedPort(int defaultIfUnset) {
        return port > 0 ? port : defaultIfUnset;
    }
}