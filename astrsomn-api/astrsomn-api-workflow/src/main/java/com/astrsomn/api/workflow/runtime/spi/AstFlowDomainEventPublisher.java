package com.astrsomn.api.workflow.runtime.spi;

import java.util.Map;

public interface AstFlowDomainEventPublisher {

    void publish(String eventType, Map<String, Object> payload);
}