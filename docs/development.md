# Development

## Eerste keer opzetten

```bash
git clone https://github.com/CharlesDegraeuwe/sdp-ii-monorepo.git
cd sdp-ii-monorepo
make setup
```

Vul daarna `.env` in (zie `.env.example` voor alle variabelen).

## Dagelijks ontwikkelen

### Alles opstarten

```bash
make start-all
```

Of in aparte terminals:

```bash
make start-api       # Terminal 1
make start-web       # Terminal 2
make start-desktop   # Terminal 3
```

### Testen draaien

```bash
make test-all        # Alles
make test-web        # Alleen web (Vitest)
make test-api        # Alleen API (JUnit)
make test-desktop    # Alleen desktop (JUnit)
```

### Formatting en linting

```bash
make lint            # Check alles
make lint-fix        # Fix alles automatisch
```

Per project handmatig:

```bash
# Web
cd web && bun run lint

# Java (API of Desktop)
cd api && ./mvnw spotless:check     # check
cd api && ./mvnw spotless:apply     # fix
```

## Nieuwe feature toevoegen

### API

1. Maak entity in `domain/`
2. Maak repository in `rest/repository/`
3. Maak service in `rest/service/`
4. Maak DTOs in `rest/dto/`
5. Maak controller in `rest/controller/`
6. Schrijf tests in `src/test/`

### Web

1. Maak pagina in `web/app/(pages)/(app)/`
2. Maak hook in `web/hooks/` voor data fetching
3. Maak componenten in `web/components/app/`
4. Gebruik design system componenten uit `web/components/design-system/`

### Desktop

1. Maak DTO in `domain/dto/`
2. Maak service in `domain/services/`
3. Maak facade in `domain/facades/`
4. Maak FXML layout in `resources/fmxl/`
5. Maak controller in `gui/app/`
6. Schrijf tests voor de facade

## Pre-commit hooks

Bij elke commit draait automatisch:

- **Altijd**: Prettier + ESLint op gewijzigde web bestanden
- **Als api/ gewijzigd**: Spotless check + Maven tests
- **Als desktop/ gewijzigd**: Spotless check + Maven tests

Als een check faalt:

```bash
make lint-fix        # Fix formatting
git add .            # Stage opnieuw
git commit           # Probeer opnieuw
```

## Environment variabelen

Alles staat in 1 bestand: `.env` in de root.

| Variabele | Gebruikt door | Omschrijving |
|-----------|--------------|--------------|
| `AUTH_API_URL` | web | URL naar API voor server-side auth |
| `NEXT_PUBLIC_API_URL` | web | URL naar API voor client-side calls |
| `NEXT_PUBLIC_WS_URL` | web | WebSocket URL voor chat |
| `AUTH_URL` | web | URL van de web app zelf |
| `AUTH_SECRET` | web | NextAuth secret (min. 32 tekens) |
| `NEXT_PUBLIC_GOOGLE_API_KEY` | web | Google Maps API key |
| `DB_URL` | api | JDBC connection string |
| `DB_USERNAME` | api | Database gebruiker |
| `DB_PASSWORD` | api | Database wachtwoord |
| `JWT_SECRET` | api | JWT signing secret |
| `GEMINI_API_KEY` | api | Gemini AI API key |
| `RESEND_API_KEY` | api | Resend e-mail API key |

`make sync-env` kopieert `AUTH_*` en `NEXT_PUBLIC_*` variabelen naar `web/.env.local`. Dit gebeurt automatisch bij `make start-web` en `make start-all`.

## Lokale database

Standaard wordt de remote database op `vichogent.be` gebruikt. Voor lokaal:

```bash
docker compose up -d
```

Pas `DB_URL` in `.env` aan:

```
DB_URL=jdbc:mysql://localhost:3306/SDP2_2526_DB_G01?serverTimezone=UTC
```

Let op: de lokale database is leeg. Je hebt een SQL dump nodig om data te importeren.

## Veelvoorkomende problemen

### `./mvnw: Permission denied`

```bash
chmod +x api/mvnw desktop/mvnw
```

### `bun install` faalt

Zorg dat Bun geinstalleerd is: https://bun.sh/

### Pre-commit hook draait niet

```bash
cd web && bun run prepare
```

### API start niet (database connectie)

Check of je VPN aan staat (voor vichogent.be) of draai lokale MySQL via Docker.
