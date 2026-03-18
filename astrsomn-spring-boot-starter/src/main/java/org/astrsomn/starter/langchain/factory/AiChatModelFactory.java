package org.astrsomn.starter.langchain.factory;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam;
import org.astrsomn.core.mapper.AiModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiChatModelFactory {

    @Resource
    private AiModelMapper aiModelMapper;





    private OpenAiChatModel getOpenAiLanguageModel(String modelName,
                                                   String apiUrl,
                                                   String apiKey) {
        OpenAiChatModel openAiLanguageModel = OpenAiChatModel.builder()
                .modelName(modelName)
                .baseUrl(apiUrl)
                .apiKey(apiKey)
                .logRequests(true)
                .logResponses(true)
                .build();
        return openAiLanguageModel;
    }


    public ChatModel getLanguageModel(AiChatBuildParam buildParam) {
//        AiModelConfigEntity aiModelConfig = aiModelMapper.selectOne(buildParam.getModelId());
//        String provider = aiModelConfig.getProvider();
//        String modelName = aiModelConfig.getModelName();
//        String apiKey = aiModelConfig.getApiKey();
//        String apiUrl = aiModelConfig.getApiUrl();
//        String apiSecret = aiModelConfig.getApiSecret();
//        AiModelEnum.ProviderEnum providerEnum =
//                AiModelEnum.ProviderEnum.fromCode(provider);
//
//
//        if (providerEnum == null) {
//            log.error("====> 未找到匹配的 provider: {} <====", provider);
//            throw new RuntimeException("====> 未找到匹配的 provider  <====");
//        }
//        switch (providerEnum) {
//            case DEEPSEEK:
//                return getOpenAiLanguageModel(modelName, apiUrl, apiKey);
//        }
        throw new RuntimeException("不支持的模型类型");
    }
}
