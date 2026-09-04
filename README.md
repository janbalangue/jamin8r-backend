# Jamin8r Backend

Private, proprietary backend for Jamin8r, a profile-first social application focused on identity, music, interests, discovery, and eventually an in-house recommendation stack.

Current release: **0.1.0** (2026-09-04).

## Technology baseline

- Java 21
- Spring Boot 4.1.1
- Spring Modulith 2.1.1
- Spring MVC + virtual threads
- PostgreSQL 18.x
- Flyway
- Maven 3.9.16 bootstrap scripts

The application starts as a modular monolith. PostgreSQL is the transactional source of truth. Durable integration events are written to an outbox in the same database transaction as domain changes. A future GCP Pub/Sub adapter will publish those events to graph, recommendation, notification, and other workers.

## Current vertical slice

0.1.0 implements:

- create a user;
- create or update that user's profile;
- read a user/profile;
- update profile fields;
- follow another user;
- list following/followers;
- atomically persist `user.registered` v1, `profile.updated` v1, and `social.user-followed` v1 integration events into the outbox;
- dispatch pending outbox events through a pluggable publisher port (local logging adapter only for now).

Authentication is intentionally **not implemented yet**. The current IDs in request paths/bodies are development scaffolding and must not be treated as authorization boundaries. See `docs/SECURITY.md`.

## Quick start

Requirements: JDK 21+, Docker/Podman for local PostgreSQL, and either Maven 3.9.x or the included bootstrap script.

```bash
docker compose up -d
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`.

Health:

```bash
curl http://localhost:8080/actuator/health
```

Create a user:

```bash
curl -i -X POST http://localhost:8080/v1/users \
  -H 'Content-Type: application/json' \
  -d '{"username":"jay"}'
```

Create or update the profile (using the UUID returned above):

```bash
curl -i -X PUT http://localhost:8080/v1/users/<uuid>/profile \
  -H 'Content-Type: application/json' \
  -d '{"displayName":"Jay","bio":"music + systems","location":"California"}'
```

Follow another user:

```bash
curl -i -X POST http://localhost:8080/v1/follows \
  -H 'Content-Type: application/json' \
  -H 'X-Correlation-Id: 9e9e2438-660a-46de-a9ff-3c532ebd4c6a' \
  -d '{"followerId":"<uuid>","followedId":"<uuid>"}'
```

## Test

```bash
./mvnw verify
```

The test profile uses H2 in PostgreSQL compatibility mode so the suite does not require Docker. Production migrations target PostgreSQL-compatible SQL. See `docs/TESTING.md` for the current limits and the planned real-PostgreSQL CI gate.

## Architecture

```text
HTTP / Spring MVC
       |
       v
application handlers
       |
       +---- domain model / invariants
       |
       +---- JPA repositories ----> PostgreSQL
       |
       +---- outbox writer --------> PostgreSQL
                                      |
                                      v
                              outbox dispatcher
                                      |
                                      v
                         IntegrationEventPublisher
                                      |
                           local: log only
                           later: GCP Pub/Sub
```

Modules are organized by domain rather than by global technical layer:

```text
com.jamin8r
  identity/
  profile/
  social/
  eventing/
  websupport/
```

See `docs/ARCHITECTURE.md`, `docs/API.md`, `docs/EVENTS.md`, and `docs/DEPENDENCIES.md`.

## Configuration

The default runtime expects PostgreSQL:

| Variable | Default |
|---|---|
| `JAMIN8R_DB_URL` | `jdbc:postgresql://localhost:5432/jamin8r` |
| `JAMIN8R_DB_USERNAME` | `jamin8r` |
| `JAMIN8R_DB_PASSWORD` | `jamin8r` |
| `JAMIN8R_OUTBOX_DISPATCH_ENABLED` | `false` |

Outbox dispatch is disabled by default until an external broker adapter exists. Enable it locally only to exercise the logging publisher:

```bash
JAMIN8R_OUTBOX_DISPATCH_ENABLED=true ./mvnw spring-boot:run
```

## Proprietary status

This repository is private and proprietary. See `LICENSE` and `NOTICE.md`. Do not publish it to a public package registry or public source host.
