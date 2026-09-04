package com.jamin8r.eventing.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import com.jamin8r.eventing.IntegrationEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class OutboxDispatcherTest {

    private static final Instant NOW = Instant.parse("2026-09-04T18:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void marksSuccessfulPublication() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        IntegrationEventPublisher publisher = mock(IntegrationEventPublisher.class);
        OutboxEventEntity entity = event();
        when(repository.findByPublishedAtIsNullOrderByCreatedAtAsc(any(Pageable.class)))
                .thenReturn(List.of(entity));

        OutboxDispatcher dispatcher = new OutboxDispatcher(repository, publisher, CLOCK, 10);

        assertThat(dispatcher.dispatchBatch()).isEqualTo(1);
        assertThat(entity.getPublishedAt()).isEqualTo(NOW);
        assertThat(entity.getAttempts()).isEqualTo(1);
        assertThat(entity.getLastError()).isNull();
    }

    @Test
    void recordsFailureAndLeavesEventPending() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        IntegrationEventPublisher publisher = mock(IntegrationEventPublisher.class);
        OutboxEventEntity entity = event();
        when(repository.findByPublishedAtIsNullOrderByCreatedAtAsc(any(Pageable.class)))
                .thenReturn(List.of(entity));
        doThrow(new IllegalStateException("broker unavailable")).when(publisher).publish(any());

        OutboxDispatcher dispatcher = new OutboxDispatcher(repository, publisher, CLOCK, 10);

        assertThat(dispatcher.dispatchBatch()).isZero();
        assertThat(entity.getPublishedAt()).isNull();
        assertThat(entity.getAttempts()).isEqualTo(1);
        assertThat(entity.getLastError()).isEqualTo("broker unavailable");
    }

    private static OutboxEventEntity event() {
        UUID eventId = UUID.randomUUID();
        return new OutboxEventEntity(eventId, "social.user-followed", 1, UUID.randomUUID(), NOW,
                UUID.randomUUID(), null, "{}", NOW.minusSeconds(1));
    }
}
