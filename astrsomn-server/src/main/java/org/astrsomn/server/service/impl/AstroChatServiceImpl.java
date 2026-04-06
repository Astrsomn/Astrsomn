package org.astrsomn.server.service.impl;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.AstroChatRequest;
import org.astrsomn.core.common.langchain.AstroChatAssistant;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ConversationSetting;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstroChatErrorEnum;
import org.astrsomn.server.service.AstroChatService;
import org.astrsomn.starter.langchain.factory.AstroAssistantFactory;
import org.astrsomn.starter.langchain.stream.AstroChatStreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service

public class AstroChatServiceImpl implements AstroChatService {

    @Resource
    private  AstroAssistantFactory assistantFactory;

    @Resource
    private  AstroChatStreamUtil chatStreamUtil;


    @Override
    public Flux<String> stream(AstroChatRequest request) {
        if (request == null || request.getAgentKey() == null) {
            throw new BusinessException(AstroChatErrorEnum.CHAT_PARAM_ERROR);
        }
        try {
            AstroChatParam<AstroChatAssistant> param = AstroChatParam.of(AstroChatAssistant.class, request.getAgentKey());
            param.setInstanceKey(request.getInstanceKey());
            param.setMemoryKey(request.getMemoryKey());
            param.setUserMessage(request.getUserMessage());
            param.setConversationSetting(new ConversationSetting()
                    .setEnableNetwork(request.isEnableNetwork())
                    .setEnableStream(true)
                    .setEnableDeepThinking(request.isEnableDeepThinking()));
            AstroChatAssistant chatAssistant = assistantFactory.createAssistant(param);
            return chatStreamUtil.convertStreamToFlux(chatAssistant.stream(request.getUserMessage(), request.getMemoryKey()), param);
        } catch (Exception e) {
            throw new BusinessException(AstroChatErrorEnum.CHAT_PERMISSION_DENIED, e.getMessage());
        }
    }
}
