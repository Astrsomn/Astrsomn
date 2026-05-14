package com.astrsomn.api.runtime.common.dto.instance;

import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiInstanceQueryRequestDTO extends AiInstanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    private String modelType;


    private String extensionCode;
}