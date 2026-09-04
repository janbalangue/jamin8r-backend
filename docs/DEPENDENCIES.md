# Dependency baseline

Version review date: 2026-09-04.

| Component | Version/policy | Rationale |
|---|---|---|
| Java | 21 minimum | LTS baseline available in the build environment; supports virtual threads. |
| Spring Boot | 4.1.1 | Current stable Spring Boot release at review time. |
| Spring Modulith | 2.1.1 | Current stable Spring Modulith line compatible with the Boot 4.1 generation. |
| PostgreSQL | 18.x; local Compose uses 18.6 | PostgreSQL 18 is the current stable major; 18.6 is the current maintenance release at review time. |
| Maven | 3.9.16 | Current stable Maven release; custom bootstrap scripts pin it. |
| Maven Enforcer Plugin | 3.6.3 | Current stable plugin release at review time. |
| Hibernate, Jackson 3, PostgreSQL JDBC, Flyway | Spring Boot BOM managed | Avoid independent pins that can drift from Boot's tested dependency set. |

## Update policy

For each Jamin8r release:

1. review Spring Boot stable releases;
2. review the compatible stable Spring Modulith line;
3. update Maven/bootstrap tooling when a newer stable release is appropriate;
4. update the PostgreSQL local-dev image to a supported maintenance release;
5. run the complete `./mvnw verify` gate after dependency changes;
6. update this file, `CHANGELOG.md`, and any affected setup docs.

Do not move to preview, milestone, beta, RC, or snapshot dependencies in a stable Jamin8r release without an explicit reason documented in the changelog.
