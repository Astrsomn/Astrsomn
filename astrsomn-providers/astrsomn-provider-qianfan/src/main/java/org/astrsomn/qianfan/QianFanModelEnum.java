package org.astrsomn.qianfan;

import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.List;

@Getter
public enum QianFanModelEnum {

    // --- Chat Models (对话模型) ---
    ERNIE_BOT("ernie-bot", "ERNIE Bot", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    ERNIE_BOT_TURBO("ernie-bot-turbo", "ERNIE Bot Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    ERNIE_BOT_4("ernie-bot-4", "ERNIE Bot 4.0", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE,
        AiModelEnum.ChatCapabilitiesEnum.NETWORK_SEARCH
    )),
    ERNIE_BOT_3_5("ernie-bot-3.5", "ERNIE Bot 3.5", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),

    // --- Code Models (代码模型) ---
    ERNIE_BOT_CODE("ernie-bot-code", "ERNIE Bot Code", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    ERNIE_BOT_CODE_TURBO("ernie-bot-code-turbo", "ERNIE Bot Code Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),

    // --- Multimodal Models (多模态模型) ---
    ERNIE_VISION("ernie-vision", "ERNIE Vision", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    ERNIE_VISION_PRO("ernie-vision-pro", "ERNIE Vision Pro", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),

    // --- Embedding Models (向量模型) ---
    BGE_LARGE_ZH("bge-large-zh", "BGE Large ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),
    BGE_BASE_ZH("bge-base-zh", "BGE Base ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),
    BGE_SMALL_ZH("bge-small-zh", "BGE Small ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING
    )),
    ERNIE_EMBEDDING_V1("ernie-embedding-v1", "ERNIE Embedding V1", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),

    // --- Image Models (图像模型) ---
    ERNIE_VILG("ernie-vilg", "ERNIE ViLG", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), List.of(
        AiModelEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_GENERATION
    )),
    ERNIE_VILG_V2("ernie-vilg-v2", "ERNIE ViLG V2", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), List.of(
        AiModelEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_GENERATION,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_EDITING
    )),

    // --- Specialized Models (专用模型) ---
    ERNIE_BOT_MATH("ernie-bot-math", "ERNIE Bot Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING
    )),
    ERNIE_BOT_MEDICAL("ernie-bot-medical", "ERNIE Bot Medical", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    ERNIE_BOT_FINANCE("ernie-bot-finance", "ERNIE Bot Finance", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    ));

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;

    QianFanModelEnum(String modelName, String modelKey, String modelType, List<? extends BaseEnum> capabilities) {
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

    public AiModelEntity toEntity(String provider){
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