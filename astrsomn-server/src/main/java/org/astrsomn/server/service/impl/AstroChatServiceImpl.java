package org.astrsomn.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.dto.chat.AstroChatRequest;
import org.astrsomn.core.common.langchain.AstroChatAssistant;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.server.service.AstroChatService;
import org.astrsomn.starter.langchain.AstroAssistantFactory;
import org.astrsomn.starter.langchain.stream.AstroChatStreamUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
@RequiredArgsConstructor
public class AstroChatServiceImpl implements AstroChatService {

    private final AstroAssistantFactory assistantFactory;
    private final AstroChatStreamUtil chatStreamUtil;


    @Override
    public Flux<String> stream(AstroChatRequest request) {
        AstroChatParam<AstroChatAssistant> param = AstroChatParam.of(AstroChatAssistant.class, request.getAgentKey());
        param.setModelKey(request.getModelKey());
        param.setMemoryKey(request.getMemoryKey());
        param.setUserMessage(request.getUserMessage());
        param.setChatSetting(new ChatSetting()
                .setEnableNetwork(request.isEnableNetwork())
                .setEnableStream(request.isEnableStream()));
        AstroChatAssistant chatAssistant = assistantFactory.createAssistant(param);
        return chatStreamUtil.convertStreamToFlux(chatAssistant.stream(request.getUserMessage(), request.getMemoryKey()), null);
    }
}
