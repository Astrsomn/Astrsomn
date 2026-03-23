package org.astrsomn.starter.langchain.factory;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.ModelDisabledException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;
import org.astrsomn.core.common.langchain.buildParam.ChatFeatureFlags;
import org.astrsomn.core.common.langchain.buildParam.ModelInferenceConfig;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.astrsomn.core.common.constant.AiModelEnum.ProviderEnum.DEEPSEEK;



@Slf4j
@Component
public class AiStreamModelFactory {
    @Resource
    private AiModelMapper aiModelMapper;

    private AstrsomnProperties astrsomnProperties;

    public <T> StreamingChatModel getStreamingModel(AstroChatRequest<T> param) {

        ModelInferenceConfig modelConfig = param.getInferenceConfig();
        ChatFeatureFlags chatFeatureFlags = param.getFeatures();
        AiModelEntity modelEntity = aiModelMapper.selectOne(new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getModelKey, param.getModelKey())
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        AiModelEnum.ProviderEnum providerEnum =
                AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());

        if (Objects.isNull(providerEnum)) {
            log.error("====>  Astrsomn  ====>  未找到匹配的模型提供商");
            throw new RuntimeException("");
        }

        switch (providerEnum) {
            case ALIBABA:
                return getQwenStreamingChatModel(modelEntity, modelConfig, chatFeatureFlags);
            case OPENAI:
                return getStreamLanguageModel(modelEntity, modelConfig, chatFeatureFlags);
            case DEEPSEEK:
                return getStreamLanguageModel(modelEntity, modelConfig, chatFeatureFlags);
            case ZHIPU:
                return getZhiPuStreamLanguageModel(modelEntity, modelConfig, chatFeatureFlags);
            case QIANFAN:
                return getQianfanStreamLanguageModel(modelEntity, modelConfig, chatFeatureFlags);
            case GOOGLE:
                return getGoogleGeminiStreamLanguageModel(modelEntity, modelConfig, chatFeatureFlags);
            // 可以继续加其他 case
            default:
                log.error("====>  Astrsomn  ====> 未处理的 provider 类型: {} <====", providerEnum);
                throw new RuntimeException("未知的大模型参数");
        }
    }


    private StreamingChatModel getGoogleGeminiStreamLanguageModel(AiModelEntity modelEntity,
                                                                  ModelInferenceConfig modelConfig,
                                                                  ChatFeatureFlags chatFeatureFlags) {
        return GoogleAiGeminiStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .apiKey(modelEntity.getApiKey())
                .build();
    }

    private StreamingChatModel getQianfanStreamLanguageModel(AiModelEntity modelEntity,
                                                             ModelInferenceConfig modelConfig,
                                                             ChatFeatureFlags chatFeatureFlags) {

        return OpenAiStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .apiKey(modelEntity.getApiKey())
                .baseUrl(modelEntity.getApiUrl())
                .build();
    }

    private StreamingChatModel getZhiPuStreamLanguageModel(AiModelEntity modelEntity,
                                                           ModelInferenceConfig modelConfig,
                                                           ChatFeatureFlags chatFeatureFlags) {

        return ZhipuAiStreamingChatModel.builder()
                .model(modelEntity.getModelName())
                .apiKey(modelEntity.getApiKey())
                .build();
    }




    private OpenAiStreamingChatModel getStreamLanguageModel(AiModelEntity modelEntity,
                                                            ModelInferenceConfig modelConfig,
                                                            ChatFeatureFlags chatFeatureFlags) {



        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .baseUrl(modelEntity.getApiUrl())
                .apiKey(modelEntity.getApiKey())
                .maxTokens(modelConfig.getMaxTokens())
                .logRequests(true)
                .logResponses(true);
        List<String> capabilities = JsonUtil.parseArray(modelEntity.getCapabilities(), String.class);
        // 深度思考
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.DEEP_REASONING.getCode()) && chatFeatureFlags.isEnableDeepThinking()) {

        }

        // temperature
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TEMPERATURE_SETTING.getCode()) && Objects.nonNull(modelConfig.getTemperature())) {
            builder.temperature(modelConfig.getTemperature());
        }
        // topP
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TOP_P_SETTING.getCode())) {
            builder.topP(modelConfig.getTopP());
        }

        // seed
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.SEED_SETTING.getCode())) {
            builder.seed(modelConfig.getSeed());
        }
        // maxTokens
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.MAX_TOKEN_SETTING.getCode()) && Objects.nonNull(modelConfig.getMaxTokens())) {
            builder.maxTokens(modelConfig.getMaxTokens());
        }

        return builder.build();
    }



    private StreamingChatModel getQwenStreamingChatModel(AiModelEntity modelEntity,
                                                         ModelInferenceConfig modelConfig,
                                                         ChatFeatureFlags chatFeatureFlags) {

        QwenStreamingChatModel.QwenStreamingChatModelBuilder builder = QwenStreamingChatModel.builder()
                .modelName(modelEntity.getModelName())
                .apiKey(modelEntity.getApiKey());

        List<String> capabilities = JsonUtil.parseArray(modelEntity.getCapabilities(), String.class);
        // 深度思考
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.DEEP_REASONING.getCode()) && chatFeatureFlags.isEnableDeepThinking()) {

        }
        // 联网查找
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.NETWORK_SEARCH.getCode()) && chatFeatureFlags.isEnableNetwork()) {
            builder.enableSearch(true);
        }
        // temperature
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TEMPERATURE_SETTING.getCode()) && Objects.nonNull(modelConfig.getTemperature())) {
            builder.temperature(modelConfig.getTemperature().floatValue());
        }
        // topP
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TOP_P_SETTING.getCode())) {
            builder.topP(modelConfig.getTopP());
        }
        // topK
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TOP_K_SETTING.getCode())) {
            builder.topK(modelConfig.getTopK());
        }
        // seed
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.SEED_SETTING.getCode())) {
            builder.seed(modelConfig.getSeed());
        }
        // maxTokens
        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.MAX_TOKEN_SETTING.getCode()) && Objects.nonNull(modelConfig.getMaxTokens())) {
            builder.maxTokens(modelConfig.getMaxTokens());
        }
        return builder.build();
    }
}
