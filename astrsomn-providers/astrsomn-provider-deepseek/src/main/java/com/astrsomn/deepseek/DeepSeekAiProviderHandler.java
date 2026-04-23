package com.astrsomn.deepseek;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import com.astrsomn.core.common.constant.AiModelEnum;
import com.astrsomn.core.common.constant.AiModelParamEnum;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import com.astrsomn.core.common.langchain.buildParam.setting.EmbeddingSetting;
import com.astrsomn.core.common.langchain.buildParam.setting.ImageSetting;
import com.astrsomn.core.common.langchain.extension.model.AbstractModelProviderHandler;
import com.astrsomn.core.common.utils.CollectionUtils;
import com.astrsomn.core.exception.UnknowModelException;

import com.astrsomn.core.common.utils.StringUtils;

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
        } else if (ImageModel.class.isAssignableFrom(modelClass)) {
            model = getImageModel(param);
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

    private ImageModel getImageModel(AstroChatParam<?> param) {
        var builder = OpenAiImageModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        applyImageSetting(builder, param);
        return builder.build();
    }

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
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS.getCode())) {
            builder.dimensions(es.getDimensions());
        }
        if (StringUtils.isNotBlank(es.getUser())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.USER.getCode())) {
            builder.user(es.getUser());
        }
        if (es.getMaxRetries() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.MAX_RETRIES.getCode())) {
            builder.maxRetries(es.getMaxRetries());
        }
        if (es.getMaxSegmentsPerBatch() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.MAX_SEGMENTS_PER_BATCH.getCode())) {
            builder.maxSegmentsPerBatch(es.getMaxSegmentsPerBatch());
        }
        if (StringUtils.isNotBlank(es.getEncodingFormat())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.ENCODING_FORMAT.getCode())) {
            builder.encodingFormat(es.getEncodingFormat());
        }
        if (es.getTimeoutSeconds() != null
                && es.getTimeoutSeconds() > 0
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.TIMEOUT_SECONDS.getCode())) {
            builder.timeout(Duration.ofSeconds(es.getTimeoutSeconds()));
        }
    }

    private static void applyChatSetting(OpenAiChatModel.OpenAiChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (cs.getTemperature() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.MAX_TOKENS.getCode())) {
            builder.maxTokens(cs.getMaxTokens());
        }
        if (cs.getSeed() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.SEED.getCode())) {
            builder.seed(cs.getSeed());
        }
        if (cs.getPresencePenalty() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY.getCode())) {
            builder.presencePenalty(cs.getPresencePenalty());
        }
        if (cs.getFrequencyPenalty() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY.getCode())) {
            builder.frequencyPenalty(cs.getFrequencyPenalty());
        }
    }

    private static void applyChatSetting(OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (cs.getTemperature() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.MAX_TOKENS.getCode())) {
            builder.maxTokens(cs.getMaxTokens());
        }
        if (cs.getSeed() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.SEED.getCode())) {
            builder.seed(cs.getSeed());
        }
        if (cs.getPresencePenalty() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.PRESENCE_PENALTY.getCode())) {
            builder.presencePenalty(cs.getPresencePenalty());
        }
        if (cs.getFrequencyPenalty() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.FREQUENCY_PENALTY.getCode())) {
            builder.frequencyPenalty(cs.getFrequencyPenalty());
        }
    }

    private static void applyImageSetting(OpenAiImageModel.OpenAiImageModelBuilder builder, AstroChatParam<?> param) {
        ImageSetting is = param.getImageSetting();
        if (is == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (StringUtils.isNotBlank(is.getSize())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.SIZE.getCode())) {
            builder.size(is.getSize());
        }
        if (StringUtils.isNotBlank(is.getStyle())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.STYLE.getCode())) {
            builder.style(is.getStyle());
        }
        if (StringUtils.isNotBlank(is.getQuality())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.QUALITY.getCode())) {
            builder.quality(is.getQuality());
        }
        if (StringUtils.isNotBlank(is.getResponseFormat())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.RESPONSE_FORMAT.getCode())) {
            builder.responseFormat(is.getResponseFormat());
        }
        if (StringUtils.isNotBlank(is.getUser())
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.USER.getCode())) {
            builder.user(is.getUser());
        }
        if (is.getMaxRetries() != null
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.MAX_RETRIES.getCode())) {
            builder.maxRetries(is.getMaxRetries());
        }
        if (is.getTimeoutSeconds() != null
                && is.getTimeoutSeconds() > 0
                && DeepSeekModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ImageParamEnum.TIMEOUT_SECONDS.getCode())) {
            builder.timeout(Duration.ofSeconds(is.getTimeoutSeconds()));
        }
    }

    private static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if ((modelKey == null || modelKey.isBlank()) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }


}
