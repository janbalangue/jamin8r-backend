package com.jamin8r.social.internal;

import java.time.Instant;
import java.util.UUID;

import com.jamin8r.eventing.IntegrationEvent;

record UserFollowedEvent(
        UUID eventId,
        UUID aggregateId,
        Instant occurredAt,
        UUID correlationId,
        UserFollowedPayload payload) implements IntegrationEvent {

    @Override public String eventType() { return "social.user-followed"; }
    @Override public int eventVersion() { return 1; }
    @Override public UUID causationId() { return null; }

    record UserFollowedPayload(UUID followerId, UUID followedId) {
    }
}
