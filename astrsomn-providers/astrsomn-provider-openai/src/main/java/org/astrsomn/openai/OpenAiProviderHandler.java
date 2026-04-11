package org.astrsomn.openai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.EmbeddingSetting;
import org.astrsomn.core.common.langchain.extension.model.AbstractModelProviderHandler;
import org.astrsomn.core.common.util.CollectionUtils;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.exception.UnknowModelException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * OpenAI 官方 API（及兼容端点）；可通过 {@code baseUrl} 指向代理或第三方兼容服务。
 */
public class OpenAiProviderHandler extends AbstractModelProviderHandler {

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.OPENAI;
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
                    "Failed to initialize: " + modelClass.getName() + " is not supported by OpenAI provider.");
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
        return Arrays.stream(OpenAiModelEnum.values())
                .map(m -> m.toEntity(AiModelEnum.ProviderEnum.OPENAI.getCode()))
                .toList();
    }

    private ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = OpenAiChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
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
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
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
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
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
}
