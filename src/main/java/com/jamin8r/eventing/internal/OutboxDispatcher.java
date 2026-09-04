package com.jamin8r.eventing.internal;

import java.time.Clock;
import java.util.List;

import com.jamin8r.eventing.IntegrationEventPublisher;
import com.jamin8r.eventing.OutboxMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(prefix = "jamin8r.outbox.dispatch", name = "enabled", havingValue = "true")
public class OutboxDispatcher {

    private final OutboxEventRepository repository;
    private final IntegrationEventPublisher publisher;
    private final Clock clock;
    private final int batchSize;

    public OutboxDispatcher(
            OutboxEventRepository repository,
            IntegrationEventPublisher publisher,
            Clock clock,
            @Value("${jamin8r.outbox.dispatch.batch-size:50}") int batchSize) {
        this.repository = repository;
        this.publisher = publisher;
        this.clock = clock;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${jamin8r.outbox.dispatch.fixed-delay-ms:1000}")
    @Transactional
    public int dispatchBatch() {
        List<OutboxEventEntity> events = repository.findByPublishedAtIsNullOrderByCreatedAtAsc(
                PageRequest.of(0, batchSize));
        int published = 0;
        for (OutboxEventEntity entity : events) {
            var v = entity.toView();
            try {
                publisher.publish(new OutboxMessage(v.eventId(), v.eventType(), v.eventVersion(),
                        v.aggregateId(), v.occurredAt(), v.correlationId(), v.causationId(), v.payload()));
                entity.markPublished(clock.instant());
                published++;
            } catch (RuntimeException ex) {
                entity.markFailed(ex.getMessage());
            }
        }
        return published;
    }
}
