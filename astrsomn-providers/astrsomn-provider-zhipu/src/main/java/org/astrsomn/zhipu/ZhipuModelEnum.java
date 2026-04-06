package org.astrsomn.zhipu;

import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.List;

/**
 * 智谱 GLM 占位清单；可按实际开放模型名扩展。
 */
@Getter
public enum ZhipuModelEnum {

    GLM_4_FLASH("glm-4-flash", "GLM-4 Flash", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    GLM_4("glm-4", "GLM-4", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    GLM_4V("glm-4v", "GLM-4V", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING
    )),
    EMBEDDING_2("embedding-2", "Embedding-2", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING
    ));

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;

    ZhipuModelEnum(String modelName, String modelKey, String modelType, List<? extends BaseEnum> capabilities) {
        this.modelName = modelName;
        this.modelKey = modelKey;
        this.modelType = modelType;
        this.capabilities = capabilities;
    }

    /**
     * 将 capabilities 转换为 JSON 字符串
     */
    public String getCapabilities() {
        return EnumUtils.toCapabilitiesJson(capabilities);
    }

    /**
     * 转换为 AiModelEntity
     */
    public AiModelEntity toEntity(String provider) {
        AiModelEntity entity = new AiModelEntity();
        entity.setModelKey(this.modelKey);
        entity.setModelName(this.modelName);
        entity.setModelType(this.modelType);
        entity.setProvider(provider);
        entity.setCapabilities(this.getCapabilities());
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        return entity;
    }
}
