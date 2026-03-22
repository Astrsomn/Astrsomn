package org.astrsomn.starter.langchain.factory;


import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
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
import org.astrsomn.core.mapper.AiModelMapper;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.astrsomn.core.common.constant.AiModelEnum.ProviderEnum.DEEPSEEK;



@Slf4j
@Component
public class AiStreamModelFactory {
    @Resource
    private AiModelMapper aiModelMapper;


    public StreamingChatModel getStreamingLanguageModel(AiChatBuildParam buildParam) {
        AiModelEntity aiModel = aiModelMapper.selectById(buildParam.getModelId());
        String provider = aiModel.getProvider();
        String modelName = aiModel.getModelName();
        String apiKey = aiModel.getApiKey();
        String apiUrl = aiModel.getApiUrl();
        String apiSecret = aiModel.getApiSecret();
        AiModelEnum.ProviderEnum providerEnum =
                AiModelEnum.ProviderEnum.fromCode(provider);

        if (providerEnum == null) {
            log.error("====> 未找到匹配的 provider: {} <====", provider);
            return getQwenStreamingChatModel(modelName, apiUrl, apiKey);
        }

        switch (providerEnum) {
            case ALIBABA:
                return getQwenStreamingChatModel(modelName, apiUrl, apiKey);
            case OPENAI:
                return getStreamLanguageModel(aiModel, buildParam);
            case DEEPSEEK:
                return getStreamLanguageModel(aiModel, buildParam);
            case ZHIPU:
                return getZhiPuStreamLanguageModel(modelName, apiUrl, apiKey);
            case QIANFAN:
                return getQianfanStreamLanguageModel(modelName, apiUrl, apiKey, apiSecret);
            case GOOGLE:
                return getGoogleGeminiStreamLanguageModel(modelName, apiUrl, apiKey);
            // 可以继续加其他 case
            default:
                log.error("====> 未处理的 provider 类型: {} <====", providerEnum);
                throw new RuntimeException("未知的大模型参数");
        }
    }

    private StreamingChatModel getGoogleGeminiStreamLanguageModel(String modelName, String apiUrl, String apiKey) {
        return GoogleAiGeminiStreamingChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .build();
    }

    private StreamingChatModel getQianfanStreamLanguageModel(String modelName, String apiUrl, String apiKey, String apiSecret) {

        return OpenAiStreamingChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .build();
    }

    private StreamingChatModel getZhiPuStreamLanguageModel(String modelName, String apiUrl, String apiKey) {

        return ZhipuAiStreamingChatModel.builder()
                .model(modelName)
                .apiKey(apiKey)
                .build();
    }


    private StreamingChatModel getQwenStreamingChatModel(String modelName,
                                                         String apiUrl,
                                                         String apiKey) {
        return (StreamingChatModel) QwenStreamingChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .build();
    }


    private OpenAiStreamingChatModel getStreamLanguageModel(AiModelEntity aiModel, AiChatBuildParam buildParam) {



        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
                .modelName(aiModel.getModelName())
                .baseUrl(aiModel.getApiUrl())
                .apiKey(aiModel.getApiKey())
                .maxTokens(buildParam.getMaxToken())
                .logRequests(true)
                .logResponses(true);
//        List<String> capabilities = JSONArray.parseArray(aiModel.getCapabilities(), String.class);
////        List<String> capabilities = Arrays.asList(aiModel.getCapabilities().split(","));
//
//
//        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.DEEP_REASONING.getCode()) && buildParam.isEnableDeepSeek()) {
//            builder.returnThinking(true);
//            // 2. 发送：在多轮对话中，自动把历史消息中的思考内容发回给 API（解决 400 报错）
//            builder.sendThinking(true);
//        }
//
//        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TEMPERATURE_SETTING.getCode())) {
//            builder.temperature(buildParam.getTemperature());
//        }
//        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.TOP_P_SETTING.getCode())) {
//            builder.topP(buildParam.getTopP);
//        }
//        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.SEED_SETTING.getCode())) {
//            builder.seed(buildParam.getSeed());
//        }
//        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.PRESENCE_PENALTY_SETTING.getCode())) {
//            builder.presencePenalty(buildParam.getPresencePenalty());
//        }
//        if (capabilities.contains(AiModelEnum.CapabilitiesEnum.FREQUENCY_PENALTY_SETTING.getCode())) {
//            builder.frequencyPenalty(buildParam.getFrequencyPenalty());
//        }

        return builder.build();
    }


}
