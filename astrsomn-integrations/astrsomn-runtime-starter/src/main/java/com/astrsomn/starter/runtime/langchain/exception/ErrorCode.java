package com.astrsomn.starter.runtime.langchain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ────────────── 1xxx: 配置类 ──────────────

    AGENT_KEY_REQUIRED(1000, "Agent key is required"),
    AGENT_NOT_FOUND(1001, "Agent not found"),
    INSTANCE_KEY_MISSING(1002, "Instance key not resolved"),
    INSTANCE_NOT_FOUND(1003, "Instance not found"),
    MODEL_KEY_MISSING(1004, "Model key not resolved"),
    MODEL_NOT_FOUND(1005, "Model not found"),
    ACCOUNT_NOT_FOUND(1006, "Account not found"),
    DATASOURCE_URL_REQUIRED(1007, "Datasource URL is required"),
    INVALID_ROUTE_CONFIG(1008, "Invalid route configuration"),

    // ────────────── 2xxx: 模型调用类 ──────────────

    MODEL_PROVIDER_NOT_FOUND(2000, "Model provider not found"),
    MODEL_CALL_FAILED(2001, "Model API call failed"),
    CONTENT_MODERATION_BLOCKED(2002, "Content blocked by moderation"),

    // ────────────── 3xxx: 路由类 ──────────────

    NO_AVAILABLE_ENDPOINT(3000, "No available endpoint for routing"),
    ALL_ENDPOINTS_FAILED(3001, "All endpoints failed after failover attempts"),

    // ────────────── 4xxx: 工具类 ──────────────

    MCP_CONNECTION_FAILED(4000, "MCP connection failed"),
    MCP_SSE_URL_EMPTY(4001, "MCP SSE URL must not be empty"),
    TOOL_EXECUTION_FAILED(4002, "Tool execution failed"),
    VECTOR_STORE_NOT_FOUND(4003, "Vector store not found");

    private final int code;
    private final String defaultMessage;
}
