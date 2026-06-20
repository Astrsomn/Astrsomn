package com.astrsomn.provider.volcengine;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ChatSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.EmbeddingSetting;
import com.astrsomn.api.runtime.common.langchain.extension.model.AbstractModelProviderHandler;
import com.astrsomn.common.UnknowModelException;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
public class VolcengineProviderHandler extends AbstractModelProviderHandler {

    private static void applyChatSetting(OpenAiChatModel.OpenAiChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (cs.getTemperature() != null) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null) {
            builder.maxTokens(cs.getMaxTokens());
        }
    }

    private static void applyChatSetting(OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (cs.getTemperature() != null) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null) {
            builder.maxTokens(cs.getMaxTokens());
        }
    }

    private static void applyEmbeddingSetting(
            OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder builder, AstroChatParam<?> param) {
        EmbeddingSetting es = param.getEmbeddingSetting();
        if (es == null) {
            return;
        }
        if (es.getDimensions() != null) {
            builder.dimensions(es.getDimensions());
        }
        if (StringUtils.isNotBlank(es.getUser())) {
            builder.user(es.getUser());
        }
        if (es.getMaxRetries() != null) {
            builder.maxRetries(es.getMaxRetries());
        }
        if (es.getMaxSegmentsPerBatch() != null) {
            builder.maxSegmentsPerBatch(es.getMaxSegmentsPerBatch());
        }
        if (StringUtils.isNotBlank(es.getEncodingFormat())) {
            builder.encodingFormat(es.getEncodingFormat());
        }
        if (es.getTimeoutSeconds() != null && es.getTimeoutSeconds() > 0) {
            builder.timeout(Duration.ofSeconds(es.getTimeoutSeconds()));
        }
    }

    public static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if ((modelKey == null || modelKey.isBlank()) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.VOLCENGINE;
    }

    @Override
    public <T> T createModel(Class<T> modelClass, AstroChatParam<?> param) {
        if (modelClass == null || param == null || param.getModelSetting() == null) {
            throw new IllegalArgumentException("Model class and parameters must not be null");
        }

        Object model;

        if (StreamingChatModel.class.isAssignableFrom(modelClass)) {
            model = getStreamModel(param);
        } else if (ChatModel.class.isAssignableFrom(modelClass)) {
            model = getChatModel(param);
        } else if (EmbeddingModel.class.isAssignableFrom(modelClass)) {
            model = getEmbeddingModel(param);
        } else {
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by Volcengine provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException("Model instance created but is not compatible with " + modelClass.getName());
        }
    }

        @Override
    public List<AiModelEntity> getAvailableModels(String apiKey, String apiSecret) {
        VolcengineModelApiClient client = new VolcengineModelApiClient();
        List<ProviderModelDTO> dtos = client.listModels(apiKey, apiSecret);
        return dtos.stream().map(dto -> {
            AiModelEntity entity = new AiModelEntity();
            entity.setModelKey(dto.getModelKey());
            entity.setModelName(dto.getModelName() != null ? dto.getModelName() : dto.getModelKey());
            entity.setDescription(dto.getDescription());
            entity.setModelType(dto.getModelType());
            entity.setExtensionCode(AiModelEnum.ProviderEnum.VOLCENGINE.getCode());
            entity.setCapabilities(toJson(dto.getCapabilities()));
            entity.setParams(toJson(dto.getParams()));
            entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
            entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
            return entity;
        }).collect(Collectors.toList());
    }

    protected ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = OpenAiChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        applyChatSetting(builder, param);
        return builder.build();
    }

    protected StreamingChatModel getStreamModel(AstroChatParam<?> param) {
        var builder = OpenAiStreamingChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        applyChatSetting(builder, param);
        return builder.build();
    }

    protected EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        var builder = OpenAiEmbeddingModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        applyEmbeddingSetting(builder, param);
        return builder.build();
    }
}
