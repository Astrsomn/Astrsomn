package org.astrsomn.server.service.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 非 Model 资源 key 前缀，避免与 modelKey 及其它表语义混淆。
 */
@Getter
@RequiredArgsConstructor
public enum BizKeyNamespace {
    MODEL("MODEL", "MD"),
    AGENT("AGENT", "AG"),
    PROMPT("PROMPT", "PT"),
    TOOL("TOOL", "TL"),
    MCP("MCP", "MC"),
    INSTANCE("INSTANCE", "IN"),
    ACCOUNT("ACCOUNT", "AC");

    private final String prefix;
    private final String shortCode;
}
