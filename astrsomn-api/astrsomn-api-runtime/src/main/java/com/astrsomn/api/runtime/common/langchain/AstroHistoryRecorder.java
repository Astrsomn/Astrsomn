package com.astrsomn.api.runtime.common.langchain;

import dev.langchain4j.model.output.TokenUsage;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;

public interface AstroHistoryRecorder {
    
    void savePair(AstroChatParam param, String assistantContent, TokenUsage usage);
}