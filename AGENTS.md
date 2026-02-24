# Repository Guidelines

## Project Structure & Module Organization
This repository is currently a fresh scaffold with only `.java-version` (Java `25`) committed. As code is added, keep a standard Java layout:
- `src/main/java/` for application code
- `src/test/java/` for unit/integration tests
- `src/main/resources/` for config and static resources
- `docs/` for architecture notes and ADRs

Keep package names lowercase (for example, `com.dynamissky.core`). Group by feature first, then technical layer.

## Build, Test, and Development Commands
No build tool wrapper is committed yet (`pom.xml`, `build.gradle`, `mvnw`, and `gradlew` are not present). When bootstrapping, prefer one tool and commit its wrapper.

Expected commands once initialized:
- `./mvnw clean verify` or `./gradlew clean build`: compile + run all checks
- `./mvnw test` or `./gradlew test`: run test suite only
- `./mvnw spotless:apply` or `./gradlew spotlessApply`: format code (if Spotless is enabled)

## Coding Style & Naming Conventions
Use 4-space indentation and UTF-8 source files. Follow standard Java style:
- Classes: `PascalCase`
- Methods/fields: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Test classes: `<ClassName>Test`

Prefer small, cohesive classes and constructor injection over global/static state.

## Testing Guidelines
Adopt JUnit 5 for unit tests and keep tests deterministic. Mirror production package structure under `src/test/java`.
- Name tests by behavior (example: `createsSessionWhenTokenIsValid`)
- Cover happy path, edge cases, and failure paths
- Add regression tests for every bug fix

Target meaningful coverage on core logic, not just line-count metrics.

## Commit & Pull Request Guidelines
This repository has no commit history yet, so establish conventions now:
- Use Conventional Commits (for example, `feat: add trip search service`)
- Keep commits focused and reviewable
- PRs should include: purpose, key changes, test evidence, and linked issue (`#123`) when applicable
- Include screenshots/log excerpts for UI or behavior changes

## Security & Configuration Tips
Never commit secrets. Use environment variables and provide a sanitized `.env.example` (or equivalent) for required configuration keys.
