package org.astrsomn.starter.langchain.factory.impl;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ImageSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ModelSetting;
import org.astrsomn.starter.langchain.factory.core.AbstractModelProviderHandler;

import org.springframework.stereotype.Component;

import java.util.List;

import static org.astrsomn.core.common.constant.AiModelEnum.InferenceParamEnum.*;

@Component
public class OpenAiProviderHandler extends AbstractModelProviderHandler {

    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return AiModelEnum.ProviderEnum.OPENAI;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createModel(Class<T> modelClass, AiModelEntity modelEntity, AiAccountEntity accountEntity, AstroChatParam<?> param) {

        if (modelClass.isAssignableFrom(StreamingChatModel.class)) {
            return (T) buildStreamingChatModel(modelEntity, accountEntity, param);
        } else if (modelClass.isAssignableFrom(ChatModel.class)) {
            return (T) buildChatModel(modelEntity, accountEntity, param);
        } else if (modelClass.isAssignableFrom(ImageModel.class)) {
            return (T) buildImageModel(modelEntity, accountEntity, param);
        } else if (modelClass.isAssignableFrom(EmbeddingModel.class)) {
            return (T) buildEmbeddingModel(modelEntity, accountEntity, param);
        }

        throw new IllegalArgumentException("OpenAI 暂不支持模型类型: " + modelClass.getName());
    }

    private EmbeddingModel buildEmbeddingModel(AiModelEntity modelEntity, AiAccountEntity accountEntity, AstroChatParam<?> param) {
        return null;
    }

    private ChatModel buildChatModel(AiModelEntity modelEntity, AiAccountEntity accountEntity, AstroChatParam<?> param) {
        return null;
    }

    private StreamingChatModel buildStreamingChatModel(AiModelEntity entity,
                                                       AiAccountEntity account,
                                                       AstroChatParam<?> param) {

        var builder = OpenAiStreamingChatModel.builder()
                .modelName(entity.getModelName())
                .apiKey(account.getApiKey())
                .baseUrl(entity.getApiUrl());

        // 获取参数和能力集
        ModelSetting s = param.getModelSetting();
        List<String> caps = parseCapabilities(entity);

        // --- 声明式参数填充 (告别 if-else) ---
        apply(caps, TEMPERATURE, s.getTemperature(), builder::temperature);
        apply(caps, TOP_P, s.getTopP(), builder::topP);
        apply(caps, MAX_TOKENS, s.getMaxTokens(), builder::maxTokens);
        apply(caps, SEED, s.getSeed(), builder::seed);
        apply(caps, PRESENCE_PENALTY, s.getPresencePenalty(), builder::presencePenalty);
        apply(caps, FREQUENCY_PENALTY, s.getFrequencyPenalty(), builder::frequencyPenalty);

        // 特殊逻辑：深度思考
        if (param.getChatSetting().isEnableDeepThinking() && caps.contains("DEEP_REASONING")) {
            builder.returnThinking(true).sendThinking(true);
        }

        return builder.build();
    }

    private ImageModel buildImageModel(AiModelEntity entity, AiAccountEntity account, AstroChatParam<?> param) {
        ImageSetting s = param.getImageSetting();
        return OpenAiImageModel.builder()
                .apiKey(account.getApiKey())
                .modelName(entity.getModelName())
                .size(s.getSize())
                .quality(s.getQuality())
                .build();
    }
}
