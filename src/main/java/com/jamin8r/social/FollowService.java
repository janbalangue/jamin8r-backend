package com.jamin8r.social;

import java.util.List;
import java.util.UUID;

public interface FollowService {
    FollowResult follow(UUID followerId, UUID followedId, UUID correlationId);
    List<UUID> following(UUID userId);
    List<UUID> followers(UUID userId);

    record FollowResult(boolean created) {
    }
}
