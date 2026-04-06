package org.astrsomn.qwen;

import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.List;

@Getter
public enum QwenModelEnum {

    // --- Chat Models (对话模型) ---
    QWEN_TURBO("qwen-turbo", "Qwen Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    QWEN_PLUS("qwen-plus", "Qwen Plus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),
    QWEN_MAX("qwen-max", "Qwen Max", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE,
        AiModelEnum.ChatCapabilitiesEnum.NETWORK_SEARCH
    )),
    QWEN_7B("qwen-7b", "Qwen 7B", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING
    )),
    QWEN_14B("qwen-14b", "Qwen 14B", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    QWEN_72B("qwen-72b", "Qwen 72B", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING,
        AiModelEnum.ChatCapabilitiesEnum.JSON_MODE
    )),

    // --- Code Models (代码模型) ---
    QWEN_CODE("qwen-code", "Qwen Code", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    QWEN_CODE_TURBO("qwen-code-turbo", "Qwen Code Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),

    // --- Multimodal Models (多模态模型) ---
    QWEN_VL("qwen-vl", "Qwen VL", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    QWEN_VL_PLUS("qwen-vl-plus", "Qwen VL Plus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.VISION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),

    // --- Embedding Models (向量模型) ---
    QWEN_EMBEDDING("qwen-embedding", "Qwen Embedding", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),
    QWEN_EMBEDDING_V2("qwen-embedding-v2", "Qwen Embedding V2", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), List.of(
        AiModelEnum.EmbeddingCapabilitiesEnum.TEXT_EMBEDDING,
        AiModelEnum.EmbeddingCapabilitiesEnum.SEMANTIC_SEARCH
    )),

    // --- Specialized Models (专用模型) ---
    QWEN_MATH("qwen-math", "Qwen Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING
    )),
    QWEN_MEDICAL("qwen-medical", "Qwen Medical", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    )),
    QWEN_FINANCE("qwen-finance", "Qwen Finance", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), List.of(
        AiModelEnum.ChatCapabilitiesEnum.TEXT_GENERATION,
        AiModelEnum.ChatCapabilitiesEnum.STREAMING,
        AiModelEnum.ChatCapabilitiesEnum.FUNCTION_CALLING
    ));

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;

    QwenModelEnum(String modelName, String modelKey, String modelType, List<? extends BaseEnum> capabilities) {
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