package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.server.service.AstroChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/astro/chat")
@RequiredArgsConstructor
public class AstroChatController extends BaseController {

    private final AstroChatService astroChatService;


    @PostMapping("/stream")
    public Flux<String> stream(@RequestParam("modelKey") String modelKey,
                               @RequestParam("agentKey") String agentKey,
                               @RequestParam("memoryKey") String memoryKey) {

        return astroChatService.stream(agentKey, modelKey, memoryKey, "");

    }


}
