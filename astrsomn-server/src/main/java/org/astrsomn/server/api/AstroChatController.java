package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.langchain.AstroChatRequest;
import org.astrsomn.server.service.AstroChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
