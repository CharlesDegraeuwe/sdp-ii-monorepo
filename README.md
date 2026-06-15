# Delaware Suite Monorepo

HR- en teammanagement platform. Drie projecten in een monorepo.

Zie ook: [Architectuur](docs/architectuur.md) | [Development gids](docs/development.md)

## Projecten

| Project | Stack | Poort |
|---------|-------|-------|
| `web/` | Next.js 16, React 19, Bun, Tailwind 4 | 3000 |
| `api/` | Spring Boot 4, Java 21, MySQL | 8080 |
| `desktop/` | JavaFX 21 | - |

## Vereisten

| Tool | Versie | Installatie |
|------|--------|-------------|
| Java (JDK) | 21 | [Adoptium](https://adoptium.net/) |
| Bun | latest | [bun.sh](https://bun.sh/) |
| Maven | 3.9+ | Niet nodig als je `./mvnw` gebruikt (zit in repo) |
| Make | - | Mac: standaard aanwezig. Windows: gebruik **Git Bash** of **WSL** |

> **Windows-gebruikers**: open alle terminal-commando's in **Git Bash** of **WSL**. PowerShell en CMD worden niet ondersteund.

## Aan de slag

### 1. Repository clonen

```bash
git clone https://github.com/CharlesDegraeuwe/sdp-ii-monorepo.git
cd sdp-ii-monorepo
```

### 2. Setup (eenmalig)

```bash
make setup
```

Dit doet drie dingen:
- Kopieert `.env.example` naar `.env` (als die nog niet bestaat)
- Synchroniseert environment variabelen naar `web/.env.local`
- Installeert dependencies voor alle projecten

### 3. Environment invullen

Open `.env` in de root en vul de waardes in:

```env
# Web
AUTH_SECRET=<willekeurige string, minstens 32 tekens>
NEXT_PUBLIC_GOOGLE_API_KEY=<Google Maps API key>

# API
DB_PASSWORD=<database wachtwoord>
JWT_SECRET=<willekeurige string, minstens 32 tekens>
GEMINI_API_KEY=<Gemini API key>
RESEND_API_KEY=<Resend API key>
```

De overige waardes (URLs, DB_URL, DB_USERNAME) staan al goed voor development.

### 4. Opstarten

Alles tegelijk:

```bash
make start-all
```

Of apart in verschillende terminals:

```bash
# Terminal 1
make start-api

# Terminal 2
make start-web

# Terminal 3
make start-desktop
```

### 5. Openen

- Web: http://localhost:3000
- API: http://localhost:8080/api

## Commando's

### Opstarten

| Commando | Wat doet het |
|----------|-------------|
| `make start-all` | Start api, web en desktop tegelijk |
| `make start-api` | Start Spring Boot API |
| `make start-web` | Start Next.js dev server |
| `make start-desktop` | Start JavaFX desktop app |

### Testen

| Commando | Wat doet het |
|----------|-------------|
| `make test-all` | Alle tests (Vitest + JUnit) |
| `make test-web` | Web tests (Vitest) |
| `make test-api` | API tests (JUnit) |
| `make test-desktop` | Desktop tests (JUnit) |

### Code kwaliteit

| Commando | Wat doet het |
|----------|-------------|
| `make lint` | Check formatting en linting (alle projecten) |
| `make lint-fix` | Fix formatting automatisch (alle projecten) |

### Setup

| Commando | Wat doet het |
|----------|-------------|
| `make setup` | Volledige eerste setup |
| `make sync-env` | Sync `.env` naar `web/.env.local` |
| `make init-all` | Dependencies installeren |

## Hoe werkt de environment?

Er is 1 `.env` bestand in de root. Dat is de enige plek waar je variabelen invult.

- **Web**: `make sync-env` kopieert de relevante variabelen (`AUTH_*`, `NEXT_PUBLIC_*`) naar `web/.env.local`. Dit gebeurt automatisch bij `make start-web` en `make start-all`.
- **API**: Spring Boot leest de variabelen via `${VAR:fallback}` syntax in `application.yml`. De Makefile laadt `.env` als shell variabelen bij het opstarten.

## Lokale database (optioneel)

Standaard verbindt de API met de remote database op `vichogent.be`. Wil je lokaal draaien:

```bash
docker compose up -d
```

Pas daarna `DB_URL` aan in `.env`:

```env
DB_URL=jdbc:mysql://localhost:3306/SDP2_2526_DB_G01?serverTimezone=UTC
```

## Pre-commit hooks

Bij elke `git commit` draait automatisch:

- **Web**: Prettier + ESLint op gewijzigde bestanden
- **API/Desktop**: Spotless formatting check + Maven tests (alleen als bestanden in die map gewijzigd zijn)

Als de check faalt:

```bash
make lint-fix    # fix formatting
git add .        # opnieuw stagen
git commit       # opnieuw proberen
```

## CI/CD

Bij elke push naar `main` en bij pull requests draait GitHub Actions:

- **Web**: lint, build, test
- **API**: formatting check, compile, test
- **Desktop**: formatting check, compile, test

## Projectstructuur

```
sdp-ii-monorepo/
  api/                Spring Boot REST API
  desktop/            JavaFX desktop client
  web/                Next.js frontend
  .env.example        Template voor environment variabelen
  .env                Lokale env (niet in git)
  .husky/             Pre-commit hooks
  docker-compose.yml  Lokale MySQL database
  Makefile            Alle commando's
```
