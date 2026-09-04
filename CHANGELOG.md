# Changelog

All notable changes to Jamin8r Backend are documented here. Versions follow Semantic Versioning.

## [0.1.1] - 2026-09-04

### Fixed

* Added `spring-boot-starter-webmvc-test` so Spring Boot 4 MVC test auto-configuration is available to integration tests.
* Replaced direct `flyway-core` usage with `spring-boot-starter-flyway` so Flyway migrations run through Spring Boot 4 auto-configuration.
* Corrected integration-test transaction handling so durable outbox events are verified after real transaction commits rather than against an unflushed test transaction.
* Updated the local PostgreSQL Docker configuration for PostgreSQL 18 by mounting `/var/lib/postgresql` instead of the legacy `/var/lib/postgresql/data` path.
* Moved the local Docker PostgreSQL host port to `5433` to avoid collisions with PostgreSQL instances already using the default `5432` port.

## [0.1.0] - 2026-09-04

### Added
- Initial Spring Boot 4.1.1 / Java 21 modular-monolith backend.
- User registration and profile management vertical slice.
- Follow creation with duplicate/self-follow protection.
- Transactional integration-event outbox persisted in PostgreSQL.
- Pluggable outbox dispatcher with a local logging publisher adapter.
- Flyway schema migrations for users, profiles, follows, and outbox events.
- Virtual-thread configuration for request handling.
- Spring Modulith architecture verification.
- API, domain, persistence, and outbox tests.
- Local PostgreSQL Compose setup and development documentation.

### Deferred
- Authentication/authorization.
- GCP Pub/Sub adapter.
- Neo4j graph projection.
- Recommendation/embedding workers.
- Realtime WebSocket/SSE delivery.
- Monorepo/frontend and cloud deployment configuration.
