package com.astrsomn.provider.minimax;

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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MinimaxProviderHandler extends AbstractModelProviderHandler {

    private static void applyEmbeddingSetting(
            OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder builder, AstroChatParam<?> param) {
        EmbeddingSetting es = param.getEmbeddingSetting();
        if (es == null) {
            return;
        }
        String modelKey = param.getModelKey();
        if ((modelKey == null || modelKey.isBlank()) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        if (es.getDimensions() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS.getCode())) {
            builder.dimensions(es.getDimensions());
        }
        if (StringUtils.isNotBlank(es.getUser())
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.USER.getCode())) {
            builder.user(es.getUser());
        }
        if (es.getMaxRetries() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.MAX_RETRIES.getCode())) {
            builder.maxRetries(es.getMaxRetries());
        }
        if (es.getTimeoutSeconds() != null
                && es.getTimeoutSeconds() > 0
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.TIMEOUT_SECONDS.getCode())) {
            builder.timeout(Duration.ofSeconds(es.getTimeoutSeconds()));
        }
    }

    private static void applyChatSetting(OpenAiChatModel.OpenAiChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (Objects.nonNull(param.getConversationSetting()) && param.getConversationSetting().isEnableDeepThinking()) {
            builder.sendThinking(true);
            builder.returnThinking(true);
        }
        if (cs.getTemperature() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.MAX_TOKENS.getCode())) {
            builder.maxTokens(cs.getMaxTokens());
        }
    }

    private static void applyChatSetting(OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (Objects.nonNull(param.getConversationSetting()) && param.getConversationSetting().isEnableDeepThinking()) {
            builder.sendThinking(true);
            builder.returnThinking(true);
        }
        if (cs.getTemperature() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null
                && MinimaxModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.MAX_TOKENS.getCode())) {
            builder.maxTokens(cs.getMaxTokens());
        }
    }

    private static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if ((modelKey == null || modelKey.isBlank()) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.MINIMAX;
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
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by MiniMax provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException("Model instance created but is not compatible with " + modelClass.getName());
        }
    }

    @Override
    public List<AiModelEntity> getAvailableModels() {
        return Arrays.stream(MinimaxModelEnum.values())
                .map(model -> model.toEntity(AiModelEnum.ProviderEnum.MINIMAX.getCode()))
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
        applyChatSetting(builder, param);
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
        applyChatSetting(builder, param);
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
}
