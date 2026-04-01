package org.astrsomn.qianfan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;

@Getter
@AllArgsConstructor
public enum QianFanModelEnum {

    // --- Chat Models (对话模型) ---
    ERNIE_BOT("ernie-bot", "ERNIE Bot", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    ERNIE_BOT_TURBO("ernie-bot-turbo", "ERNIE Bot Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    ERNIE_BOT_4("ernie-bot-4", "ERNIE Bot 4.0", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\", \"network_search\"]"),
    ERNIE_BOT_3_5("ernie-bot-3.5", "ERNIE Bot 3.5", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),

    // --- Code Models (代码模型) ---
    ERNIE_BOT_CODE("ernie-bot-code", "ERNIE Bot Code", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    ERNIE_BOT_CODE_TURBO("ernie-bot-code-turbo", "ERNIE Bot Code Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),

    // --- Multimodal Models (多模态模型) ---
    ERNIE_VISION("ernie-vision", "ERNIE Vision", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\", \"function_calling\"]"),
    ERNIE_VISION_PRO("ernie-vision-pro", "ERNIE Vision Pro", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\", \"function_calling\"]"),

    // --- Embedding Models (向量模型) ---
    BGE_LARGE_ZH("bge-large-zh", "BGE Large ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),
    BGE_BASE_ZH("bge-base-zh", "BGE Base ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),
    BGE_SMALL_ZH("bge-small-zh", "BGE Small ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\"]"),
    ERNIE_EMBEDDING_V1("ernie-embedding-v1", "ERNIE Embedding V1", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),

    // --- Image Models (图像模型) ---
    ERNIE_VILG("ernie-vilg", "ERNIE ViLG", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), "[\"text_to_image\", \"image_generation\"]"),
    ERNIE_VILG_V2("ernie-vilg-v2", "ERNIE ViLG V2", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), "[\"text_to_image\", \"image_generation\", \"image_editing\"]"),

    // --- Specialized Models (专用模型) ---
    ERNIE_BOT_MATH("ernie-bot-math", "ERNIE Bot Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"deep_reasoning\"]"),
    ERNIE_BOT_MEDICAL("ernie-bot-medical", "ERNIE Bot Medical", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    ERNIE_BOT_FINANCE("ernie-bot-finance", "ERNIE Bot Finance", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]");

    private final String modelKey;
    private final String modelName;
    private final String modelType;
    private final String capabilities;

    public AiModelEntity toEntity(String provider){
        AiModelEntity entity = new AiModelEntity();
        entity.setModelKey(this.modelKey);
        entity.setModelName(this.modelName);
        entity.setModelType(this.modelType);
        entity.setProvider(provider);
        entity.setStatus(AiModelEnum.StatusEnum.ENABLED.getCode());
        return entity;
    }

}