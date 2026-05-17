package com.astrsomn.api.runtime.common.dto.instance;

import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import lombok.Data;

@Data
public class AiInstanceResponseDTO extends AiInstanceEntity {


    private String providerAvatar;


    private String accountName;


    private String capabilities;
}