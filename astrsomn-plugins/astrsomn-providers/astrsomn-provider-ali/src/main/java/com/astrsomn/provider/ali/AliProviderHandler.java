package com.astrsomn.provider.ali;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ChatSetting;
import com.astrsomn.api.runtime.common.langchain.extension.model.AbstractModelProviderHandler;
import com.astrsomn.common.UnknowModelException;
import com.astrsomn.common.utils.CollectionUtils;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.List;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
public class AliProviderHandler extends AbstractModelProviderHandler {

    public static void applyChatSetting(QwenChatModel.QwenChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) return;
        String modelKey = resolveModelKey(param);
        if (cs.getTemperature() != null) {
            builder.temperature(cs.getTemperature().floatValue());
        }
        if (cs.getTopP() != null) {
            builder.topP(cs.getTopP().doubleValue());
        }
        if (cs.getMaxTokens() != null) {
            builder.maxTokens(cs.getMaxTokens());
        }
    }

    public static void applyChatSetting(QwenStreamingChatModel.QwenStreamingChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) return;
        String modelKey = resolveModelKey(param);
        if (cs.getTemperature() != null) {
            builder.temperature(cs.getTemperature().floatValue());
        }
        if (cs.getTopP() != null) {
            builder.topP(cs.getTopP().doubleValue());
        }
        if (cs.getMaxTokens() != null) {
            builder.maxTokens(cs.getMaxTokens());
        }
    }

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.ALIBABA;
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
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by Ali provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException("Model instance created but is not compatible with " + modelClass.getName());
        }
    }

        @Override
    public List<AiModelEntity> getAvailableModels(String apiKey, String apiSecret) {
        AliModelApiClient client = new AliModelApiClient();
        List<ProviderModelDTO> dtos = client.listModels(apiKey, apiSecret);
        return dtos.stream().map(dto -> {
            AiModelEntity entity = new AiModelEntity();
            entity.setModelKey(dto.getModelKey());
            entity.setModelName(dto.getModelName() != null ? dto.getModelName() : dto.getModelKey());
            entity.setDescription(dto.getDescription());
            entity.setModelType(dto.getModelType());
            entity.setExtensionCode(AiModelEnum.ProviderEnum.ALIBABA.getCode());
            entity.setCapabilities(toJson(dto.getCapabilities()));
            entity.setParams(toJson(dto.getParams()));
            entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
            entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
            return entity;
        }).collect(Collectors.toList());
    }

    protected ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = QwenChatModel.builder()
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
        var builder = QwenStreamingChatModel.builder()
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
        var builder = QwenEmbeddingModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        return builder.build();
    }

}
