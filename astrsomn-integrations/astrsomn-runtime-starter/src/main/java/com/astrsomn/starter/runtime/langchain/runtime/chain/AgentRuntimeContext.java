package com.astrsomn.starter.runtime.langchain.runtime.chain;

import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import lombok.Getter;
import lombok.Setter;


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
