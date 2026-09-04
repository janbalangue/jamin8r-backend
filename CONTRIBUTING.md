# Internal contribution guide

This is a private proprietary repository.

Before merging:

```bash
./mvnw verify
```

Changes that affect a public HTTP contract or integration-event contract must update the corresponding docs and tests. Release changes must update `pom.xml`, `VERSION`, `CHANGELOG.md`, and the README release line together.
