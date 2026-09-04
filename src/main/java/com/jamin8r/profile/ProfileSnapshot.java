package com.jamin8r.profile;

import java.time.Instant;
import java.util.UUID;

public record ProfileSnapshot(
        UUID userId,
        String displayName,
        String bio,
        String location,
        Instant updatedAt) {
}
