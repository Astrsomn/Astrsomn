package com.astrsomn.server.event;

import com.astrsomn.system.dto.systemmessage.SystemMessagePushPayload;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;


public class FrontendSystemMessageEvent extends ApplicationEvent {

    public static final String SSE_EVENT_NAME = "SYSTEM_MESSAGE";

    private final SystemMessagePushPayload payload;

    public FrontendSystemMessageEvent(Object source, SystemMessagePushPayload payload) {
        super(source);
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public SystemMessagePushPayload getPayload() {
        return payload;
    }
}
