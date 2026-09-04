package com.jamin8r.eventing.internal;

import java.time.Clock;

import com.jamin8r.eventing.IntegrationEvent;
import com.jamin8r.eventing.OutboxWriter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
class JpaOutboxWriter implements OutboxWriter {

    private final OutboxEventRepository repository;
    private final JsonMapper jsonMapper;
    private final Clock clock;

    JpaOutboxWriter(OutboxEventRepository repository, JsonMapper jsonMapper, Clock clock) {
        this.repository = repository;
        this.jsonMapper = jsonMapper;
        this.clock = clock;
    }

    @Override
    public void append(IntegrationEvent event) {
        repository.save(new OutboxEventEntity(
                event.eventId(),
                event.eventType(),
                event.eventVersion(),
                event.aggregateId(),
                event.occurredAt(),
                event.correlationId(),
                event.causationId(),
                jsonMapper.writeValueAsString(event.payload()),
                clock.instant()));
    }
}
