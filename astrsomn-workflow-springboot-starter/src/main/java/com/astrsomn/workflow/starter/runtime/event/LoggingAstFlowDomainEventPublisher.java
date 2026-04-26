package com.astrsomn.workflow.starter.runtime.event;

import com.astrsomn.workflow.core.runtime.spi.AstFlowDomainEventPublisher;

import java.util.Map;
import java.util.logging.Logger;

public class LoggingAstFlowDomainEventPublisher implements AstFlowDomainEventPublisher {

    private static final Logger LOGGER = Logger.getLogger(LoggingAstFlowDomainEventPublisher.class.getName());

    @Override
    public void publish(String eventType, Map<String, Object> payload) {
        LOGGER.info(() -> "workflow-domain-event type=" + eventType + ", payload=" + payload);
    }
}
