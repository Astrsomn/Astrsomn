package com.astrsomn.server.service;

import com.astrsomn.core.common.langchain.AstroChatRequest;
import reactor.core.publisher.Flux;

public interface AstroChatService {

    Flux<String> stream(AstroChatRequest request);
}
