package org.astrsomn.starter.langchain.factory;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;
import org.astrsomn.core.mapper.AiModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiChatModelFactory {


    private final AiModelMapper aiModelMapper;

    public <T> ChatModel getChatModel(AstroChatRequest<T> param) {
        return null;
    }
}
