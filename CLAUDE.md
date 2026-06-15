# CLAUDE.md

Monorepo for "Delaware Suite" -- HR/team management platform.

## Projects

| Project | Stack | Port |
|---------|-------|------|
| `web/` | Next.js 16, React 19, Bun, Tailwind 4 | 3000 |
| `api/` | Spring Boot 4, Java 21, MySQL | 8080 |
| `desktop/` | JavaFX 21 | -- |

## Commands

- `make setup` -- copy .env.example, sync env, install all deps
- `make start-all` -- start api + desktop + web in parallel
- `make sync-env` -- grep AUTH_/NEXT_PUBLIC_ from root .env into web/.env.local
- `make init-all` -- install deps for all projects
- `make test-all` -- run Vitest (web) + JUnit 5 (api, desktop)

## Auth

JWT-based. NextAuth.js on frontend, Spring Security on api.

## Database

MySQL. Remote: vichogent.be:41219. Local: docker-compose option.
Connection configured via `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in root `.env`.

## Env

Single root `.env` (copied from `.env.example`). `make sync-env` pushes `AUTH_*` and `NEXT_PUBLIC_*` vars to `web/.env.local`. API reads env vars directly via shell sourcing.

## Tests

- Web: Vitest
- API + Desktop: JUnit 5
- Run: `make test-all`

## Pre-commit

Husky runs lint-staged (web) + mvn test on changed Java projects.
