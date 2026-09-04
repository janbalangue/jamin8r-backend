package com.jamin8r.profile;

import java.util.UUID;

public interface ProfileService {
    ProfileSnapshot get(UUID userId);
    ProfileSnapshot upsert(UUID userId, String displayName, String bio, String location, UUID correlationId);
}
