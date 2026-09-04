package com.jamin8r.identity.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected UserEntity() {
    }

    UserEntity(UUID id, String username, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public Instant getCreatedAt() { return createdAt; }
}
