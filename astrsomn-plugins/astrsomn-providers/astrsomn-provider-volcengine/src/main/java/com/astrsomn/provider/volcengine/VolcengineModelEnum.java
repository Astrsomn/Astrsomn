package com.astrsomn.provider.volcengine;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.EnumUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.*;

@Getter
public enum VolcengineModelEnum {

    DOUBAO_1_5_PRO_32K("doubao-1-5-pro-32k", "Doubao 1.5 Pro 32K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    DOUBAO_1_5_PRO_256K("doubao-1-5-pro-256k", "Doubao 1.5 Pro 256K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    DOUBAO_1_5_PRO("doubao-1-5-pro", "Doubao 1.5 Pro", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    DOUBAO_1_5_LITE_32K("doubao-1-5-lite-32k", "Doubao 1.5 Lite 32K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    DOUBAO_1_5_LITE("doubao-1-5-lite", "Doubao 1.5 Lite", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    DOUBAO_PRO_32K("doubao-pro-32k", "Doubao Pro 32K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    DOUBAO_PRO_128K("doubao-pro-128k", "Doubao Pro 128K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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
    DOUBAO_LITE_32K("doubao-lite-32k", "Doubao Lite 32K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    DOUBAO_LITE_128K("doubao-lite-128k", "Doubao Lite 128K", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
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

    VolcengineModelEnum(String modelName, String description, String modelType,
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
        for (VolcengineModelEnum model : values()) {
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
