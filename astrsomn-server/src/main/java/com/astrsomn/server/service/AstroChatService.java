package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.langchain.AstroBuilderChatRequest;
import com.astrsomn.api.runtime.common.langchain.AstroChatRequest;
import reactor.core.publisher.Flux;

public interface AstroChatService {

    Flux<String> stream(AstroChatRequest request);

    Flux<String> builderStream(AstroBuilderChatRequest request);
}
