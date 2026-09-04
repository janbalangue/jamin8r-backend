package com.jamin8r.identity;

import java.util.UUID;

public interface UserDirectory {
    boolean exists(UUID userId);
    UserSnapshot get(UUID userId);
}
