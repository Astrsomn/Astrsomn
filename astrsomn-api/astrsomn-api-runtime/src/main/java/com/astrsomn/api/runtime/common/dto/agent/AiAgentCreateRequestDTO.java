package com.astrsomn.api.runtime.common.dto.agent;

import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import lombok.Data;

import java.util.List;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;

@Data
public class AiAgentCreateRequestDTO extends AiAgentEntity {

    List<AiInstanceCreateRequestDTO> instanceList;

    private String promptContent;



}