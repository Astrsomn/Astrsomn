package org.astrsomn.deepseek;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.langchain.buildParam.setting.EmbeddingSetting;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.extension.AbstractModelProviderHandler;
import org.astrsomn.core.common.util.CollectionUtils;
import org.astrsomn.core.exception.UnknowModelException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class DeepSeekAiProviderHandler extends AbstractModelProviderHandler {
    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.DEEPSEEK;
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
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by DeepSeek provider.");
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
        return Arrays.stream(DeepSeekModelEnum.values())
                .map(model -> model.toEntity(AiModelEnum.ProviderEnum.DEEPSEEK.getCode()))
                .toList();
    }

    private ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = OpenAiChatModel.builder()
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
        var builder = OpenAiStreamingChatModel.builder()
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
        var builder = OpenAiEmbeddingModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (param.getModelSetting().getApiUrl() != null && !param.getModelSetting().getApiUrl().isEmpty()) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        applyEmbeddingSetting(builder, param);
        return builder.build();
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
        if (org.astrsomn.core.common.util.StringUtils.isNotBlank(es.getUser())) {
            builder.user(es.getUser());
        }
        if (es.getMaxRetries() != null) {
            builder.maxRetries(es.getMaxRetries());
        }
        if (es.getMaxSegmentsPerBatch() != null) {
            builder.maxSegmentsPerBatch(es.getMaxSegmentsPerBatch());
        }
        if (org.astrsomn.core.common.util.StringUtils.isNotBlank(es.getEncodingFormat())) {
            builder.encodingFormat(es.getEncodingFormat());
        }
        if (es.getTimeoutSeconds() != null && es.getTimeoutSeconds() > 0) {
            builder.timeout(Duration.ofSeconds(es.getTimeoutSeconds()));
        }
    }


}
