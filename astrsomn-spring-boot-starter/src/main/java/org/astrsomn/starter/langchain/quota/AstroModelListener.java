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

    private final ModelQuotaManager quotaManager;
    private final DatabaseHistoryRecorder historyRecorder;


    public ChatModelListener createBindingListener(AstroChatParam<?> param) {
        // 返回一个极简的匿名实现，内部逻辑引用单例方法
        return new ChatModelListener() {
            @Override
            public void onResponse(ChatModelResponseContext context) {
                doAsyncAudit(param, context);
            }
        };
    }
    private void doAsyncAudit(AstroChatParam<?> param, ChatModelResponseContext context) {
        CompletableFuture.runAsync(() -> {
            try {
                TokenUsage usage = context.chatResponse().tokenUsage();
                String content = context.chatResponse().aiMessage().text();
                if (usage != null) {
                    quotaManager.addUsage(param.getModelKey(), usage.totalTokenCount());
                    historyRecorder.savePair(param, content, usage);
                }
            } catch (Exception e) {
                log.error("====> [Astrsomn] 审计归档失败", e);
            }
        });
    }
}
