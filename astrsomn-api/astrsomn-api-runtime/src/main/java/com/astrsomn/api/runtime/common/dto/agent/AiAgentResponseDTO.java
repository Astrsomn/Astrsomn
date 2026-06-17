package com.astrsomn.api.runtime.common.dto.agent;

import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class AiAgentResponseDTO extends AiAgentEntity {

    private AiPromptEntity prompt;

    private List<AiInstanceResponseDTO> instanceList;

    /**
     * 绑定的 key 中已被删除的资源，前端据此展示孤儿条目供用户手动清理
     */
    private List<String> orphanedToolKeys;
    private List<String> orphanedMcpKeys;
    private List<String> orphanedKnowledgeBaseKeys;
    private List<String> orphanedTemplateKeys;

    private String promptTitle;

    private String promptContent;

    private String modelName;

    private String providerAvatar;
}