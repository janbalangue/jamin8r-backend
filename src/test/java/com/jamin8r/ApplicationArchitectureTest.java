package com.jamin8r;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ApplicationArchitectureTest {

    @Test
    void moduleStructureIsValid() {
        ApplicationModules.of(Jamin8rApplication.class).verify();
    }
}
