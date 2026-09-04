package com.jamin8r.eventing.internal;

import com.jamin8r.eventing.IntegrationEventPublisher;
import com.jamin8r.eventing.OutboxMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(IntegrationEventPublisher.class)
class LoggingIntegrationEventPublisher implements IntegrationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingIntegrationEventPublisher.class);

    @Override
    public void publish(OutboxMessage message) {
        log.info("Local outbox publish eventId={} type={} version={}",
                message.eventId(), message.eventType(), message.eventVersion());
    }
}
