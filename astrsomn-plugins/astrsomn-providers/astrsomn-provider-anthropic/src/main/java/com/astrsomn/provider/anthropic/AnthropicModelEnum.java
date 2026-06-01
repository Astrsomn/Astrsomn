package com.astrsomn.provider.anthropic;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.EnumUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.*;

@Getter
public enum AnthropicModelEnum {

    CLAUDE_4_OPUS("claude-4-opus-20250514", "Claude 4 Opus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    CLAUDE_4_SONNET("claude-4-sonnet-20250514", "Claude 4 Sonnet", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    CLAUDE_3_7_SONNET("claude-3-7-sonnet-20250219", "Claude 3.7 Sonnet", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    CLAUDE_3_5_SONNET("claude-3-5-sonnet-20241022", "Claude 3.5 Sonnet", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    CLAUDE_3_5_HAIKU("claude-3-5-haiku-20241022", "Claude 3.5 Haiku", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    CLAUDE_3_OPUS("claude-3-opus-20240229", "Claude 3 Opus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    CLAUDE_3_SONNET("claude-3-sonnet-20240229", "Claude 3 Sonnet", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    CLAUDE_3_HAIKU("claude-3-haiku-20240307", "Claude 3 Haiku", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    );

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String modelName;
    private final String description;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;
    private final List<? extends BaseEnum> params;

    AnthropicModelEnum(String modelName, String description, String modelType,
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
        for (AnthropicModelEnum model : values()) {
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
