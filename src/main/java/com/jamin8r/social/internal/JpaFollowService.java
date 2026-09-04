package com.jamin8r.social.internal;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.jamin8r.eventing.OutboxWriter;
import com.jamin8r.identity.UserDirectory;
import com.jamin8r.identity.UserNotFoundException;
import com.jamin8r.social.FollowService;
import com.jamin8r.social.SelfFollowException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
class JpaFollowService implements FollowService {

    private final FollowRepository repository;
    private final UserDirectory users;
    private final OutboxWriter outbox;
    private final Clock clock;
    private final TransactionTemplate transactions;

    JpaFollowService(
            FollowRepository repository,
            UserDirectory users,
            OutboxWriter outbox,
            Clock clock,
            TransactionTemplate transactions) {
        this.repository = repository;
        this.users = users;
        this.outbox = outbox;
        this.clock = clock;
        this.transactions = transactions;
    }

    @Override
    public FollowResult follow(UUID followerId, UUID followedId, UUID correlationId) {
        if (followerId.equals(followedId)) throw new SelfFollowException();
        if (!users.exists(followerId)) throw new UserNotFoundException(followerId);
        if (!users.exists(followedId)) throw new UserNotFoundException(followedId);
        if (repository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            return new FollowResult(false);
        }
        try {
            FollowResult result = transactions.execute(status -> {
                if (repository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
                    return new FollowResult(false);
                }
                Instant now = clock.instant();
                repository.saveAndFlush(new FollowEntity(UUID.randomUUID(), followerId, followedId, now));
                outbox.append(new UserFollowedEvent(
                        UUID.randomUUID(), followerId, now, correlationId,
                        new UserFollowedEvent.UserFollowedPayload(followerId, followedId)));
                return new FollowResult(true);
            });
            return result == null ? new FollowResult(false) : result;
        } catch (DataIntegrityViolationException ex) {
            // A concurrent request may have committed the same unique edge first.
            if (repository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
                return new FollowResult(false);
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> following(UUID userId) {
        ensureUser(userId);
        return repository.findByFollowerIdOrderByCreatedAtDesc(userId).stream()
                .map(FollowEntity::getFollowedId).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> followers(UUID userId) {
        ensureUser(userId);
        return repository.findByFollowedIdOrderByCreatedAtDesc(userId).stream()
                .map(FollowEntity::getFollowerId).toList();
    }

    private void ensureUser(UUID userId) {
        if (!users.exists(userId)) throw new UserNotFoundException(userId);
    }
}
