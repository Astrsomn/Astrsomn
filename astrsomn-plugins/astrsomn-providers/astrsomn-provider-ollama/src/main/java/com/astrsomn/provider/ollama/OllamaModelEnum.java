package com.astrsomn.provider.ollama;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.EnumUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.*;

@Getter
public enum OllamaModelEnum {

    LLAMA3_1("llama3.1", "LLaMA 3.1", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    LLAMA3("llama3", "LLaMA 3", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    QWEN2_5("qwen2.5", "Qwen 2.5", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    MISTRAL("mistral", "Mistral", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GEMMA2("gemma2", "Gemma 2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    DEEPSEEK_R1("deepseek-r1", "DeepSeek R1", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    NOMIC_EMBED_TEXT("nomic-embed-text", "Nomic Embed Text", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                    AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    );

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String modelName;
    private final String description;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;
    private final List<? extends BaseEnum> params;

    OllamaModelEnum(String modelName, String description, String modelType,
                    List<? extends BaseEnum> capabilities,
                    List<? extends BaseEnum> params) {
        this.modelName = modelName;
        this.description = description;
        this.modelType = modelType;
        this.capabilities = capabilities;
        this.params = params;
    }

    public static boolean isParamAvailable(String modelKey, String targetParamCode) {
        if (modelKey == null || modelKey.isBlank() || targetParamCode == null || targetParamCode.isBlank()) {
            return false;
        }
        for (OllamaModelEnum model : values()) {
            if (!modelKey.equals(model.getModelName())) {
                continue;
            }
            for (BaseEnum modelParam : model.params) {
                if (Objects.equals(modelParam.getCode(), targetParamCode)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public String getModelKey() {
        return modelName;
    }

    public String getCapabilities() {
        return EnumUtils.toCapabilitiesJson(capabilities);
    }

    public String getParams() {
        try {
            List<Map<String, Object>> paramList = new ArrayList<>();
            for (BaseEnum param : params) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("id", param.getCode());
                paramMap.put("desc", param.getDesc());
                paramMap.put("mapping", param.getCode());
                paramMap.put("default", "");
                paramMap.put("range", "");
                paramMap.put("active", true);
                paramList.add(paramMap);
            }
            return OBJECT_MAPPER.writeValueAsString(paramList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert params to JSON", e);
        }
    }

    public AiModelEntity toEntity(String provider) {
        AiModelEntity entity = new AiModelEntity();
        entity.setModelKey(this.getModelKey());
        entity.setModelName(this.modelName);
        entity.setDescription(this.description);
        entity.setModelType(this.modelType);
        entity.setCapabilities(this.getCapabilities());
        entity.setParams(this.getParams());
        entity.setExtensionCode(provider);
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
        return entity;
    }
}
