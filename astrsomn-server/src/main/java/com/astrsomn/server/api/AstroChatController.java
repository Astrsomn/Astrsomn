package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BaseController;
import com.astrsomn.core.common.langchain.AstroChatRequest;
import com.astrsomn.server.service.AstroChatService;
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


}
