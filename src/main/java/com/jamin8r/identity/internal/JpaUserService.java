package com.jamin8r.identity.internal;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.jamin8r.eventing.OutboxWriter;
import com.jamin8r.identity.UserDirectory;
import com.jamin8r.identity.UserNotFoundException;
import com.jamin8r.identity.UserRegistrationService;
import com.jamin8r.identity.UserSnapshot;
import com.jamin8r.identity.UsernameTakenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
class JpaUserService implements UserDirectory, UserRegistrationService {

    private final UserRepository repository;
    private final OutboxWriter outbox;
    private final Clock clock;
    private final TransactionTemplate transactions;

    JpaUserService(UserRepository repository, OutboxWriter outbox, Clock clock, TransactionTemplate transactions) {
        this.repository = repository;
        this.outbox = outbox;
        this.clock = clock;
        this.transactions = transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(UUID userId) {
        return repository.existsById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSnapshot get(UUID userId) {
        return repository.findById(userId).map(JpaUserService::snapshot)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public UserSnapshot register(String rawUsername, UUID correlationId) {
        String username = normalize(rawUsername);
        if (repository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameTakenException(username);
        }
        try {
            UserSnapshot created = transactions.execute(status -> {
                UUID userId = UUID.randomUUID();
                Instant now = clock.instant();
                UserEntity entity = repository.saveAndFlush(new UserEntity(userId, username, now));
                outbox.append(new UserRegisteredEvent(
                        UUID.randomUUID(), userId, now, correlationId,
                        new UserRegisteredEvent.UserRegisteredPayload(userId, username)));
                return snapshot(entity);
            });
            if (created == null) throw new IllegalStateException("User registration transaction returned no result");
            return created;
        } catch (DataIntegrityViolationException ex) {
            if (repository.existsByUsernameIgnoreCase(username)) {
                throw new UsernameTakenException(username);
            }
            throw ex;
        }
    }

    private static String normalize(String rawUsername) {
        if (rawUsername == null) {
            throw new IllegalArgumentException("username is required");
        }
        String username = rawUsername.trim().toLowerCase(Locale.ROOT);
        if (!username.matches("[a-z0-9_]{3,32}")) {
            throw new IllegalArgumentException("username must be 3-32 characters using letters, numbers, or underscore");
        }
        return username;
    }

    private static UserSnapshot snapshot(UserEntity entity) {
        return new UserSnapshot(entity.getId(), entity.getUsername(), entity.getCreatedAt());
    }
}
