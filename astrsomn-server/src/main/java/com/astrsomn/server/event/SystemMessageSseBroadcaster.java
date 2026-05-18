package com.astrsomn.server.event;

import com.astrsomn.system.dto.systemmessage.SystemMessagePushPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Component
public class SystemMessageSseBroadcaster {

    private final List<SseEmitter> channels = new CopyOnWriteArrayList<>();

    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(0L);
        channels.add(emitter);
        Runnable detach = () -> {
            boolean removed = channels.remove(emitter);
            if (removed) {
                log.debug("System message SSE detached, active={}", channels.size());
            }
        };
        emitter.onCompletion(detach);
        emitter.onTimeout(detach);
        emitter.onError(t -> {
            log.debug("System message SSE error, closing: {}", t.toString());
            detach.run();
        });
        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            detach.run();
        }
        return emitter;
    }

    public void broadcastPayload(SystemMessagePushPayload payload) {
        if (payload == null) {
            return;
        }
        for (SseEmitter emitter : channels) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(FrontendSystemMessageEvent.SSE_EVENT_NAME)
                                .data(payload, MediaType.APPLICATION_JSON));
            } catch (Exception ex) {
                log.debug("Dropping system-message SSE (send failed), removing channel: {}", ex.getMessage());
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
                channels.remove(emitter);
            }
        }
    }
}
