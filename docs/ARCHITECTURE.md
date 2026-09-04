# Architecture

## Decision summary

Jamin8r begins as a modular monolith rather than a collection of microservices. The goal is to preserve strong domain boundaries while keeping transactions, local development, debugging, and deployment simple during product discovery.

PostgreSQL is the source of truth. Neo4j is planned as a derived graph read model once graph traversal becomes product-critical. Recommendation/embedding services are planned as asynchronous consumers of durable integration events.

## Module boundaries

- `identity`: user lifecycle and canonical username.
- `profile`: public profile fields owned by a user.
- `social`: follows and eventually friendships/blocks.
- `eventing`: durable integration-event outbox and publisher boundary.
- `shared`: transport-neutral shared primitives only; keep this small.

No module should directly update another module's persistence tables. Cross-domain side effects should increasingly be modeled as events.

## Command flow

Example: follow a user.

```text
POST /v1/follows
      |
      v
FollowUserHandler
      |
      +-- validate user existence
      +-- reject self-follow
      +-- idempotently reject duplicate edge
      +-- insert follows row
      +-- insert outbox row
      |
    COMMIT
      |
     202
```

The follow edge is durable when 202 is returned. Downstream graph/recommendation/notification work is not required to finish before the response.

## Transactional outbox

Database mutation and event intent are committed atomically. The application never does a best-effort `save(); publish();` sequence for durable business events.

```text
transaction
  INSERT/UPDATE domain state
  INSERT outbox_event
commit

later
  dispatcher -> publisher -> broker
```

Consumers must be idempotent because the eventual Pub/Sub delivery model will be at-least-once.

## Concurrency

Spring MVC runs with virtual threads enabled, and `spring.main.keep-alive=true` prevents a virtual-thread-only scheduler/runtime from allowing the JVM to exit unexpectedly. This reduces the cost of blocking I/O but does not remove downstream limits. Database pools, graph pools, inference workers, and external APIs will each require explicit concurrency budgets as the application grows.

The current release deliberately does not invent those limits before the corresponding workloads exist. Username creation and follow creation already rely on database uniqueness plus transaction-bound retry interpretation so concurrent duplicate requests remain deterministic.

## Planned GCP shape

Later phases are expected to add:

```text
Cloud Run API
   |
Cloud SQL PostgreSQL
   |
outbox -> Pub/Sub
           |
           +-> graph projector -> Neo4j
           +-> recommendation workers
           +-> embeddings/inference
           +-> notification worker
```

Cloud deployment is intentionally absent from 0.1.0.
