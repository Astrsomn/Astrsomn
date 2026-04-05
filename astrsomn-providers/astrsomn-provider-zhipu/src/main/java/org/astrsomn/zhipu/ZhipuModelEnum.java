package org.astrsomn.zhipu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;

/**
 * 智谱 GLM 占位清单；可按实际开放模型名扩展。
 */
@Getter
@AllArgsConstructor
public enum ZhipuModelEnum {

    GLM_4_FLASH("glm-4-flash", "GLM-4 Flash", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    GLM_4("glm-4", "GLM-4", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    GLM_4V("glm-4v", "GLM-4V", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\"]"),
    EMBEDDING_2("embedding-2", "Embedding-2", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\"]");

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final String capabilities;

    public AiModelEntity toEntity(String provider) {
        AiModelEntity entity = new AiModelEntity();
        entity.setModelKey(this.modelKey);
        entity.setModelName(this.modelName);
        entity.setModelType(this.modelType);
        entity.setProvider(provider);
        entity.setCapabilities(this.capabilities);
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        return entity;
    }
}
