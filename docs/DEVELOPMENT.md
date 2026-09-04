# Development

## Toolchain

- JDK 21+
- Maven 3.9.x (bootstrap scripts pin 3.9.16)
- PostgreSQL 18.x for local production-like development

## Run locally

```bash
docker compose up -d
./mvnw spring-boot:run
```

If Maven is already installed, `mvn` can be used instead.

## Database migrations

Flyway scripts live under `src/main/resources/db/migration`. Migrations are append-only after release. Do not edit an already-released migration; create the next versioned migration.

Hibernate schema generation is set to `validate`, so Flyway owns schema changes.

## Versioning

Project versions use Semantic Versioning.

- PATCH: compatible fixes/docs/test improvements.
- MINOR: backward-compatible capabilities or public contract additions.
- MAJOR: incompatible public/API/event-contract changes.

When releasing, update together:

1. `pom.xml` project version
2. `VERSION`
3. `CHANGELOG.md`
4. README current release line
5. any version-specific API/event docs

## Event publishing

The current `LoggingIntegrationEventPublisher` is a local adapter, not production messaging. A future GCP module should implement `IntegrationEventPublisher` with Pub/Sub and disable/replace the logging adapter.
