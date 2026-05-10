package com.astrsomn.api.runtime.common.dto.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;

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

    /** 来自 AI_INSTANCE.INSTANCE_NAME（图像实例） */
    private String imageInstanceName;

    /** 来自 AI_PROMPT.PROMPT_TITLE（当前环境下该 promptKey 的最大版本） */
    private String promptTitle;

    /** 对话模型所属厂商 code，来自 AI_MODEL.PROVIDER（与实例关联的 chat 模型） */
    private String modelProvider;

    /**
     * 与 modelProvider 匹配的系统扩展头像（SVG 等），来自 SYSTEM_EXTENSION：
     * provider_code 或 extension_key 与 PROVIDER 一致且同环境。
     */
    private String providerAvatar;
}
