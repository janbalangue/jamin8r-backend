package com.jamin8r.profile.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profile")
public class ProfileEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Column(length = 120)
    private String location;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProfileEntity() {
    }

    ProfileEntity(UUID userId, String displayName, String bio, String location, Instant updatedAt) {
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.location = location;
        this.updatedAt = updatedAt;
    }

    void update(String displayName, String bio, String location, Instant updatedAt) {
        this.displayName = displayName;
        this.bio = bio;
        this.location = location;
        this.updatedAt = updatedAt;
    }

    public UUID getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getBio() { return bio; }
    public String getLocation() { return location; }
    public Instant getUpdatedAt() { return updatedAt; }
}
