package com.jamin8r.eventing;

import java.time.Instant;
import java.util.UUID;

public interface IntegrationEvent {
    UUID eventId();
    String eventType();
    int eventVersion();
    UUID aggregateId();
    Instant occurredAt();
    UUID correlationId();
    UUID causationId();
    Object payload();
}
