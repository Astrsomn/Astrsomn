package com.astrsomn.api.runtime.common.dto.vecsource;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import lombok.Data;

@Data
public class AiVecSourceResponseDTO extends AiVecSourceEntity {


    private String providerAvatar;


    private String extensionName;


    private String extensionType;
}