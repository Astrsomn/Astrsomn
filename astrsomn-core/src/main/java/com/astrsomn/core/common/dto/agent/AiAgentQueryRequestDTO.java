package com.astrsomn.core.common.dto.agent;


import lombok.Data;
import com.astrsomn.core.common.entity.AiAgentEntity;

import java.io.Serializable;

@Data
public class AiAgentQueryRequestDTO extends AiAgentEntity implements Serializable {

    private String name;
    private String status;

}
