package org.astrsomn.zhipu;

import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.AiModelParamEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.EmbeddingSetting;
import org.astrsomn.core.common.langchain.extension.model.AbstractModelProviderHandler;
import org.astrsomn.core.common.utils.CollectionUtils;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.exception.UnknowModelException;

import java.util.Arrays;
import java.util.List;

/**
 * 智谱 AI；LangChain4j 使用 {@code model} 与 {@code baseUrl}，与 {@link AstroChatParam} 中 {@code modelName}/{@code apiUrl} 对应。
 */
public class ZhipuProviderHandler extends AbstractModelProviderHandler {

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
    public List<AiModelEntity> getAvailableModels() {
        return Arrays.stream(ZhipuModelEnum.values())
                .map(m -> m.toEntity(AiModelEnum.ProviderEnum.ZHIPU.getCode()))
                .toList();
    }

    private String modelId(AstroChatParam<?> param) {
        return StringUtils.isNotBlank(param.getModelSetting().getModelName())
                ? param.getModelSetting().getModelName()
                : ZhipuModelEnum.GLM_4_FLASH.getModelKey();
    }

    private ChatModel getChatModel(AstroChatParam<?> param) {
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

    private StreamingChatModel getStreamModel(AstroChatParam<?> param) {
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

    private EmbeddingModel getEmbeddingModel(AstroChatParam<?> param) {
        var builder = ZhipuAiEmbeddingModel.builder()
                .apiKey(param.getModelSetting().getApiKey())
                .model(modelId(param));
        if (StringUtils.isNotBlank(param.getModelSetting().getApiUrl())) {
            builder.baseUrl(param.getModelSetting().getApiUrl());
        }
        applyEmbeddingSetting(builder, param);
        return builder.build();
    }

    private static void applyChatSetting(ZhipuAiChatModel.ZhipuAiChatModelBuilder builder, AstroChatParam<?> param) {
        ChatSetting chatSetting = param.getChatSetting();
        if (chatSetting == null) {
            return;
        }
        String modelKey = resolveModelKey(param);
        if (chatSetting.getTemperature() != null
                && ZhipuModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
            builder.temperature(chatSetting.getTemperature());
        }
        if (chatSetting.getTopP() != null
                && ZhipuModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
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
        if (chatSetting.getTemperature() != null
                && ZhipuModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TEMPERATURE.getCode())) {
            builder.temperature(chatSetting.getTemperature());
        }
        if (chatSetting.getTopP() != null
                && ZhipuModelEnum.isParamAvailable(modelKey, AiModelParamEnum.ChatParamEnum.TOP_P.getCode())) {
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
        if (embeddingSetting.getDimensions() != null
                && ZhipuModelEnum.isParamAvailable(modelKey, AiModelParamEnum.EmbeddingParamEnum.DIMENSIONS.getCode())) {
            builder.dimensions(embeddingSetting.getDimensions());
        }
    }

    private static String resolveModelKey(AstroChatParam<?> param) {
        String modelKey = param.getModelKey();
        if (StringUtils.isBlank(modelKey) && param.getModelSetting() != null) {
            modelKey = param.getModelSetting().getModelName();
        }
        return modelKey;
    }
}
