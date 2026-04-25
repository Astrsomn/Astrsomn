package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import jakarta.annotation.Resource;
import com.astrsomn.core.common.langchain.AstroChatAssistant;
import com.astrsomn.core.common.langchain.AstroChatRequest;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.core.common.langchain.buildParam.setting.ConversationSetting;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AstroChatErrorEnum;
import com.astrsomn.server.service.AstroChatService;
import com.astrsomn.starter.langchain.factory.AstroAssistantFactory;
import com.astrsomn.starter.langchain.stream.AstroChatStreamUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.astrsomn.core.common.utils.PageUtils;

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
