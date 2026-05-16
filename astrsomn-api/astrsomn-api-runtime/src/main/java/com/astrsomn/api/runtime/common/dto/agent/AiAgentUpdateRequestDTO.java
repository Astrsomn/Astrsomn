package com.astrsomn.api.runtime.common.dto.agent;

import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import lombok.Data;

import java.util.List;

@Data
public class AiAgentUpdateRequestDTO extends AiAgentEntity {

    List<AiInstanceCreateRequestDTO> instanceList;

    private String promptContent;
}