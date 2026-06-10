package com.astrsomn.provider.deepseek;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.AiModelParamEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ChatSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.EmbeddingSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ImageSetting;
import com.astrsomn.api.runtime.common.langchain.extension.model.AbstractModelProviderHandler;
import com.astrsomn.common.UnknowModelException;
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
public class DeepSeekAiProviderHandler extends AbstractModelProviderHandler {
    public static void applyEmbeddingSetting(
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
                ) {
            builder.dimensions(es.getDimensions());
        }
        if (StringUtils.isNotBlank(es.getUser())
                ) {
            builder.user(es.getUser());
        }
        if (es.getMaxRetries() != null
                ) {
            builder.maxRetries(es.getMaxRetries());
        }
        if (es.getMaxSegmentsPerBatch() != null
                ) {
            builder.maxSegmentsPerBatch(es.getMaxSegmentsPerBatch());
        }
        if (StringUtils.isNotBlank(es.getEncodingFormat())
                ) {
            builder.encodingFormat(es.getEncodingFormat());
        }
        if (es.getTimeoutSeconds() != null
                && es.getTimeoutSeconds() > 0
                ) {
            builder.timeout(Duration.ofSeconds(es.getTimeoutSeconds()));
        }
    }

    public static void applyChatSetting(OpenAiChatModel.OpenAiChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        System.out.println("====>  对话参数:{}" + param.getConversationSetting());
        if (Objects.nonNull(param.getConversationSetting()) && param.getConversationSetting().isEnableDeepThinking()) {
            builder.sendThinking(true);
            builder.returnThinking(true);
        }
        if (cs.getTemperature() != null
                ) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null
                ) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null
                ) {
            builder.maxTokens(cs.getMaxTokens());
        }
        if (cs.getSeed() != null
                ) {
            builder.seed(cs.getSeed());
        }
        if (cs.getPresencePenalty() != null
                ) {
            builder.presencePenalty(cs.getPresencePenalty());
        }
        if (cs.getFrequencyPenalty() != null
                ) {
            builder.frequencyPenalty(cs.getFrequencyPenalty());
        }
    }

    public static void applyChatSetting(OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting cs = param.getChatSetting();
        if (cs == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        System.out.println("====>  对话参数:{}" + param.getConversationSetting());
        if (Objects.nonNull(param.getConversationSetting()) && param.getConversationSetting().isEnableDeepThinking()) {
            builder.sendThinking(true);
            builder.returnThinking(true);
        }
        if (cs.getTemperature() != null
                ) {
            builder.temperature(cs.getTemperature());
        }
        if (cs.getTopP() != null
                ) {
            builder.topP(cs.getTopP());
        }
        if (cs.getMaxTokens() != null
                ) {
            builder.maxTokens(cs.getMaxTokens());
        }
        if (cs.getSeed() != null
                ) {
            builder.seed(cs.getSeed());
        }
        if (cs.getPresencePenalty() != null
                ) {
            builder.presencePenalty(cs.getPresencePenalty());
        }
        if (cs.getFrequencyPenalty() != null
                ) {
            builder.frequencyPenalty(cs.getFrequencyPenalty());
        }
    }

    public static void applyImageSetting(OpenAiImageModel.OpenAiImageModelBuilder builder, AstroChatParam<?> param) {
        ImageSetting is = param.getImageSetting();
        if (is == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (StringUtils.isNotBlank(is.getSize())
                ) {
            builder.size(is.getSize());
        }
        if (StringUtils.isNotBlank(is.getStyle())
                ) {
            builder.style(is.getStyle());
        }
        if (StringUtils.isNotBlank(is.getQuality())
                ) {
            builder.quality(is.getQuality());
        }
        if (StringUtils.isNotBlank(is.getResponseFormat())
                ) {
            builder.responseFormat(is.getResponseFormat());
        }
        if (StringUtils.isNotBlank(is.getUser())
                ) {
            builder.user(is.getUser());
        }
        if (is.getMaxRetries() != null
                ) {
            builder.maxRetries(is.getMaxRetries());
        }
        if (is.getTimeoutSeconds() != null
                && is.getTimeoutSeconds() > 0
                ) {
            builder.timeout(Duration.ofSeconds(is.getTimeoutSeconds()));
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
        return AiModelEnum.ProviderEnum.DEEPSEEK;
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
        } else if (ImageModel.class.isAssignableFrom(modelClass)) {
            model = getImageModel(param);
        } else if (EmbeddingModel.class.isAssignableFrom(modelClass)) {
            model = getEmbeddingModel(param);
        } else {
            throw new UnknowModelException("Failed to initialize: " + modelClass.getName() + " is not supported by DeepSeek provider.");
        }


        try {
            return modelClass.cast(model);
        } catch (ClassCastException e) {
            throw new UnknowModelException("Model instance created but is not compatible with " + modelClass.getName());
        }
    }

        @Override
    public List<AiModelEntity> getAvailableModels(String apiKey, String apiSecret) {
        DeepSeekModelApiClient client = new DeepSeekModelApiClient();
        List<ProviderModelDTO> dtos = client.listModels(apiKey, apiSecret);
        return dtos.stream().map(dto -> {
            AiModelEntity entity = new AiModelEntity();
            entity.setModelKey(dto.getModelKey());
            entity.setModelName(dto.getModelName() != null ? dto.getModelName() : dto.getModelKey());
            entity.setDescription(dto.getDescription());
            entity.setModelType(dto.getModelType());
            entity.setExtensionCode(AiModelEnum.ProviderEnum.DEEPSEEK.getCode());
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

    protected ImageModel getImageModel(AstroChatParam<?> param) {
        var builder = OpenAiImageModel.builder()
                .modelName(param.getModelSetting().getModelName())
                .apiKey(param.getModelSetting().getApiKey());
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        applyImageSetting(builder, param);
        return builder.build();
    }


}
