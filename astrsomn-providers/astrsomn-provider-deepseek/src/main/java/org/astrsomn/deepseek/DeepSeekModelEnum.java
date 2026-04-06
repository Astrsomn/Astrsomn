package org.astrsomn.deepseek;

import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.List;

@Getter
public enum DeepSeekModelEnum {

    // --- Chat Models (对话模型) ---
    DEEPSEEK_CHAT("deepseek-chat", "deepseek-chat", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    DEEPSEEK_CHAT_V2("deepseek-chat-v2", "DeepSeek Chat V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    DEEPSEEK_CHAT_V3("deepseek-chat-v3", "DeepSeek Chat V3", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE,
        AiModelEnum.ChatCapabilitiesEnum.NETWORK_SEARCH
    )),

    // --- Reasoning Models (推理模型) ---
    DEEPSEEK_R1("deepseek-reasoner", "DeepSeek R1 (Reasoning)",
                AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING,
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    DEEPSEEK_R1_PREVIEW("deepseek-reasoner-preview", "DeepSeek R1 Preview",
                        AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING,
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    DEEPSEEK_R1_LITE("deepseek-reasoner-lite", "DeepSeek R1 Lite",
                    AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING,
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING
    )),

    // --- Code Models (代码模型) ---
    DEEPSEEK_CODER("deepseek-coder", "DeepSeek Coder", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    DEEPSEEK_CODER_V2("deepseek-coder-v2", "DeepSeek Coder V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    DEEPSEEK_CODER_INSTRUCT("deepseek-coder-instruct", "DeepSeek Coder Instruct", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),

    // --- Math Models (数学模型) ---
    DEEPSEEK_MATH("deepseek-math", "DeepSeek Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING
    )),
    DEEPSEEK_MATH_V2("deepseek-math-v2", "DeepSeek Math V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING
    )),

    // --- Embedding Models (向量模型) ---
    DEEPSEEK_EMBEDDING("deepseek-embed", "DeepSeek Embedding v1",
                       AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),
    DEEPSEEK_EMBEDDING_V2("deepseek-embed-v2", "DeepSeek Embedding v2",
                         AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),
    DEEPSEEK_EMBEDDING_LITE("deepseek-embed-lite", "DeepSeek Embedding Lite",
                           AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING
    )),

    // --- Multimodal Models (多模态模型) ---
    DEEPSEEK_VL("deepseek-vl", "DeepSeek VL (Vision-Language)", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    DEEPSEEK_VL_CHAT("deepseek-vl-chat", "DeepSeek VL Chat", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),

    // --- Image Models (图像模型) ---
    DEEPSEEK_JANUS("deepseek-janus", "DeepSeek Janus (Multimodal)",
                   AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), List.of(
        AiModelEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_UNDERSTANDING,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_GENERATION
    )),
    DEEPSEEK_JANUS_PRO("deepseek-janus-pro", "DeepSeek Janus Pro",
                      AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), List.of(
        AiModelEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_UNDERSTANDING,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_GENERATION,
        AiModelEnum.ImageCapabilitiesEnum.IMAGE_EDITING
    )),

    // --- Specialized Models (专用模型) ---
    DEEPSEEK_MOE("deepseek-moe", "DeepSeek MoE", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    DEEPSEEK_LIGHT("deepseek-light", "DeepSeek Light", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING
    )),
    DEEPSEEK_TINY("deepseek-tiny", "DeepSeek Tiny", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING
    ));

    private final String modelKey;
    private final String modelName;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;

    DeepSeekModelEnum(String modelKey, String modelName, String modelType, List<? extends BaseEnum> capabilities) {
        this.modelKey = modelKey;
        this.modelName = modelName;
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
        entity.setCapabilities(this.getCapabilities());
        entity.setProvider(provider);
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        return entity;
    }


}