package com.astrsomn.provider.qianfan;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.api.runtime.common.langchain.extension.model.AbstractModelProviderHandler;
import com.astrsomn.common.UnknowModelException;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.community.model.qianfan.QianfanEmbeddingModel;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.Arrays;
import java.util.List;


public class QianFanProviderHandler extends AbstractModelProviderHandler {

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.QIANFAN;
    }

    @Override
    public <T> T createModel(Class<T> modelClass, AstroChatParam<?> param) {
        if (modelClass == null || param == null || param.getModelSetting() == null) {
            throw new IllegalArgumentException("Model class and parameters must not be null");
        }
        if (StringUtils.isBlank(param.getModelSetting().getApiKey())
                || StringUtils.isBlank(param.getModelSetting().getApiSecret())) {
            throw new IllegalArgumentException("Qianfan requires apiKey and apiSecret (secretKey)");
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
                    "Failed to initialize: " + modelClass.getName() + " is not supported by QianFan provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException(
                    "Model instance created but is not compatible with " + modelClass.getName());
        }
    }

    @Override
    public List<AiModelEntity> getAvailableModels() {
        return Arrays.stream(QianFanModelEnum.values())
                .map(model -> model.toEntity(AiModelEnum.ProviderEnum.QIANFAN.getCode()))
                .toList();
    }

    private ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = QianfanChatModel.builder()
                .apiKey(param.getModelSetting().getApiKey())
                .secretKey(param.getModelSetting().getApiSecret())
                .modelName(param.getModelSetting().getModelName());
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        return builder.build();
    }

    private StreamingChatModel getStreamModel(AstroChatParam<?> param) {
        var builder = QianfanStreamingChatModel.builder()
                .apiKey(param.getModelSetting().getApiKey())
                .secretKey(param.getModelSetting().getApiSecret())
                .modelName(param.getModelSetting().getModelName());
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        return builder.build();
    }

    private EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        var builder = QianfanEmbeddingModel.builder()
                .apiKey(param.getModelSetting().getApiKey())
                .secretKey(param.getModelSetting().getApiSecret())
                .modelName(param.getModelSetting().getModelName());
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        return builder.build();
    }
}
