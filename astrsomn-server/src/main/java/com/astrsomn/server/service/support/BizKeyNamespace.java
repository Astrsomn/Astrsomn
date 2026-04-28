package com.astrsomn.server.service.support;

import com.astrsomn.core.common.entity.*;
import com.astrsomn.starter.mapper.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 业务资源 key 前缀枚举，定义资源类型、短代码、对应的实体类、Mapper 和字段名。
 */
@Getter
@RequiredArgsConstructor
public enum BizKeyNamespace {
    MODEL("MODEL", "MD", AiModelEntity.class, AiModelMapper.class, "modelKey"),
    AGENT("AGENT", "AG", AiAgentEntity.class, AiAgentMapper.class, "agentKey"),
    PROMPT("PROMPT", "PT", AiPromptEntity.class, AiPromptMapper.class, "promptKey"),
    TOOL("TOOL", "TL", AiToolEntity.class, AiToolMapper.class, "toolKey"),
    MCP("MCP", "MC", AiMcpEntity.class, AiMcpMapper.class, "mcpKey"),
    INSTANCE("INSTANCE", "IN", AiInstanceEntity.class, AiInstanceMapper.class, "instanceKey"),
    ACCOUNT("ACCOUNT", "AC", AiAccountEntity.class, AiAccountMapper.class, "accountKey");

    private final String prefix;
    private final String shortCode;
    private final Class<?> entityClass;
    private final Class<?> mapperClass;
    private final String keyFieldName;
}
