package com.jamin8r.identity;

import java.util.UUID;

public interface UserRegistrationService {
    UserSnapshot register(String username, UUID correlationId);
}
