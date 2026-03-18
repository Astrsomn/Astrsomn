package org.astrsomn.core.common.langchain;

import dev.langchain4j.model.output.TokenUsage;
import org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam;

public interface AstroHistoryRecorder {
    /**
     * 保存一组对话（用户+助手）
     */
    void savePair(AiChatBuildParam param, String assistantContent, TokenUsage usage);
}
