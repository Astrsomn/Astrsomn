package com.astrsomn.workflow.core.runtime.spi;

import java.util.Map;

public interface AstFlowDomainEventPublisher {

    void publish(String eventType, Map<String, Object> payload);
}
