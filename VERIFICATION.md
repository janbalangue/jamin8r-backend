# Verification status — 0.1.1

Date: 2026-09-04

## Passed in the packaging environment

- Java 21 compilation of all dependency-free domain/event contracts.
- `pom.xml` XML parsing.
- YAML parsing for runtime, test, and Compose configuration.
- Semantic-version consistency across `pom.xml`, `VERSION`, `README.md`, and `CHANGELOG.md`.
- Proprietary license/notice presence.
- Repository file-set and documentation consistency audit.

Reproduce the offline checks with:

```bash
./scripts/verify-static.sh
```

## Full Spring/Maven gate

The intended release gate is:

```bash
./mvnw verify
```

It could not be executed to completion in the packaging environment because that environment could not resolve `repo.maven.apache.org`, so Maven dependencies could not be downloaded. The attempt failed before Maven/Spring compilation began.

Do **not** interpret the offline checks as a substitute for the full Maven test suite. Run `./mvnw verify` in a networked development/CI environment before treating 0.1.1 as a fully build-verified release.
