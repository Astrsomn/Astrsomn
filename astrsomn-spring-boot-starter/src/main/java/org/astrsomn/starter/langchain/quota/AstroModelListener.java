package org.astrsomn.starter.langchain.quota;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.langchain.stream.DatabaseHistoryRecorder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstroModelListener implements ChatModelListener {
    private final SensitiveWordProvider sensitiveWordProvider;
    private final ModelQuotaManager quotaManager;
    private final DatabaseHistoryRecorder historyRecorder;
    private final Executor taskExecutor;

    public ChatModelListener createBindingListener(AstroChatParam<?> param) {
        // 返回一个极简的匿名实现，内部逻辑引用单例方法
        return new ChatModelListener() {
            @Override
            public void onResponse(ChatModelResponseContext context) {
                doAsyncAuditAndClean(param, context);
            }
            @Override
            public void onRequest(ChatModelRequestContext context) {
                // 1. 输入过滤（同步执行，拦截异常直接中断 AI 请求）
                String userMessage = context.chatRequest().messages().get(0).toString();
                if (containsSensitiveWord(userMessage)) {
                    log.error("====> [Astrsomn] 用户输入命中敏感词拦截: {}", userMessage);
                    throw new RuntimeException("您的输入包含违规内容，请重新组织语言");
                }
            }
        };
    }
    private void doAsyncAuditAndClean(AstroChatParam<?> param, ChatModelResponseContext context) {
        CompletableFuture.runAsync(() -> {
            try {
                TokenUsage usage = context.chatResponse().tokenUsage();
                String rawContent = context.chatResponse().aiMessage().text();


                String cleanedContent = sensitiveWordProvider.filter(rawContent);

                if (usage != null) {
                    // 计费使用原始 Token
                    quotaManager.addUsage(param.getModelKey(), usage.totalTokenCount());
                    // 存档使用清洗后的内容
                    historyRecorder.savePair(param, cleanedContent, usage);
                }
            } catch (Exception e) {
                log.error("====> [Astrsomn] 审计归档失败", e);
            }
        }, taskExecutor);
    }
    private boolean containsSensitiveWord(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (sensitiveWordProvider.checkSensitiveWord(text, i) > 0) return true;
        }
        return false;
    }
}
