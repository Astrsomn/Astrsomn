package org.astrsomn.qwen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;

@Getter
@AllArgsConstructor
public enum QwenModelEnum {

    // --- Chat Models (对话模型) ---
    QWEN_TURBO("qwen-turbo", "Qwen Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    QWEN_PLUS("qwen-plus", "Qwen Plus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),
    QWEN_MAX("qwen-max", "Qwen Max", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\", \"network_search\"]"),
    QWEN_7B("qwen-7b", "Qwen 7B", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\"]"),
    QWEN_14B("qwen-14b", "Qwen 14B", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    QWEN_72B("qwen-72b", "Qwen 72B", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\", \"json_mode\"]"),

    // --- Code Models (代码模型) ---
    QWEN_CODE("qwen-code", "Qwen Code", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    QWEN_CODE_TURBO("qwen-code-turbo", "Qwen Code Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),

    // --- Multimodal Models (多模态模型) ---
    QWEN_VL("qwen-vl", "Qwen VL", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\", \"function_calling\"]"),
    QWEN_VL_PLUS("qwen-vl-plus", "Qwen VL Plus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"vision\", \"streaming\", \"function_calling\"]"),

    // --- Embedding Models (向量模型) ---
    QWEN_EMBEDDING("qwen-embedding", "Qwen Embedding", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),
    QWEN_EMBEDDING_V2("qwen-embedding-v2", "Qwen Embedding V2", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), "[\"text_embedding\", \"semantic_search\"]"),

    // --- Specialized Models (专用模型) ---
    QWEN_MATH("qwen-math", "Qwen Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"deep_reasoning\"]"),
    QWEN_MEDICAL("qwen-medical", "Qwen Medical", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]"),
    QWEN_FINANCE("qwen-finance", "Qwen Finance", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), "[\"text_generation\", \"streaming\", \"function_calling\"]");

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