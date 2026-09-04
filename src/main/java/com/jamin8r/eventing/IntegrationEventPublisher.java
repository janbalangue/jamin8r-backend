package com.jamin8r.eventing;

public interface IntegrationEventPublisher {
    void publish(OutboxMessage message);
}
