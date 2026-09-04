package com.jamin8r.profile.internal;

import java.time.Instant;
import java.util.UUID;

import com.jamin8r.eventing.IntegrationEvent;

record ProfileUpdatedEvent(
        UUID eventId,
        UUID aggregateId,
        Instant occurredAt,
        UUID correlationId,
        ProfileUpdatedPayload payload) implements IntegrationEvent {

    @Override public String eventType() { return "profile.updated"; }
    @Override public int eventVersion() { return 1; }
    @Override public UUID causationId() { return null; }

    record ProfileUpdatedPayload(UUID userId, String displayName, String bio, String location) {
    }
}
