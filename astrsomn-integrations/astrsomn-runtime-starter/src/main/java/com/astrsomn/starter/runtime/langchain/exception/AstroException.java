package com.astrsomn.starter.runtime.langchain.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


@Getter
public class AstroException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> context;

    public AstroException(ErrorCode errorCode) {
        super(buildMessage(errorCode, null, null));
        this.errorCode = errorCode;
        this.context = Collections.emptyMap();
    }

    public AstroException(ErrorCode errorCode, String detail) {
        super(buildMessage(errorCode, detail, null));
        this.errorCode = errorCode;
        this.context = Collections.emptyMap();
    }

    public AstroException(ErrorCode errorCode, String detail, Throwable cause) {
        super(buildMessage(errorCode, detail, null), cause);
        this.errorCode = errorCode;
        this.context = Collections.emptyMap();
    }

    public AstroException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(buildMessage(errorCode, detail, context));
        this.errorCode = errorCode;
        this.context = context != null ? Collections.unmodifiableMap(context) : Collections.emptyMap();
    }

    public AstroException(ErrorCode errorCode, String detail, Map<String, Object> context, Throwable cause) {
        super(buildMessage(errorCode, detail, context), cause);
        this.errorCode = errorCode;
        this.context = context != null ? Collections.unmodifiableMap(context) : Collections.emptyMap();
    }

    
    public Object getContextValue(String key) {
        return context.get(key);
    }

    private static String buildMessage(ErrorCode errorCode, String detail, Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("[E").append(errorCode.getCode()).append("] ");
        sb.append(errorCode.getDefaultMessage());
        if (detail != null && !detail.isBlank()) {
            sb.append(": ").append(detail);
        }
        if (context != null && !context.isEmpty()) {
            sb.append(" | context=").append(context);
        }
        return sb.toString();
    }

    // ────────────── context builder helpers ──────────────

    public static MapBuilder context() {
        return new MapBuilder();
    }

    public static class MapBuilder {
        private final Map<String, Object> map = new LinkedHashMap<>();

        public MapBuilder put(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return map;
        }
    }
}
