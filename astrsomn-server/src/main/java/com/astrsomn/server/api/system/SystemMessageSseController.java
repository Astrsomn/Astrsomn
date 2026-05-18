package com.astrsomn.server.api.system;

import com.astrsomn.system.dto.systemmessage.SystemMessagePushPayload;
import com.astrsomn.server.event.SystemMessageSseBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/v1/astro/sse")
@RequiredArgsConstructor
public class SystemMessageSseController {

    private final SystemMessageSseBroadcaster systemMessageSseBroadcaster;

    @GetMapping(value = "/system-message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        return systemMessageSseBroadcaster.connect();
    }
}
