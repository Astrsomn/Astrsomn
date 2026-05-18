package com.astrsomn.server.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SystemMessageFrontendEventListener {

    private final SystemMessageSseBroadcaster systemMessageSseBroadcaster;

    @EventListener
    public void on(FrontendSystemMessageEvent event) {
        systemMessageSseBroadcaster.broadcastPayload(event.getPayload());
    }
}
