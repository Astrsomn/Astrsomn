package com.astrsomn.server.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 将应用内发布的 {@link FrontendSystemMessageEvent} 转为 SSE 推送，不包含任何落库。
 */
@Component
@RequiredArgsConstructor
public class SystemMessageFrontendEventListener {

    private final SystemMessageSseBroadcaster systemMessageSseBroadcaster;

    @EventListener
    public void on(FrontendSystemMessageEvent event) {
        systemMessageSseBroadcaster.broadcastPayload(event.getPayload());
    }
}
