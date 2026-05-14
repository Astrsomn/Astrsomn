package com.astrsomn.api.runtime.common.dto.agent;


import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiAgentQueryRequestDTO extends AiAgentEntity implements Serializable {

    private String name;
    private String status;


    private String extensionCode;

}