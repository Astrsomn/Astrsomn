package org.astrsomn.qwen;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.extension.model.AbstractModelProviderHandler;
import org.astrsomn.core.common.util.CollectionUtils;
import org.astrsomn.core.exception.UnknowModelException;

import java.util.Arrays;
import java.util.List;

public class QwenAiProviderHandler extends AbstractModelProviderHandler {

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.ALIBABA;
    }

    @Override
    public <T> T createModel(Class<T> modelClass, AstroChatParam<?> param) {
        // 1. 参数前置校验，防止后续深层调用出现 NPE
        if (modelClass == null || param == null || param.getModelSetting() == null) {
            throw new IllegalArgumentException("Model class and parameters must not be null");
        }

        Object model;

        // 2. 逻辑分支判断：注意 StreamingChatModel 通常是 ChatModel 的子类
        // 建议先判断最具体的接口
        if (StreamingChatModel.class.isAssignableFrom(modelClass)) {
            model = getStreamModel(param);
        } else if (ChatModel.class.isAssignableFrom(modelClass)) {
            model = getChatModel(param);
        } else if (EmbeddingModel.class.isAssignableFrom(modelClass)) {
            model = getEmbeddingModel(param);
        } else {
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by Qwen provider.");
        }

        // 3. 安全的类型转换
        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException("Model instance created but is not compatible with " + modelClass.getName());
        }
    }

    @Override
    public List<AiModelEntity> getAvailableModels() {
        return Arrays.stream(QwenModelEnum.values())
                .map(model -> model.toEntity(AiModelEnum.ProviderEnum.ALIBABA.getCode()))
                .toList();
    }

    private ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = QwenChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        return builder.build();
    }

    private StreamingChatModel getStreamModel(AstroChatParam<?> param) {
        var builder = QwenStreamingChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        return builder.build();
    }

    private EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        var builder = QwenEmbeddingModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        return builder.build();
    }

}