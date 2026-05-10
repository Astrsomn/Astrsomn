package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 将接入层文本与文件 URL 组装为 LangChain4j {@link UserMessage}；若 {@code param.userMessage} 已存在则跳过。
 */
@Component
@Order(15)
public class ResolveUserMessageChainHandler implements AgentRuntimeChainHandler {

    @Override
    public void handle(AgentRuntimeContext ctx) {
        resolveUserMessage(ctx.getParam());
    }

    /**
     * 供跳过责任链的场景（如 {@code createAssistantDirect}）在调用前显式组装。
     */
    public static <T> void resolveUserMessage(AstroChatParam<T> param) {
        if (param == null || param.getUserMessage() != null) {
            return;
        }
        UserMessage built = assembleFromTextAndUrls(param.getUserMessageText(), param.getFileUrlList());
        param.setUserMessage(built);
    }

    /**
     * 纯文本 + URL 列表 → 多模态 {@link UserMessage}；无任何有效输入时返回 {@code null}。
     */
    public static UserMessage assembleFromTextAndUrls(String userMessageText, List<String> fileUrlList) {
        String text = StringUtils.trimToNull(userMessageText);
        List<Content> parts = new ArrayList<>();
        if (text != null) {
            parts.add(TextContent.from(text));
        }
        if (fileUrlList != null) {
            for (String url : fileUrlList) {
                String trimmed = StringUtils.trimToNull(url);
                if (trimmed != null) {
                    parts.add(ImageContent.from(trimmed));
                }
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        if (parts.size() == 1 && parts.get(0) instanceof TextContent) {
            return UserMessage.from(text);
        }
        return UserMessage.from(parts);
    }
}
