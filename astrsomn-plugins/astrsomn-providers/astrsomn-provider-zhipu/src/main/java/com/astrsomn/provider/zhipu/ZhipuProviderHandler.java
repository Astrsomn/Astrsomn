package com.astrsomn.provider.zhipu;

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
import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.List;


import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
public class ZhipuProviderHandler extends AbstractModelProviderHandler {

    private static void applyChatSetting(ZhipuAiChatModel.ZhipuAiChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting chatSetting = param.getChatSetting();
        if (chatSetting == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (chatSetting.getTemperature() != null) {
            builder.temperature(chatSetting.getTemperature());
        }
        if (chatSetting.getTopP() != null) {
            builder.topP(chatSetting.getTopP());
        }
    }

    private static void applyChatSetting(
            ZhipuAiStreamingChatModel.ZhipuAiStreamingChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting chatSetting = param.getChatSetting();
        if (chatSetting == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (chatSetting.getTemperature() != null) {
            builder.temperature(chatSetting.getTemperature());
        }
        if (chatSetting.getTopP() != null) {
            builder.topP(chatSetting.getTopP());
        }
    }

    private static void applyEmbeddingSetting(
            ZhipuAiEmbeddingModel.ZhipuAiEmbeddingModelBuilder builder, AstroChatParam<?> param) {
        EmbeddingSetting embeddingSetting = param.getEmbeddingSetting();
        if (embeddingSetting == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (embeddingSetting.getDimensions() != null) {
            builder.dimensions(embeddingSetting.getDimensions());
        }
    }

    public static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if (StringUtils.isBlank(modelKey) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.ZHIPU;
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
            throw new UnknowModelException(
                    "Failed to initialize: " + modelClass.getName() + " is not supported by Zhipu provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException(
                    "Model instance created but is not compatible with " + modelClass.getName());
        }
    }

        @Override
    public List<AiModelEntity> getAvailableModels(String apiKey, String apiSecret) {
        ZhipuModelApiClient client = new ZhipuModelApiClient();
        List<ProviderModelDTO> dtos = client.listModels(apiKey, apiSecret);
        return dtos.stream().map(dto -> {
            AiModelEntity entity = new AiModelEntity();
            entity.setModelKey(dto.getModelKey());
            entity.setModelName(dto.getModelName() != null ? dto.getModelName() : dto.getModelKey());
            entity.setDescription(dto.getDescription());
            entity.setModelType(dto.getModelType());
            entity.setExtensionCode(AiModelEnum.ProviderEnum.ZHIPU.getCode());
            entity.setCapabilities(toJson(dto.getCapabilities()));
            entity.setParams(toJson(dto.getParams()));
            entity.setStatus(AiModelEnum.StatusEnum.DISABLED.getCode());
            entity.setSourceType(AiModelEnum.SourceTypeEnum.PLUGIN.getCode());
            return entity;
        }).collect(Collectors.toList());
    }

    private String modelId(AstroChatParam<?> param) {
        return StringUtils.isNotBlank(param.getModelSetting().getModelName())
                ? param.getModelSetting().getModelName()
                : "";
    }

    public ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = ZhipuAiChatModel.builder().apiKey(param.getModelSetting().getApiKey()).model(modelId(param));
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        applyChatSetting(builder, param);
        return builder.build();
    }

    public StreamingChatModel getStreamModel(AstroChatParam<?> param) {
        var builder = ZhipuAiStreamingChatModel.builder()
                .apiKey(param.getModelSetting().getApiKey())
                .model(modelId(param));
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        applyChatSetting(builder, param);
        return builder.build();
    }

    public EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        var builder = ZhipuAiEmbeddingModel.builder()
                .apiKey(param.getModelSetting().getApiKey())
                .model(modelId(param));
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        applyEmbeddingSetting(builder, param);
        return builder.build();
    }
}
