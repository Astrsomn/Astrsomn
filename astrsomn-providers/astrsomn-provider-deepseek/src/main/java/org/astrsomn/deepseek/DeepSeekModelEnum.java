package org.astrsomn.deepseek;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;

@Getter
@AllArgsConstructor
public enum DeepSeekModelEnum {

    // --- Chat Models (对话模型) ---
    DEEPSEEK_CHAT("deepseek-chat", "deepseek-chat", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    DEEPSEEK_CHAT_V2("deepseek-chat-v2", "DeepSeek Chat V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    DEEPSEEK_CHAT_V3("deepseek-chat-v3", "DeepSeek Chat V3", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\", \"network_search\"]"),

    // --- Reasoning Models (推理模型) ---
    DEEPSEEK_R1("deepseek-reasoner", "DeepSeek R1 (Reasoning)",
                AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"deep_reasoning\", \"chat\", \"streaming\", \"function_calling\"]"),
    DEEPSEEK_R1_PREVIEW("deepseek-reasoner-preview", "DeepSeek R1 Preview",
                        AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"deep_reasoning\", \"chat\", \"streaming\", \"function_calling\"]"),
    DEEPSEEK_R1_LITE("deepseek-reasoner-lite", "DeepSeek R1 Lite",
                    AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"deep_reasoning\", \"chat\", \"streaming\"]"),

    // --- Code Models (代码模型) ---
    DEEPSEEK_CODER("deepseek-coder", "DeepSeek Coder", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    DEEPSEEK_CODER_V2("deepseek-coder-v2", "DeepSeek Coder V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    DEEPSEEK_CODER_INSTRUCT("deepseek-coder-instruct", "DeepSeek Coder Instruct", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),

    // --- Math Models (数学模型) ---
    DEEPSEEK_MATH("deepseek-math", "DeepSeek Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"deep_reasoning\"]"),
    DEEPSEEK_MATH_V2("deepseek-math-v2", "DeepSeek Math V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"deep_reasoning\"]"),

    // --- Embedding Models (向量模型) ---
    DEEPSEEK_EMBEDDING("deepseek-embed", "DeepSeek Embedding v1",
                       AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),
    DEEPSEEK_EMBEDDING_V2("deepseek-embed-v2", "DeepSeek Embedding v2",
                         AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),
    DEEPSEEK_EMBEDDING_LITE("deepseek-embed-lite", "DeepSeek Embedding Lite",
                           AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\"]"),

    // --- Multimodal Models (多模态模型) ---
    DEEPSEEK_VL("deepseek-vl", "DeepSeek VL (Vision-Language)", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\", \"function_calling\"]"),
    DEEPSEEK_VL_CHAT("deepseek-vl-chat", "DeepSeek VL Chat", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\", \"function_calling\"]"),

    // --- Image Models (图像模型) ---
    DEEPSEEK_JANUS("deepseek-janus", "DeepSeek Janus (Multimodal)",
                   AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), "[\"text_to_image\", \"image_understanding\", \"image_generation\"]"),
    DEEPSEEK_JANUS_PRO("deepseek-janus-pro", "DeepSeek Janus Pro",
                      AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), "[\"text_to_image\", \"image_understanding\", \"image_generation\", \"image_editing\"]"),

    // --- Specialized Models (专用模型) ---
    DEEPSEEK_MOE("deepseek-moe", "DeepSeek MoE", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    DEEPSEEK_LIGHT("deepseek-light", "DeepSeek Light", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\"]"),
    DEEPSEEK_TINY("deepseek-tiny", "DeepSeek Tiny", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\"]");

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