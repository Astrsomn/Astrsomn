package com.astrsomn.api.runtime.common.dto.model;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;

@Data
public class AiModelResponseDTO extends AiModelEntity {

    
    private Boolean modelKeyImmutable;

    
    private String providerAvatar;
}