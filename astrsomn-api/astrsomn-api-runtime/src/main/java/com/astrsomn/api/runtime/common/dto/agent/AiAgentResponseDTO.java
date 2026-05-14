package com.astrsomn.api.runtime.common.dto.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;


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