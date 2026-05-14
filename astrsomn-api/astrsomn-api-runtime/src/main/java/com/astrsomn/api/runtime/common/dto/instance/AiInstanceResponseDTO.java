package com.astrsomn.api.runtime.common.dto.instance;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;

@Data
public class AiInstanceResponseDTO extends AiInstanceEntity {

    
    private String providerAvatar;

    
    private String accountName;

    
    private String capabilities;
}