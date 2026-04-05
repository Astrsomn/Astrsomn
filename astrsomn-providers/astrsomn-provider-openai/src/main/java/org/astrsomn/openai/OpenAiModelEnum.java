package org.astrsomn.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;

/**
 * 占位模型清单，可按业务在枚举中增补；与库表 {@code AI_MODEL} 对齐时建议以库为准。
 */
@Getter
@AllArgsConstructor
public enum OpenAiModelEnum {

    GPT_4O_MINI("gpt-4o-mini", "GPT-4o mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    GPT_4O("gpt-4o", "GPT-4o", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"vision\"]"),
    TEXT_EMBEDDING_3_SMALL(
            "text-embedding-3-small",
            "text-embedding-3-small",
            AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            "[\"text_embedding\"]");

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
