package org.astrsomn.deepseek;

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

@Getter
public enum DeepSeekModelEnum {

    // --- Chat Models (对话模型) ---
    DEEPSEEK_CHAT("deepseek-chat", "deepseek-chat", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_CHAT_V2("deepseek-chat-v2", "DeepSeek Chat V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_CHAT_V3("deepseek-chat-v3", "DeepSeek Chat V3", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    // --- Reasoning Models (推理模型) ---
    DEEPSEEK_R1("deepseek-reasoner", "DeepSeek R1 (Reasoning)",
                AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING,
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
            AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),
    DEEPSEEK_R1_PREVIEW("deepseek-reasoner-preview", "DeepSeek R1 Preview",
                        AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING,
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
            AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),
    DEEPSEEK_R1_LITE("deepseek-reasoner-lite", "DeepSeek R1 Lite",
                    AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING,
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),

    // --- Code Models (代码模型) ---
    DEEPSEEK_CODER("deepseek-coder", "DeepSeek Coder", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_CODER_V2("deepseek-coder-v2", "DeepSeek Coder V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_CODER_INSTRUCT("deepseek-coder-instruct", "DeepSeek Coder Instruct", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    // --- Math Models (数学模型) ---
    DEEPSEEK_MATH("deepseek-math", "DeepSeek Math", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_MATH_V2("deepseek-math-v2", "DeepSeek Math V2", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    // --- Embedding Models (向量模型) ---
    DEEPSEEK_EMBEDDING("deepseek-embed", "DeepSeek Embedding v1",
                       AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),
    DEEPSEEK_EMBEDDING_V2("deepseek-embed-v2", "DeepSeek Embedding v2",
                         AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),
    DEEPSEEK_EMBEDDING_LITE("deepseek-embed-lite", "DeepSeek Embedding Lite",
                           AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
        ),
        List.of(
            AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
        )
    ),

    // --- Multimodal Models (多模态模型) ---
    DEEPSEEK_VL("deepseek-vl", "DeepSeek VL (Vision-Language)", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_VL_CHAT("deepseek-vl-chat", "DeepSeek VL Chat", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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

    // --- Image Models (图像模型) ---
    DEEPSEEK_JANUS("deepseek-janus", "DeepSeek Janus (Multimodal)",
                   AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE
        ),
        List.of(
            AiModelParamEnum.ImageParamEnum.SIZE,
            AiModelParamEnum.ImageParamEnum.QUALITY,
            AiModelParamEnum.ImageParamEnum.STYLE
        )
    ),
    DEEPSEEK_JANUS_PRO("deepseek-janus-pro", "DeepSeek Janus Pro",
                      AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(), 
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
    DEEPSEEK_MOE("deepseek-moe", "DeepSeek MoE", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
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
    DEEPSEEK_LIGHT("deepseek-light", "DeepSeek Light", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
        ),
        List.of(
            AiModelParamEnum.ChatParamEnum.TEMPERATURE,
            AiModelParamEnum.ChatParamEnum.TOP_P,
            AiModelParamEnum.ChatParamEnum.MAX_TOKENS
        )
    ),
    DEEPSEEK_TINY("deepseek-tiny", "DeepSeek Tiny", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(), 
        List.of(
            AiModelParamEnum.ChatCapabilitiesEnum.STREAMING
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

    DeepSeekModelEnum(String modelKey, String modelName, String modelType, 
                     List<? extends BaseEnum> capabilities, 
                     List<? extends BaseEnum> params) {
        this.modelKey = modelKey;
        this.modelName = modelName;
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