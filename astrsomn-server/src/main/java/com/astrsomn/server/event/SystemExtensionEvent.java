package com.astrsomn.server.event;

import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.system.entity.SystemExtensionEntity;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SystemExtensionEvent extends ApplicationEvent {

    private final EventType eventType;
    private final SystemExtensionEntity extension;
    private final List<AiModelEntity> models;
    public SystemExtensionEvent(Object source,
                                EventType eventType,
                                SystemExtensionEntity extension,
                                List<AiModelEntity> models) {
        super(source);
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.extension = extension;
        this.models = models == null ? Collections.emptyList() : Collections.unmodifiableList(models);
    }

    public EventType getEventType() {
        return eventType;
    }

    public SystemExtensionEntity getExtension() {
        return extension;
    }

    public List<AiModelEntity> getModels() {
        return models;
    }

    public enum EventType {
        INSTALLED,
        UNINSTALLED,
        UPDATED
    }
}
