# Dependency baseline

Version review date: 2026-09-04 (unchanged for 0.1.1; that release corrected how existing dependencies are declared rather than moving any version).

| Component | Version/policy | Rationale |
|---|---|---|
| Java | 21 minimum | LTS baseline available in the build environment; supports virtual threads. |
| Spring Boot | 4.1.1 | Current stable Spring Boot release at review time. |
| Spring Modulith | 2.1.1 | Current stable Spring Modulith line compatible with the Boot 4.1 generation. |
| PostgreSQL | 18.x; local Compose uses 18.6 | PostgreSQL 18 is the current stable major; 18.6 is the current maintenance release at review time. The 18 images require the data volume to be mounted at `/var/lib/postgresql`. |
| Maven | 3.9.16 | Current stable Maven release; custom bootstrap scripts pin it. |
| Maven Enforcer Plugin | 3.6.3 | Current stable plugin release at review time. |
| Hibernate, Jackson 3, PostgreSQL JDBC, Flyway | Spring Boot BOM managed | Avoid independent pins that can drift from Boot's tested dependency set. |

## Spring Boot 4 starter requirements

Spring Boot 4 splits functionality out of the umbrella starters that carried it in Boot 3. Two declarations exist for that reason and must not be "simplified" away:

| Declaration | Scope | Why it is required |
|---|---|---|
| `spring-boot-starter-flyway` | compile | Provides Flyway auto-configuration. Depending on `flyway-core` alone supplies the migration classes but no auto-configuration, so migrations never run at startup. `flyway-database-postgresql` remains a separate runtime dependency for the PostgreSQL dialect. |
| `spring-boot-starter-webmvc-test` | test | Provides MVC test auto-configuration (`@AutoConfigureMockMvc`, `org.springframework.boot.webmvc.test.autoconfigure`), which `spring-boot-starter-test` no longer contains. |

## Update policy

For each Jamin8r release:

1. review Spring Boot stable releases;
2. review the compatible stable Spring Modulith line;
3. update Maven/bootstrap tooling when a newer stable release is appropriate;
4. update the PostgreSQL local-dev image to a supported maintenance release;
5. re-check whether a Boot upgrade has moved auto-configuration into or out of a starter, and adjust the declarations above;
6. run the complete `./mvnw verify` gate after dependency changes;
7. update this file, `CHANGELOG.md`, and any affected setup docs.

Do not move to preview, milestone, beta, RC, or snapshot dependencies in a stable Jamin8r release without an explicit reason documented in the changelog.
