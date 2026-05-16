package com.astrsomn.server.service.impl;

import cn.hutool.core.lang.UUID;
import com.astrsomn.api.runtime.common.langchain.AstroBuilderChatRequest;
import com.astrsomn.api.runtime.common.langchain.AstroChatAssistant;
import com.astrsomn.api.runtime.common.langchain.AstroChatRequest;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.*;
import com.astrsomn.api.runtime.exception.AstroChatErrorEnum;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.AstroChatService;
import com.astrsomn.starter.runtime.langchain.factory.AstroAssistantFactory;
import com.astrsomn.starter.runtime.langchain.stream.AstroChatStreamUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service

public class AstroChatServiceImpl implements AstroChatService {

    @Resource
    private AstroAssistantFactory assistantFactory;

    @Resource
    private AstroChatStreamUtil chatStreamUtil;


    @Override
    public Flux<String> stream(AstroChatRequest request) {
        if (request == null || request.getBizKey() == null) {
            throw new BusinessException(AstroChatErrorEnum.CHAT_PARAM_ERROR);
        }
        try {
            AstroChatParam<AstroChatAssistant> param = AstroChatParam.of(AstroChatAssistant.class, request.getBizKey());
            param.setInstanceKey(request.getInstanceKey());
            param.setMemoryKey(request.getMemoryKey());
            param.setUserMessageText(request.getUserMessage());
            param.setFileUrlList(request.getFileUrlList());
            param.setConversationSetting(new ConversationSetting()
                    .setEnableNetwork(request.isEnableNetwork())
                    .setEnableStream(true)
                    .setEnableDeepThinking(request.isEnableDeepThinking()));
            AstroChatAssistant chatAssistant = assistantFactory.createAssistant(param);

            if (param.getUserMessage() == null) {
                throw new BusinessException(AstroChatErrorEnum.CHAT_PARAM_ERROR);
            }

            return chatStreamUtil.convertStreamToFlux(
                    chatAssistant.stream(param.getUserMessage(), param.getMemoryKey()), param);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(AstroChatErrorEnum.CHAT_PERMISSION_DENIED, e.getMessage());
        }
    }

    @Override
    public Flux<String> builderStream(AstroBuilderChatRequest request) {
        if (request == null || StringUtils.isBlank(request.getUserMessage())) {
            throw new BusinessException(AstroChatErrorEnum.CHAT_PARAM_ERROR);
        }

        String memoryKey = StringUtils.isNotBlank(request.getMemoryKey())
                ? request.getMemoryKey()
                : UUID.fastUUID().toString();

        ConversationSetting conversationSetting = Optional.ofNullable(request.getConversationSetting())
                .orElse(new ConversationSetting());
        conversationSetting.setEnableStream(true);

        AstroChatParam<AstroChatAssistant> param = AstroChatParam.<AstroChatAssistant>builder()
                .serviceClass(AstroChatAssistant.class)
                .bizKey("builder-playground")
                .memoryKey(memoryKey)
                .userMessageText(request.getUserMessage())
                .modelSetting(Optional.ofNullable(request.getModelSetting()).orElse(new ModelSetting()))
                .chatSetting(Optional.ofNullable(request.getChatSetting()).orElse(new ChatSetting()))
                .promptSetting(Optional.ofNullable(request.getPromptSetting()).orElse(new PromptSetting()))
                .conversationSetting(conversationSetting)
                .toolSetting(Optional.ofNullable(request.getToolSetting()).orElse(new ToolSetting()))
                .ragSetting(Optional.ofNullable(request.getRagSetting()).orElse(new RagSetting()))
                .maxHistoryMessages(Optional.ofNullable(request.getMaxHistoryMessages()).orElse(10))
                .enableHistorySave(request.isEnableHistorySave())
                .build();


        try {
            AstroChatAssistant chatAssistant = assistantFactory.createAssistantDirect(param);
            return chatStreamUtil.convertStreamToFlux(
                    chatAssistant.stream(param.getUserMessage(), memoryKey), param);
        } catch (Exception e) {
            throw new BusinessException(AstroChatErrorEnum.CHAT_PERMISSION_DENIED, e.getMessage());
        }
    }
}
