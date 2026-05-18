package com.astrsomn.server.event;

import com.astrsomn.system.dto.systemmessage.SystemMessagePushPayload;
import com.astrsomn.system.dto.systemmessage.SystemMessageRecordCommand;
import com.astrsomn.system.entity.SystemMessageEntity;
import com.astrsomn.starter.runtime.system.message.SystemMessageRecorder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class SystemMessageEventCoordinator {

    private final SystemMessageRecorder systemMessageRecorder;
    private final ApplicationEventPublisher eventPublisher;

    public SystemMessageEntity recordOnly(SystemMessageRecordCommand command) {
        return systemMessageRecorder.record(Objects.requireNonNull(command, "command"));
    }

    public void pushFromEntity(SystemMessageEntity entity) {
        SystemMessagePushPayload payload = SystemMessagePushPayload.fromEntity(Objects.requireNonNull(entity, "entity"));
        pushOnly(Objects.requireNonNull(payload, "payload"));
    }

    public void pushOnly(SystemMessagePushPayload payload) {
        eventPublisher.publishEvent(new FrontendSystemMessageEvent(this, Objects.requireNonNull(payload, "payload")));
    }

    public SystemMessageEntity recordAndPush(SystemMessageRecordCommand command) {
        SystemMessageEntity row = recordOnly(command);
        pushFromEntity(row);
        return row;
    }
}
