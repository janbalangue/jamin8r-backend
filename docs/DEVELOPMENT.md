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

## Local database

`compose.yml` runs `postgres:18.6` with:

| Setting | Value |
|---|---|
| Host port | `5433` (container port stays `5432`) |
| Database | `jamin8r` |
| User / password | `jamin8r` / `jamin8r` |
| Data volume mount | `jamin8r_postgres:/var/lib/postgresql` |

The host port is `5433` rather than the default `5432` so the Compose database does not collide with a PostgreSQL instance already installed on the development machine. `.env.example` and the `JAMIN8R_DB_URL` default in `application.yml` both point at `5433`; change all three together if you repoint the port.

The volume is mounted at `/var/lib/postgresql`, not the legacy `/var/lib/postgresql/data`. The PostgreSQL 18 images moved the data directory under a version-specific subdirectory, so mounting the old path leaves the container without a persistent data directory.

Connect with psql:

```bash
psql "postgresql://jamin8r:jamin8r@localhost:5433/jamin8r"
```

A volume created against the legacy mount path is not migrated automatically. If local data predates this change and the container fails to start, remove the stale volume with `docker compose down -v` and let Flyway rebuild the schema.

## Database migrations

Flyway is pulled in through `spring-boot-starter-flyway` so Spring Boot 4 auto-configures it; do not depend on `flyway-core` directly, as that gives the migration classes without the auto-configuration that runs them at startup. `flyway-database-postgresql` is a runtime dependency for the PostgreSQL dialect.

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
6. `VERIFICATION.md` release header
7. `MANIFEST.sha256`, regenerated after all other files are final

Regenerate the manifest and re-run the offline gate with:

```bash
git ls-files | grep -v '^MANIFEST.sha256$' | sed 's|^|./|' | sort \
  | xargs shasum -a 256 > MANIFEST.sha256
./scripts/verify-static.sh
```

The manifest covers every tracked file except itself, so regenerate it last. `verify-static.sh` checks it with `sha256sum -c`/`shasum -c` and fails the offline gate if any tracked file drifted after the manifest was written.

## Event publishing

The current `LoggingIntegrationEventPublisher` is a local adapter, not production messaging. A future GCP module should implement `IntegrationEventPublisher` with Pub/Sub and disable/replace the logging adapter.
