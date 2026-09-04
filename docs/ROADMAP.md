# Backend roadmap

## 0.1.x hardening
- Real PostgreSQL CI gate.
- Additional negative-path and concurrency tests.
- OpenAPI contract generation/validation.
- Observability baseline.

## 0.2.0 identity/security
- OIDC authentication.
- Principal-to-user mapping.
- Ownership and visibility authorization.
- Block/mute primitives.

## 0.3.0 GCP event transport
- Pub/Sub publisher adapter.
- Idempotent subscriber foundation.
- Dead-letter/retry policy.
- Event schema compatibility tests.

## 0.4.0 graph projection
- Neo4j projection of follows and taste edges.
- Rebuildable graph projector.
- Candidate-generation query APIs.

## 0.5.0 recommendation foundation
- User/item feature model.
- Local embedding service integration.
- Candidate generation + ranker boundary.
- Recommendation snapshots and explanation metadata.

Frontend monorepo and Cloud Run deployment work are intentionally deferred from this repository milestone.
