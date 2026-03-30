package org.astrsomn.core.common.dto.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.entity.AiAgentEntity;

/**
 * 列表/详情展示用：联表填充对话模型名称、实例展示名、提示词标题（库表仅存 instanceKey / promptKey）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiAgentResponseDTO extends AiAgentEntity {

    /** 来自 chat 实例关联的 AI_MODEL.MODEL_NAME */
    private String modelName;

    /** 来自 AI_INSTANCE.INSTANCE_NAME（对话实例） */
    private String chatInstanceName;

    /** 来自 AI_INSTANCE.INSTANCE_NAME（向量实例） */
    private String embeddingInstanceName;

    /** 来自 AI_INSTANCE.INSTANCE_NAME（图像实例） */
    private String imageInstanceName;

    /** 来自 AI_PROMPT.PROMPT_TITLE（当前环境下该 promptKey 的最大版本） */
    private String promptTitle;
}
