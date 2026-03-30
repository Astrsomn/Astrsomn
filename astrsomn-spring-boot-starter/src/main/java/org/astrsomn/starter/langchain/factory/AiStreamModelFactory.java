package org.astrsomn.starter.langchain.factory;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;

import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ModelSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;

import org.astrsomn.starter.langchain.quota.AstroModelListener;
import org.astrsomn.starter.langchain.quota.ModelQuotaManager;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.List;
import java.util.Objects;




@Slf4j
@Component
@RequiredArgsConstructor
public class AiStreamModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AstrsomnProperties astrsomnProperties;
    private final ModelQuotaManager quotaManager;
    private final AstroModelListener astroModelListener;
    private final AiAccountMapper aiAccountMapper;


    public <T> StreamingChatModel getStreamingModel(AstroChatParam<T> param) {

        ModelSetting modelSetting = param.getModelSetting();
        ChatSetting chatSetting = param.getChatSetting();
        AiModelEntity modelEntity = aiModelMapper.selectOne(new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getModelKey, param.getModelKey())
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        if (modelEntity.getMaxQuotaTokens() != null &&
                quotaManager.isExceeded(modelEntity.getModelKey(), modelEntity.getMaxQuotaTokens())) {
            log.warn("====> [Astrsomn] 模型 {} 流量超限，准备切换到备选模型，服务降级", modelEntity.getModelKey());
            String fallbackKey = astrsomnProperties.getRefs().getDefaultModelKey();
            modelEntity = aiModelMapper.selectOne(new LambdaUpdateWrapper<AiModelEntity>()
                    .eq(AiModelEntity::getModelKey, fallbackKey)
                    .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
            param.setModelKey(modelEntity.getModelKey());
        }
        AiAccountEntity aiAccountEntity = aiAccountMapper.selectOne(new LambdaQueryWrapper<AiAccountEntity>()
                .eq(AiAccountEntity::getAccountKey, modelEntity.getAccountKey()));
        AiModelEnum.ProviderEnum providerEnum =
                AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());

        if (Objects.isNull(providerEnum)) {
            log.error("====>  Astrsomn  ====>  未找到匹配的模型提供商");
            throw new RuntimeException("");
        }

        switch (providerEnum) {
            case ALIBABA:
                return getQwenStreamingChatModel(modelEntity, modelSetting, chatSetting, aiAccountEntity, param);
            case OPENAI:
                return getStreamLanguageModel(modelEntity, modelSetting, chatSetting,aiAccountEntity, param);
            case DEEPSEEK:
                return getStreamLanguageModel(modelEntity, modelSetting, chatSetting,aiAccountEntity, param);
            case ZHIPU:
                return getZhiPuStreamLanguageModel(modelEntity, modelSetting, chatSetting,aiAccountEntity, param);
            case QIANFAN:
                return getQianfanStreamLanguageModel(modelEntity, modelSetting, chatSetting,aiAccountEntity, param);
            case GOOGLE:
                return getGoogleGeminiStreamLanguageModel(modelEntity, modelSetting, chatSetting,aiAccountEntity, param);
            // 可以继续加其他 case
            default:
                log.error("====>  Astrsomn  ====> 未处理的 provider 类型: {} <====", providerEnum);
                throw new RuntimeException("未知的大模型参数");
        }
    }


    private StreamingChatModel getGoogleGeminiStreamLanguageModel(AiModelEntity modelEntity,
                                                                  ModelSetting modelSetting,
                                                                  ChatSetting chatSetting,
                                                                  AiAccountEntity aiAccountEntity,
                                                                  AstroChatParam astroChatParam) {
        return GoogleAiGeminiStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .apiKey(aiAccountEntity.getApiKey())
                .build();
    }

    private StreamingChatModel getQianfanStreamLanguageModel(AiModelEntity modelEntity,
                                                             ModelSetting modelSetting,
                                                             ChatSetting chatSetting,
                                                             AiAccountEntity aiAccountEntity,
                                                             AstroChatParam astroChatParam) {

        return OpenAiStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .apiKey(aiAccountEntity.getApiKey())
                .baseUrl(modelEntity.getApiUrl())
                .build();
    }

    private StreamingChatModel getZhiPuStreamLanguageModel(AiModelEntity modelEntity,
                                                           ModelSetting modelSetting,
                                                           ChatSetting chatSetting,
                                                           AiAccountEntity aiAccountEntity,
                                                           AstroChatParam astroChatParam) {

        return ZhipuAiStreamingChatModel.builder()
                .model(modelEntity.getModelName())
                .apiKey(aiAccountEntity.getApiKey())
                .build();
    }


    private OpenAiStreamingChatModel getStreamLanguageModel(AiModelEntity modelEntity,
                                                            ModelSetting modelSetting,
                                                            ChatSetting chatSetting,
                                                            AiAccountEntity aiAccountEntity,
                                                            AstroChatParam astroChatParam) {


        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .baseUrl(modelEntity.getApiUrl())
                .apiKey(aiAccountEntity.getApiKey())
                .listeners(Collections.singletonList(astroModelListener.createBindingListener(astroChatParam)))
                .logRequests(true)
                .logResponses(true);
        List<String> capabilities = JsonUtil.parseArray(modelEntity.getCapabilities(), String.class);
        if (capabilities == null) {
            capabilities = Collections.emptyList();
        }

        if (capabilities.contains(AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING.getCode()) && chatSetting.isEnableDeepThinking()) {
            builder.returnThinking(true);
            builder.sendThinking(true);
        }
        if (AiModelEnum.InferenceParamEnum.TEMPERATURE.containedIn(capabilities)
                && modelSetting.getTemperature() != null) {
            builder.temperature(modelSetting.getTemperature());
        }
        if (AiModelEnum.InferenceParamEnum.TOP_P.containedIn(capabilities) && modelSetting.getTopP() != null) {
            builder.topP(modelSetting.getTopP());
        }
        if (AiModelEnum.InferenceParamEnum.SEED.containedIn(capabilities) && modelSetting.getSeed() != null) {
            builder.seed(modelSetting.getSeed());
        }
        if (AiModelEnum.InferenceParamEnum.MAX_TOKENS.containedIn(capabilities)
                && modelSetting.getMaxTokens() != null) {
            builder.maxTokens(modelSetting.getMaxTokens());
        }
        if (AiModelEnum.InferenceParamEnum.PRESENCE_PENALTY.containedIn(capabilities)
                && modelSetting.getPresencePenalty() != null) {
            builder.presencePenalty(modelSetting.getPresencePenalty());
        }
        if (AiModelEnum.InferenceParamEnum.FREQUENCY_PENALTY.containedIn(capabilities)
                && modelSetting.getFrequencyPenalty() != null) {
            builder.frequencyPenalty(modelSetting.getFrequencyPenalty());
        }
        return builder.build();
    }


    private StreamingChatModel getQwenStreamingChatModel(AiModelEntity modelEntity,
                                                         ModelSetting modelSetting,
                                                         ChatSetting chatSetting,
                                                         AiAccountEntity aiAccountEntity,
                                                         AstroChatParam astroChatParam) {

        QwenStreamingChatModel.QwenStreamingChatModelBuilder builder = QwenStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .apiKey(aiAccountEntity.getApiKey());

        List<String> capabilities = JsonUtil.parseArray(modelEntity.getCapabilities(), String.class);
        if (capabilities == null) {
            capabilities = Collections.emptyList();
        }
        if (capabilities.contains(AiModelEnum.ChatCapabilitiesEnum.DEEP_REASONING.getCode()) && chatSetting.isEnableDeepThinking()) {

        }
        if (capabilities.contains(AiModelEnum.ChatCapabilitiesEnum.NETWORK_SEARCH.getCode()) && chatSetting.isEnableNetwork()) {
            builder.enableSearch(true);
        }
        if (AiModelEnum.InferenceParamEnum.TEMPERATURE.containedIn(capabilities)
                && modelSetting.getTemperature() != null) {
            builder.temperature(modelSetting.getTemperature().floatValue());
        }
        if (AiModelEnum.InferenceParamEnum.TOP_P.containedIn(capabilities) && modelSetting.getTopP() != null) {
            builder.topP(modelSetting.getTopP());
        }
        if (AiModelEnum.InferenceParamEnum.TOP_K.containedIn(capabilities) && modelSetting.getTopK() != null) {
            builder.topK(modelSetting.getTopK());
        }
        if (AiModelEnum.InferenceParamEnum.SEED.containedIn(capabilities) && modelSetting.getSeed() != null) {
            builder.seed(modelSetting.getSeed());
        }
        if (AiModelEnum.InferenceParamEnum.MAX_TOKENS.containedIn(capabilities)
                && modelSetting.getMaxTokens() != null) {
            builder.maxTokens(modelSetting.getMaxTokens());
        }
        return builder.build();
    }
}
