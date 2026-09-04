# Security status

0.1.1 is a backend foundation, not a production-ready authenticated service.

## Explicitly deferred

- authentication provider / OIDC integration;
- authorization policy;
- ownership checks derived from authenticated identity;
- abuse/rate limiting;
- CSRF/CORS policy for the eventual frontend origins;
- secret manager integration;
- audit/security event stream.

Current user IDs supplied in requests are development scaffolding. A client must never be trusted to assert its own identity in production.

Before any public deployment, add Spring Security and map the authenticated principal to the internal user ID server-side.
