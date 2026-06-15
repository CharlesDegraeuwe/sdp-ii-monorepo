# Delaware Suite Monorepo

HR and team management platform built as a monorepo with three projects.

## Architecture

| Project | Stack | Port |
|---------|-------|------|
| **web** | Next.js 16, React 19, Bun, Tailwind 4 | 3000 |
| **api** | Spring Boot 4, Java 21, MySQL | 8080 |
| **desktop** | JavaFX 21 | -- |

## Quickstart

### Prerequisites

- Java 21
- Bun
- Maven

### Setup

```bash
make setup        # copies .env.example -> .env, syncs env, installs deps
```

Fill in the secrets in `.env` (DB password, JWT secret, API keys).

### Run

```bash
make start-all    # starts api, desktop, and web in parallel
```

Or start individually: `make start-web`, `make start-api`, `make start-desktop`.

## Make Commands

| Command | Description |
|---------|-------------|
| `make setup` | Full first-time setup (env + deps) |
| `make sync-env` | Push `AUTH_*`/`NEXT_PUBLIC_*` from `.env` to `web/.env.local` |
| `make init-all` | Install dependencies for all projects |
| `make init-web` | Install web dependencies (`bun install`) |
| `make init-api` | Resolve API Maven dependencies |
| `make init-desktop` | Resolve desktop Maven dependencies |
| `make start-all` | Start all services in parallel |
| `make start-web` | Start Next.js dev server |
| `make start-api` | Start Spring Boot API |
| `make start-desktop` | Start JavaFX desktop app |
| `make test-all` | Run all tests (Vitest + JUnit) |
| `make test-web` | Run web tests |
| `make test-api` | Run API tests |
| `make test-desktop` | Run desktop tests |

## Project Structure

```
.
├── api/            # Spring Boot REST API
├── desktop/        # JavaFX desktop client
├── web/            # Next.js frontend
├── .env.example    # Template for environment variables
├── .env            # Local env (not committed)
└── Makefile        # All build/run commands
```

## Local Database (optioneel)

```bash
docker compose up -d   # start MySQL op localhost:3306
```

Pas `DB_URL` in `.env` aan naar `jdbc:mysql://localhost:3306/SDP2_2526_DB_G01?serverTimezone=UTC` om lokale DB te gebruiken.

## Environment

A single root `.env` file holds all config. `make sync-env` extracts web-relevant vars (`AUTH_*`, `NEXT_PUBLIC_*`) into `web/.env.local`. The API reads env vars directly at startup.
