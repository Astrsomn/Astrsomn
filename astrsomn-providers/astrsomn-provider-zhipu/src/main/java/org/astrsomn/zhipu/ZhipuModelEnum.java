package org.astrsomn.zhipu;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.AiModelParamEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 智谱 GLM 占位清单；可按实际开放模型名扩展。
 */
@Getter
public enum ZhipuModelEnum {

    GLM_4_FLASH("glm-4-flash", "GLM-4 Flash", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    GLM_4("glm-4", "GLM-4", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    GLM_4V("glm-4v", "GLM-4V", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.VISION,
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),
    EMBEDDING_2("embedding-2", "Embedding-2", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
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

    ZhipuModelEnum(String modelName, String modelKey, String modelType, 
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

    /**
     * 转换为 AiModelEntity
     */
    public AiModelEntity toEntity(String provider) {
        AiModelEntity entity = new AiModelEntity();
        entity.setModelKey(this.modelKey);
        entity.setModelName(this.modelName);
        entity.setModelType(this.modelType);
        entity.setProvider(provider);
        entity.setCapabilities(this.getCapabilities());
        entity.setParams(this.getParams());
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
        return entity;
    }

    /**
     * 按 modelKey + paramCode 判断模型是否支持指定参数。
     */
    public static boolean isParamAvailable(String modelKey, String targetParamCode) {
        if (modelKey == null || modelKey.isBlank() || targetParamCode == null || targetParamCode.isBlank()) {
            return false;
        }
        for (ZhipuModelEnum model : values()) {
            if (!Objects.equals(model.getModelKey(), modelKey) && !Objects.equals(model.getModelName(), modelKey)) {
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
}
