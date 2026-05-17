package com.astrsomn.api.vector.dto.vecsource;

import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import lombok.Data;

@Data
public class AiVecSourceResponseDTO extends AiVecSourceEntity {


    private String providerAvatar;


    private String extensionName;


    private String extensionType;
}