package com.jamin8r.social.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "follow_edge", uniqueConstraints = @UniqueConstraint(name = "uk_follow_pair", columnNames = {"follower_id", "followed_id"}))
public class FollowEntity {

    @Id
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "followed_id", nullable = false)
    private UUID followedId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected FollowEntity() {
    }

    FollowEntity(UUID id, UUID followerId, UUID followedId, Instant createdAt) {
        this.id = id;
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getFollowerId() { return followerId; }
    public UUID getFollowedId() { return followedId; }
    public Instant getCreatedAt() { return createdAt; }
}
