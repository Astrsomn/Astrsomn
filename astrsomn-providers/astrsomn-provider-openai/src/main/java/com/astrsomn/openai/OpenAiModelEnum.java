package com.astrsomn.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import com.astrsomn.commn.base.BaseEnum;
import com.astrsomn.core.common.constant.AiModelEnum;
import com.astrsomn.core.common.constant.AiModelParamEnum;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.commn.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 占位模型清单，可按业务在枚举中增补；与库表 {@code AI_MODEL} 对齐时建议以库为准。
 */
@Getter
public enum OpenAiModelEnum {

    GPT_4O_MINI("gpt-4o-mini", "GPT-4o mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    GPT_4O("gpt-4o", "GPT-4o", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    TEXT_EMBEDDING_3_SMALL(
            "text-embedding-3-small",
            "text-embedding-3-small",
            AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    );

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;
    private final List<? extends BaseEnum> params;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    OpenAiModelEnum(String modelName, String modelKey, String modelType, 
                     List<? extends BaseEnum> capabilities, 
                     List<? extends BaseEnum> params) {
        this.modelName = modelName;
        this.modelKey = modelKey;
        this.modelType = modelType;
        this.capabilities = capabilities;
        this.params = params;
    }

    /**
     * 将 capabilities 转换为 JSON 字符串
     */
    public String getCapabilities() {
        return EnumUtils.toCapabilitiesJson(capabilities);
    }

    /**
     * 将 params 转换为 JSON 字符串（包含默认值配置）
     */
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
        entity.setModelKey(this.modelKey);
        entity.setModelName(this.modelName);
        entity.setModelType(this.modelType);
        entity.setExtensionCode(provider);
        entity.setCapabilities(this.getCapabilities());
        entity.setParams(this.getParams());
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
        return entity;
    }
}
