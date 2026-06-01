package com.astrsomn.provider.gemini;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ChatSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.EmbeddingSetting;
import com.astrsomn.api.runtime.common.langchain.extension.model.AbstractModelProviderHandler;
import com.astrsomn.common.UnknowModelException;
import com.astrsomn.common.utils.CollectionUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GeminiProviderHandler extends AbstractModelProviderHandler {

    private static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if ((modelKey == null || modelKey.isBlank()) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.GOOGLE;
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
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by Gemini provider.");
        }

        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException("Model instance created but is not compatible with " + modelClass.getName());
        }
    }

    @Override
    public List<AiModelEntity> getAvailableModels() {
        return Arrays.stream(GeminiModelEnum.values())
                .map(model -> model.toEntity(AiModelEnum.ProviderEnum.GOOGLE.getCode()))
                .toList();
    }

    private ChatModel getChatModel(AstroChatParam<?> param) {
        var builder = GoogleAiGeminiChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        ChatSetting cs = param.getChatSetting();
        if (cs != null) {
            String modelKey = resolveModelKey(param);
            if (Objects.nonNull(param.getConversationSetting()) && param.getConversationSetting().isEnableDeepThinking()) {
                builder.returnThinking(true);
                builder.sendThinking(true);
            }
            if (cs.getTemperature() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
                builder.temperature(cs.getTemperature());
            }
            if (cs.getTopP() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
                builder.topP(cs.getTopP());
            }
            if (cs.getTopK() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_K.getCode())) {
                builder.topK(cs.getTopK());
            }
            if (cs.getMaxTokens() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.MAX_TOKENS.getCode())) {
                builder.maxOutputTokens(cs.getMaxTokens());
            }
        }
        return builder.build();
    }

    private StreamingChatModel getStreamModel(AstroChatParam<?> param) {
        var builder = GoogleAiGeminiStreamingChatModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (CollectionUtils.isNotEmpty(param.getChatModelListeners())) {
            builder.listeners(param.getChatModelListeners());
        }
        ChatSetting cs = param.getChatSetting();
        if (cs != null) {
            String modelKey = resolveModelKey(param);
            if (Objects.nonNull(param.getConversationSetting()) && param.getConversationSetting().isEnableDeepThinking()) {
                builder.returnThinking(true);
                builder.sendThinking(true);
            }
            if (cs.getTemperature() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
                builder.temperature(cs.getTemperature());
            }
            if (cs.getTopP() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
                builder.topP(cs.getTopP());
            }
            if (cs.getTopK() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_K.getCode())) {
                builder.topK(cs.getTopK());
            }
            if (cs.getMaxTokens() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.MAX_TOKENS.getCode())) {
                builder.maxOutputTokens(cs.getMaxTokens());
            }
        }
        return builder.build();
    }

    private EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        var builder = GoogleAiEmbeddingModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        EmbeddingSetting es = param.getEmbeddingSetting();
        if (es != null) {
            String modelKey = resolveModelKey(param);
            if (es.getDimensions() != null
                    && GeminiModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS.getCode())) {
                builder.outputDimensionality(es.getDimensions());
            }
        }
        return builder.build();
    }
}
