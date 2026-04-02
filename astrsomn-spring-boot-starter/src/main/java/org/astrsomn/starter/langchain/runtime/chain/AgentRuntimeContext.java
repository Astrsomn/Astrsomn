package org.astrsomn.starter.langchain.runtime.chain;

import lombok.Getter;
import lombok.Setter;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.config.AstrsomnProperties;

/**
 * 单次解析过程中的可变上下文：贯穿整条责任链，供各节点写入查询结果。
 */
@Getter
public class AgentRuntimeContext {

    private final AstroChatParam<?> param;
    private final AstrsomnProperties properties;

    @Setter
    private String envCode;

    @Setter
    private AiAgentEntity agent;

    @Setter
    private AiInstanceEntity instance;

    @Setter
    private AiModelEntity model;

    @Setter
    private AiAccountEntity account;

    public AgentRuntimeContext(AstroChatParam<?> param, AstrsomnProperties properties) {
        this.param = param;
        this.properties = properties;
    }
}
