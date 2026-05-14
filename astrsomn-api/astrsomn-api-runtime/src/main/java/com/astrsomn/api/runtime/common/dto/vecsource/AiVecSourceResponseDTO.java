package com.astrsomn.api.runtime.common.dto.vecsource;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;

@Data
public class AiVecSourceResponseDTO extends AiVecSourceEntity {

    
    private String providerAvatar;

    
    private String extensionName;

    
    private String extensionType;
}