package org.astrsomn.core.common.dto.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.entity.AiAgentEntity;

/**
 * 列表/详情展示用：联表填充模型名称、提示词标题（库表仅存 modelKey / promptKey）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiAgentResponseDTO extends AiAgentEntity {

    /** 来自 AI_MODEL.MODEL_NAME */
    private String modelName;

    /** 来自 AI_PROMPT.PROMPT_TITLE（当前环境下该 promptKey 的最大版本） */
    private String promptTitle;
}
