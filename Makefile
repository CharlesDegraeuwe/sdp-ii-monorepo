.PHONY: init-web init-desktop init-api init-all start-web start-desktop start-api start-all sync-env setup test-web test-api test-desktop test-all lint lint-fix kill-api

# ── Setup ──────────────────────────────────────────────
setup: env hooks sync-env init-all
	@echo "Setup compleet. Run: make start-all"

hooks:
	@git config core.hooksPath .husky
	@echo "Git hooks geactiveerd (.husky/)"

env:
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "⚠  .env aangemaakt vanuit .env.example — vul secrets in!"; \
	else \
		echo ".env bestaat al"; \
	fi

sync-env:
	@echo "env synchroniseren naar web/.env.local..."
	@grep -E '^(AUTH_|NEXT_PUBLIC_)' .env > web/.env.local
	@echo "web/.env.local bijgewerkt"

# ── Dependencies ───────────────────────────────────────
init-web:
	@echo "dependencies installeren voor web..."
	cd web/ && bun install

init-desktop:
	@echo "dependencies installeren en compileren voor desktop..."
	cd desktop/ && ./mvnw compile -q

init-api:
	@echo "dependencies installeren en compileren voor api..."
	cd api/ && ./mvnw compile -q

init-all: init-web init-desktop init-api
	@echo "Alle dependencies geïnstalleerd"

# ── Start services ─────────────────────────────────────
start-api: sync-env
	@echo "api opstarten..."
	@set -a && . ./.env && set +a && cd api && ./mvnw spring-boot:run

start-desktop:
	@echo "desktop opstarten..."
	cd desktop && ./mvnw javafx:run

start-web: sync-env
	cd web/ && bun run dev

start-all: sync-env
	@echo "Alle services starten..."
	@set -a && . ./.env && set +a && cd api && ./mvnw spring-boot:run 2>&1 | sed 's/^/[api] /' &
	@cd desktop && ./mvnw javafx:run 2>&1 | sed 's/^/[desktop] /' &
	@cd web && bun run dev 2>&1 | sed 's/^/[web] /' &
	@wait

# ── Kill services ──────────────────────────────────────
kill-api:
	@echo "api stoppen (poort 8080)..."
	@lsof -ti tcp:8080 | xargs kill -9 2>/dev/null && echo "api gestopt" || echo "geen proces op poort 8080"

# ── Tests ──────────────────────────────────────────────
test-web:
	cd web/ && bun run test

test-api:
	cd api/ && ./mvnw test

test-desktop:
	cd desktop/ && ./mvnw test

test-all: test-web test-api test-desktop
	@echo "Alle tests geslaagd"

# ── Linting ────────────────────────────────────────────
lint:
	cd web/ && bun run lint
	cd api/ && ./mvnw spotless:check -q
	cd desktop/ && ./mvnw spotless:check -q

lint-fix:
	cd web/ && bunx prettier --write "**/*.{ts,tsx,js,jsx,json,css,md}" && bun run lint --fix
	cd api/ && ./mvnw spotless:apply -q
	cd desktop/ && ./mvnw spotless:apply -q
