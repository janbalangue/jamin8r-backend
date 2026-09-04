package com.jamin8r.profile.internal;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import com.jamin8r.eventing.OutboxWriter;
import com.jamin8r.identity.UserDirectory;
import com.jamin8r.identity.UserNotFoundException;
import com.jamin8r.profile.ProfileNotFoundException;
import com.jamin8r.profile.ProfileService;
import com.jamin8r.profile.ProfileSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class JpaProfileService implements ProfileService {

    private final ProfileRepository repository;
    private final UserDirectory users;
    private final OutboxWriter outbox;
    private final Clock clock;

    JpaProfileService(ProfileRepository repository, UserDirectory users, OutboxWriter outbox, Clock clock) {
        this.repository = repository;
        this.users = users;
        this.outbox = outbox;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileSnapshot get(UUID userId) {
        return repository.findById(userId).map(JpaProfileService::snapshot)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
    }

    @Override
    @Transactional
    public ProfileSnapshot upsert(UUID userId, String displayName, String bio, String location, UUID correlationId) {
        if (!users.exists(userId)) {
            throw new UserNotFoundException(userId);
        }
        String name = requireDisplayName(displayName);
        String cleanBio = trimToNull(bio);
        String cleanLocation = trimToNull(location);
        Instant now = clock.instant();
        ProfileEntity profile = repository.findById(userId)
                .map(existing -> {
                    existing.update(name, cleanBio, cleanLocation, now);
                    return existing;
                })
                .orElseGet(() -> new ProfileEntity(userId, name, cleanBio, cleanLocation, now));
        repository.save(profile);
        outbox.append(new ProfileUpdatedEvent(
                UUID.randomUUID(), userId, now, correlationId,
                new ProfileUpdatedEvent.ProfileUpdatedPayload(userId, name, cleanBio, cleanLocation)));
        return snapshot(profile);
    }

    private static String requireDisplayName(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("displayName is required");
        String trimmed = value.trim();
        if (trimmed.length() > 80) throw new IllegalArgumentException("displayName must be at most 80 characters");
        return trimmed;
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static ProfileSnapshot snapshot(ProfileEntity entity) {
        return new ProfileSnapshot(entity.getUserId(), entity.getDisplayName(), entity.getBio(),
                entity.getLocation(), entity.getUpdatedAt());
    }
}
