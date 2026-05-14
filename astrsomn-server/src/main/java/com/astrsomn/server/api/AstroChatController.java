package com.astrsomn.server.api;

import com.astrsomn.api.runtime.common.langchain.AstroBuilderChatRequest;
import com.astrsomn.api.runtime.common.langchain.AstroChatRequest;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.server.service.AstroChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/astro/chat")
@RequiredArgsConstructor
public class AstroChatController extends BaseController {

    private final AstroChatService astroChatService;


    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody AstroChatRequest request) {

        return astroChatService.stream(request);

    }

    @PostMapping(value = "/builder/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> builderStream(@RequestBody AstroBuilderChatRequest request) {

        return astroChatService.builderStream(request);

    }

}
