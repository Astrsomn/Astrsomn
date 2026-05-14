package com.astrsomn.api.runtime.common.dto.agent;

import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class AiAgentResponseDTO extends AiAgentEntity {


    private String modelName;


    private String chatInstanceName;


    private String imageInstanceName;


    private String voiceInstanceName;


    private String promptTitle;


    private String modelProvider;


    private String providerAvatar;
}