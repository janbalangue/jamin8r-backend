package com.jamin8r.identity.internal;

import java.time.Instant;
import java.util.UUID;

import com.jamin8r.eventing.IntegrationEvent;

record UserRegisteredEvent(
        UUID eventId,
        UUID aggregateId,
        Instant occurredAt,
        UUID correlationId,
        UserRegisteredPayload payload) implements IntegrationEvent {

    @Override public String eventType() { return "user.registered"; }
    @Override public int eventVersion() { return 1; }
    @Override public UUID causationId() { return null; }

    record UserRegisteredPayload(UUID userId, String username) {
    }
}
