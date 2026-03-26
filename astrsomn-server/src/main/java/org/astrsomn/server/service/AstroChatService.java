package org.astrsomn.server.service;

import reactor.core.publisher.Flux;

public interface AstroChatService {


    Flux<String> stream(String agentKey,
                        String modelKey,
                        String memoryKey,
                        String userMessage);



}
