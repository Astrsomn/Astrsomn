package com.astrsomn.qianfan;

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

@Getter
public enum QianFanModelEnum {

    // --- Chat Models (对话模型) ---
    ERNIE_BOT("ernie-bot", "ERNIE Bot", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    ERNIE_BOT_TURBO("ernie-bot-turbo", "ERNIE Bot Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    ERNIE_BOT_4("ernie-bot-4", "ERNIE Bot 4.0", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    ERNIE_BOT_3_5("ernie-bot-3.5", "ERNIE Bot 3.5", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    // --- Code Models (代码模型) ---
    ERNIE_BOT_CODE("ernie-bot-code", "ERNIE Bot Code", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    ERNIE_BOT_CODE_TURBO("ernie-bot-code-turbo", "ERNIE Bot Code Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    // --- Multimodal Models (多模态模型) ---
    ERNIE_VISION("ernie-vision", "ERNIE Vision", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.VISION,
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
            AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),
    ERNIE_VISION_PRO("ernie-vision-pro", "ERNIE Vision Pro", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.VISION,
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
            AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),

    // --- Embedding Models (向量模型) ---
    BGE_LARGE_ZH("bge-large-zh", "BGE Large ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),
    BGE_BASE_ZH("bge-base-zh", "BGE Base ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),
    BGE_SMALL_ZH("bge-small-zh", "BGE Small ZH", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),
    ERNIE_EMBEDDING_V1("ernie-embedding-v1", "ERNIE Embedding V1", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),

    // --- Image Models (图像模型) ---
    ERNIE_VILG("ernie-vilg", "ERNIE ViLG", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE
        ),
        List.of(
            AiModelParamEnum.ImageParamEnum.SIZE,
            AiModelParamEnum.ImageParamEnum.QUALITY,
            AiModelParamEnum.ImageParamEnum.STYLE
        )
    ),
    ERNIE_VILG_V2("ernie-vilg-v2", "ERNIE ViLG V2", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
            AiModelParamEnum.ImageCapabilitiesEnum.IMAGE_EDITING
        ),
        List.of(
            AiModelParamEnum.ImageParamEnum.SIZE,
            AiModelParamEnum.ImageParamEnum.QUALITY,
            AiModelParamEnum.ImageParamEnum.STYLE
        )
    ),

    // --- Specialized Models (专用模型) ---
    ERNIE_BOT_MATH("ernie-bot-math", "ERNIE Bot Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
            AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),
    ERNIE_BOT_MEDICAL("ernie-bot-medical", "ERNIE Bot Medical", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    ERNIE_BOT_FINANCE("ernie-bot-finance", "ERNIE Bot Finance", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    private final String modelName;
    private final String modelKey;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;
    private final List<? extends BaseEnum> params;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    QianFanModelEnum(String modelName, String modelKey, String modelType, 
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

    public AiModelEntity toEntity(String provider){
        AiModelEntity entity = new AiModelEntity();
        entity.setModelKey(this.modelKey);
        entity.setModelName(this.modelName);
        entity.setModelType(this.modelType);
        entity.setCapabilities(this.getCapabilities());
        entity.setParams(this.getParams());
        entity.setProvider(provider);
        entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
        entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
        return entity;
    }

}
