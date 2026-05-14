package com.astrsomn.api.runtime.common.langchain;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import dev.langchain4j.model.output.TokenUsage;

public interface AstroHistoryRecorder {

    void savePair(AstroChatParam param, String assistantContent, TokenUsage usage);
}