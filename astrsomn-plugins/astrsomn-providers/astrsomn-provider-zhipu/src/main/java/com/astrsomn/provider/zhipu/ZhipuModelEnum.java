package com.astrsomn.provider.zhipu;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.EnumUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.*;


@Getter
public enum ZhipuModelEnum {


    GLM_4_PLUS("glm-4-plus", "GLM-4 Plus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    GLM_4("glm-4", "GLM-4", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_4_AIR("glm-4-air", "GLM-4 Air", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    GLM_4_AIRX("glm-4-airx", "GLM-4 AirX", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    GLM_4_LONG("glm-4-long", "GLM-4 Long", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_4_FLASH("glm-4-flash", "GLM-4 Flash", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_4_FLASHX("glm-4-flashx", "GLM-4 FlashX", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_4_ALLTOOLS("glm-4-alltools", "GLM-4 AllTools", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),


    GLM_4V_PLUS("glm-4v-plus", "GLM-4V Plus", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_4V("glm-4v", "GLM-4V", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_4V_FLASH("glm-4v-flash", "GLM-4V Flash", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),


    GLM_Z1_AIRX("glm-z1-airx", "GLM-Z1 AirX", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_Z1_AIR("glm-z1-air", "GLM-Z1 Air", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_Z1_FLASH("glm-z1-flash", "GLM-Z1 Flash", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    GLM_Z1_FLASHX("glm-z1-flashx", "GLM-Z1 FlashX", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),


    CODEGEEX_4("codegeex-4", "CodeGeeX-4", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.TOP_K,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),


    EMBEDDING_3("embedding-3", "Embedding-3", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                    AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    ),
    EMBEDDING_2("embedding-2", "Embedding-2", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                    AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    ),


    COGVIEW_3_PLUS("cogview-3-plus", "CogView-3 Plus", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE
            ),
            List.of(
                    AiModelParamEnum.ImageParamEnum.SIZE,
                    AiModelParamEnum.ImageParamEnum.QUALITY
            )
    ),
    COGVIEW_3("cogview-3", "CogView-3", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE
            ),
            List.of(
                    AiModelParamEnum.ImageParamEnum.SIZE
            )
    ),
    COGVIEW_4("cogview-4", "CogView-4", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
                    AiModelParamEnum.ImageCapabilitiesEnum.IMAGE_EDITING
            ),
            List.of(
                    AiModelParamEnum.ImageParamEnum.SIZE,
                    AiModelParamEnum.ImageParamEnum.QUALITY,
                    AiModelParamEnum.ImageParamEnum.RESPONSE_FORMAT
            )
    );

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String modelName;
    private final String description;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;
    private final List<? extends BaseEnum> params;

    ZhipuModelEnum(String modelName, String description, String modelType,
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
        for (ZhipuModelEnum model : values()) {
            if (!Objects.equals(model.getModelName(), modelKey)) {
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
        entity.setExtensionCode(provider);
        entity.setCapabilities(this.getCapabilities());
        entity.setParams(this.getParams());
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
        return entity;
    }
}
