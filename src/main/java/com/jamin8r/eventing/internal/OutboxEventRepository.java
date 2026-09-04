package com.jamin8r.eventing.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findByPublishedAtIsNullOrderByCreatedAtAsc(Pageable pageable);
    long countByPublishedAtIsNull();
}
