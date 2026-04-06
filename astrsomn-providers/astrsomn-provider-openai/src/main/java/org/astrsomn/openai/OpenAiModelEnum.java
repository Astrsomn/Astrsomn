package org.astrsomn.openai;

import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.List;

/**
 * 占位模型清单，可按业务在枚举中增补；与库表 {@code AI_MODEL} 对齐时建议以库为准。
 */
@Getter
public enum OpenAiModelEnum {

    GPT_4O_MINI("gpt-4o-mini", "GPT-4o mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    GPT_4O("gpt-4o", "GPT-4o", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.VISION
    )),
    TEXT_EMBEDDING_3_SMALL(
            "text-embedding-3-small",
            "text-embedding-3-small",
            AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING
            ));

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;

    OpenAiModelEnum(String modelName, String modelKey, String modelType, List<? extends BaseEnum> capabilities) {
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
