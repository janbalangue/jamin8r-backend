package com.jamin8r.identity;

import java.time.Instant;
import java.util.UUID;

public record UserSnapshot(UUID id, String username, Instant createdAt) {
}
