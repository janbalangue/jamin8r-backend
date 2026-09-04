# Testing

The intended release gate is:

```bash
./mvnw verify
```

The checked-in suite currently covers:

- static Spring Modulith module-structure verification;
- full Spring Boot/MockMvc API integration for registration, profile creation/update, follows, duplicate behavior, validation, and durable outbox rows;
- outbox-dispatch success and failure behavior with a mocked publisher.

## Test database

The automated Spring integration test profile uses H2 in PostgreSQL compatibility mode so the normal suite does not require Docker. Flyway still owns test schema creation; Hibernate schema generation is disabled in the test profile.

This is useful for fast integration coverage, but it is not a substitute for a real PostgreSQL compatibility gate. Before production deployment, add a CI job against PostgreSQL 18.x (preferably Testcontainers or an ephemeral CI service) and require both suites to pass.

## Offline verification

`scripts/verify-static.sh` validates release metadata/YAML and compiles the dependency-free Java contracts with JDK 21. It exists for constrained/offline environments and does not replace `./mvnw verify`.

See `../VERIFICATION.md` for the verification status of the packaged 0.1.0 source archive.
