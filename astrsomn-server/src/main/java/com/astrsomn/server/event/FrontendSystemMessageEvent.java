package com.astrsomn.server.event;

import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessagePushPayload;
import com.astrsomn.starter.runtime.message.SystemMessageRecorder;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * 仅用于将「已构造好的系统消息视图」分发给监听器，由监听器经 SSE 推给浏览器。
 * 本事件<strong>不</strong>写库；落库请用 {@link SystemMessageRecorder} 或
 * {@link com.astrsomn.server.service.SystemMessageService}，组合入口见
 * {@link com.astrsomn.server.event.SystemMessageEventCoordinator}。
 */
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
