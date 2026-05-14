package com.astrsomn.api.runtime.common.dto.model;

import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import lombok.Data;

@Data
public class AiModelResponseDTO extends AiModelEntity {


    private Boolean modelKeyImmutable;


    private String providerAvatar;
}