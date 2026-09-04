# Testing

The intended release gate is:

```bash
./mvnw verify
```

The checked-in suite currently covers:

- static Spring Modulith module-structure verification;
- full Spring Boot/MockMvc API integration for registration, profile creation/update, follows, duplicate behavior, validation, and durable outbox rows;
- outbox-dispatch success and failure behavior with a mocked publisher.

## Test dependencies

Spring Boot 4 no longer ships MVC test auto-configuration inside `spring-boot-starter-test`. `spring-boot-starter-webmvc-test` is a separate test-scoped dependency and is required for `@AutoConfigureMockMvc` and `org.springframework.boot.webmvc.test.autoconfigure`. Without it the integration test fails to auto-configure `MockMvc` rather than failing an assertion, so the symptom does not point at the missing starter.

## Transaction boundaries in integration tests

`Jamin8rApiIntegrationTest` is deliberately **not** annotated `@Transactional`. Each request runs in its own committed transaction, so outbox-row assertions observe what a real consumer would read after commit.

A test-managed transaction would roll back at the end of the method and would let assertions pass against unflushed state that was never committed — precisely the failure mode the outbox exists to prevent. Per-test isolation comes from `@Sql(executionPhase = BEFORE_TEST_METHOD)` deleting from `outbox_event`, `follow_edge`, `profile`, and `app_user` instead.

Keep new integration tests on the same model: clean up with `@Sql`, not with rollback.

## Test database

The automated Spring integration test profile uses H2 in PostgreSQL compatibility mode so the normal suite does not require Docker. Flyway still owns test schema creation; Hibernate schema generation is disabled in the test profile.

This is useful for fast integration coverage, but it is not a substitute for a real PostgreSQL compatibility gate. Before production deployment, add a CI job against PostgreSQL 18.x (preferably Testcontainers or an ephemeral CI service) and require both suites to pass.

## Offline verification

`scripts/verify-static.sh` validates release metadata/YAML and compiles the dependency-free Java contracts with JDK 21. It exists for constrained/offline environments and does not replace `./mvnw verify`.

See `../VERIFICATION.md` for the verification status of the packaged 0.1.1 source archive.
