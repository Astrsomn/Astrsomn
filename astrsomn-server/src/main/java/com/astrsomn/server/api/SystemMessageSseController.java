package com.astrsomn.server.api;

import com.astrsomn.server.event.SystemMessageSseBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 浏览器可建立长连接，接收 {@link com.astrsomn.core.common.dto.systemmessage.SystemMessagePushPayload} 推送。
 * 需携带与接口一致的 Token（如 {@code ?token=…} 或 {@code Authorization: Bearer}）。
 */
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
