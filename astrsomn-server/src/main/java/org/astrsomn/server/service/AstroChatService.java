package org.astrsomn.server.service;

import org.astrsomn.core.common.langchain.AstroChatRequest;
import reactor.core.publisher.Flux;

public interface AstroChatService {

    Flux<String> stream(AstroChatRequest request);
}
