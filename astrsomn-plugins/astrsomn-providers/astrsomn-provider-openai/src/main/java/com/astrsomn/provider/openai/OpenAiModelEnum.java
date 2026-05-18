package com.astrsomn.provider.openai;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.EnumUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public enum OpenAiModelEnum {


    GPT_4_1("gpt-4.1", "GPT-4.1", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),
    GPT_4_1_MINI("gpt-4.1-mini", "GPT-4.1 mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),
    GPT_4_1_NANO("gpt-4.1-nano", "GPT-4.1 nano", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
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
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),
    GPT_4O_MINI("gpt-4o-mini", "GPT-4o mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),
    CHATGPT_4O_LATEST("chatgpt-4o-latest", "ChatGPT-4o Latest", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
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


    GPT_4_TURBO("gpt-4-turbo", "GPT-4 Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),
    GPT_4("gpt-4", "GPT-4", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),


    GPT_3_5_TURBO("gpt-3.5-turbo", "GPT-3.5 Turbo", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.TEMPERATURE,
                    AiModelParamEnum.ChatParamEnum.TOP_P,
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY,
                    AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY
            )
    ),


    O1("o1", "O1", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    O1_MINI("o1-mini", "o1-mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS
            )
    ),
    O1_PRO("o1-pro", "o1-pro", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    O3("o3", "o3", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    O3_MINI("o3-mini", "o3-mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),
    O4_MINI("o4-mini", "o4-mini", AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ChatCapabilitiesEnum.STREAMING,
                    AiModelParamEnum.ChatCapabilitiesEnum.TOOLS,
                    AiModelParamEnum.ChatCapabilitiesEnum.VISION,
                    AiModelParamEnum.ChatCapabilitiesEnum.JSON_MODE,
                    AiModelParamEnum.ChatCapabilitiesEnum.DEEP_REASONING
            ),
            List.of(
                    AiModelParamEnum.ChatParamEnum.MAX_TOKENS,
                    AiModelParamEnum.ChatParamEnum.SEED
            )
    ),


    TEXT_EMBEDDING_3_LARGE("text-embedding-3-large", "text-embedding-3-large", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                    AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    ),
    TEXT_EMBEDDING_3_SMALL("text-embedding-3-small", "text-embedding-3-small", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                    AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    ),
    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002", "text-embedding-ada-002", AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.EmbeddingCapabilityEnum.TEXT_EMBEDDING
            ),
            List.of(
                    AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS
            )
    ),


    GPT_IMAGE_1("gpt-image-1", "GPT Image 1", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
                    AiModelParamEnum.ImageCapabilitiesEnum.IMAGE_EDITING
            ),
            List.of(
                    AiModelParamEnum.ImageParamEnum.SIZE,
                    AiModelParamEnum.ImageParamEnum.QUALITY
            )
    ),
    DALL_E_3("dall-e-3", "DALL-E 3", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE
            ),
            List.of(
                    AiModelParamEnum.ImageParamEnum.SIZE,
                    AiModelParamEnum.ImageParamEnum.QUALITY,
                    AiModelParamEnum.ImageParamEnum.STYLE,
                    AiModelParamEnum.ImageParamEnum.RESPONSE_FORMAT
            )
    ),
    DALL_E_2("dall-e-2", "DALL-E 2", AiModelEnum.ModelTypeEnum.IMAGE_MODEL.getCode(),
            List.of(
                    AiModelParamEnum.ImageCapabilitiesEnum.TEXT_TO_IMAGE,
                    AiModelParamEnum.ImageCapabilitiesEnum.IMAGE_EDITING
            ),
            List.of(
                    AiModelParamEnum.ImageParamEnum.SIZE,
                    AiModelParamEnum.ImageParamEnum.RESPONSE_FORMAT
            )
    );

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String modelName;
    private final String description;
    private final String modelType;
    private final List<? extends BaseEnum> capabilities;
    private final List<? extends BaseEnum> params;

    OpenAiModelEnum(String modelName, String description, String modelType,
                    List<? extends BaseEnum> capabilities,
                    List<? extends BaseEnum> params) {
        this.modelName = modelName;
        this.description = description;
        this.modelType = modelType;
        this.capabilities = capabilities;
        this.params = params;
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
