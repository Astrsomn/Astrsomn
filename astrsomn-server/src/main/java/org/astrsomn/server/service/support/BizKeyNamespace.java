package org.astrsomn.server.service.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 非 Model 资源 key 前缀，避免与 modelKey 及其它表语义混淆。
 */
@Getter
@RequiredArgsConstructor
public enum BizKeyNamespace {
    MODEL("MODEL"),
    AGENT("AGENT"),
    PROMPT("PROMPT"),
    TOOL("TOOL"),
    MCP("MCP"),
    INSTANCE("INSTANCE"),
    ACCOUNT("ACCOUNT");

    private final String prefix;
}
