package com.astrsomn.api.runtime.common.dto.agent;

import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import lombok.Data;

import java.util.List;

@Data
public class AiAgentCreateRequestDTO extends AiAgentEntity {

    List<AiInstanceCreateRequestDTO> instanceList;

    private AiPromptEntity promptEntity;

}