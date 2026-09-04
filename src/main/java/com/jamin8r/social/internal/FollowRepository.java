package com.jamin8r.social.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface FollowRepository extends JpaRepository<FollowEntity, UUID> {
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
    List<FollowEntity> findByFollowerIdOrderByCreatedAtDesc(UUID followerId);
    List<FollowEntity> findByFollowedIdOrderByCreatedAtDesc(UUID followedId);
}
