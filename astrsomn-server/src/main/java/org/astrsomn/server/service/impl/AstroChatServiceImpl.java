package org.astrsomn.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.AstroChatAssistant;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.server.service.AstroChatService;
import org.astrsomn.starter.langchain.AstroAssistantFactory;
import org.astrsomn.starter.langchain.stream.AstroChatStreamUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
@RequiredArgsConstructor
public class AstroChatServiceImpl implements AstroChatService {

    private AstroAssistantFactory assistantFactory;

    private AstroChatStreamUtil chatStreamUtil;


    @Override
    public Flux<String> stream(String agentKey,
                               String modelKey,
                               String memoryKey,
                               String userMessage) {

        AstroChatParam<AstroChatAssistant> param = AstroChatParam.of(AstroChatAssistant.class, agentKey);
        param.setModelKey(modelKey);
        param.setMemoryKey(memoryKey);
        AstroChatAssistant chatAssistant = assistantFactory.createAssistant(param);
        return chatStreamUtil.convertStreamToFlux(chatAssistant.stream(userMessage, memoryKey), null);
    }


}
