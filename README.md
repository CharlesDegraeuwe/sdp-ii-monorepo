# Delaware Suite Monorepo

HR- en teammanagement platform. Deze monorepo bevat drie projecten:

- **web/** - De website (Next.js)
- **api/** - De backend server (Spring Boot)
- **desktop/** - De desktop applicatie (JavaFX)

---

## Wat heb je nodig?

Installeer deze tools **voordat** je begint:

1. **Java 21 (JDK)** - Download van [Adoptium](https://adoptium.net/). Kies "Temurin 21 (LTS)".
2. **Bun** - Ga naar [bun.sh](https://bun.sh/) en volg de installatie-instructies.
3. **Git** - Heb je waarschijnlijk al. Zo niet: [git-scm.com](https://git-scm.com/).

Maven hoef je **niet** apart te installeren. Die zit al in de repo (`./mvnw`).

> **Windows**: Gebruik **Git Bash** of **WSL** voor alle commando's hieronder. PowerShell en CMD werken niet.

---

## Stap 1: Repository clonen

Open een terminal en voer uit:

```bash
git clone https://github.com/CharlesDegraeuwe/sdp-ii-monorepo.git
cd sdp-ii-monorepo
```

---

## Stap 2: Setup draaien

Dit commando doet alles voor je: environment klaarzetten, git hooks activeren, en alle dependencies installeren.

```bash
make setup
```

Dit kan even duren (vooral de eerste keer). Wacht tot je "Setup compleet" ziet.

---

## Stap 3: Environment invullen

Na de setup bestaat er een `.env` bestand in de root van het project. Open dit bestand in je editor en vul de ontbrekende waardes in.

Dit zijn de variabelen die je moet invullen:

| Variabele | Waar te vinden | Voorbeeld |
|-----------|----------------|-----------|
| `DB_PASSWORD` | Vraag aan teamlead | `SDP_Ding_123` |
| `JWT_SECRET` | Verzin zelf, minstens 32 tekens | `mijn-super-geheime-sleutel-hier` |
| `AUTH_SECRET` | Verzin zelf, minstens 32 tekens | `nog-een-geheime-sleutel-hier` |
| `GEMINI_API_KEY` | Vraag aan teamlead | `AIzaSy...` |
| `RESEND_API_KEY` | Vraag aan teamlead | `re_Dff...` |
| `NEXT_PUBLIC_GOOGLE_API_KEY` | Vraag aan teamlead | `AIzaSy...` |

De rest van de waardes (URLs, database URL, username) staan al goed. Daar hoef je niks aan te veranderen.

---

## Stap 4: Alles opstarten

Start alle drie de projecten tegelijk:

```bash
make start-all
```

Je ziet logs van alle services met een prefix zodat je weet wat wat is:
- `[api]` - Backend server
- `[web]` - Website
- `[desktop]` - Desktop applicatie

Wil je ze **apart** opstarten (handiger voor debuggen)? Open drie terminals:

```bash
# Terminal 1 - Backend
make start-api

# Terminal 2 - Website
make start-web

# Terminal 3 - Desktop
make start-desktop
```

---

## Stap 5: Openen en testen

Als alles draait:

- **Website**: open [http://localhost:3000](http://localhost:3000) in je browser
- **API**: draait op [http://localhost:8080/api](http://localhost:8080/api)
- **Desktop**: opent automatisch als venster

---

## Werken met de code

### Voor je commit

Er draait automatisch een check bij elke `git commit`. Die controleert:
- Of je code goed geformateerd is
- Of de tests nog slagen

Als de commit faalt door formatting:

```bash
make lint-fix
git add .
git commit
```

### Testen draaien

```bash
make test-all
```

Of per project: `make test-web`, `make test-api`, `make test-desktop`.

### Formatting checken of fixen

```bash
make lint          # alleen checken
make lint-fix      # automatisch fixen
```

---

## Alle commando's op een rij

| Commando | Wat doet het |
|----------|-------------|
| `make setup` | Volledige eerste setup (eenmalig) |
| `make start-all` | Start alles tegelijk |
| `make start-api` | Start alleen de backend |
| `make start-web` | Start alleen de website |
| `make start-desktop` | Start alleen de desktop app |
| `make test-all` | Draai alle tests |
| `make lint` | Check code formatting |
| `make lint-fix` | Fix code formatting automatisch |
| `make sync-env` | Sync environment variabelen naar web |
| `make init-all` | Herinstalleer alle dependencies |

---

## IntelliJ IDEA

Na het openen van het project in IntelliJ staan er drie run configuraties klaar:

- **API (Spring Boot)** - Start de backend
- **Desktop (JavaFX)** - Start de desktop app
- **Web (Next.js)** - Start de website

Je vindt ze in de dropdown naast de groene play-knop. Geen handmatige configuratie nodig.

---

## Veelvoorkomende problemen

### "make: command not found" (Windows)

Je gebruikt waarschijnlijk PowerShell of CMD. Open **Git Bash** of **WSL** in plaats daarvan.

### "./mvnw: Permission denied"

```bash
chmod +x api/mvnw desktop/mvnw
```

### "bun: command not found"

Bun is niet geinstalleerd. Ga naar [bun.sh](https://bun.sh/) en volg de installatie.

### API start niet op (database fout)

De database draait op de HOGENT server. Check of je verbinding hebt met het schoolnetwerk (VPN).

Als alternatief kan je een lokale database draaien met Docker:

```bash
docker compose up -d
```

Pas dan `DB_URL` aan in je `.env`:

```
DB_URL=jdbc:mysql://localhost:3306/SDP2_2526_DB_G01?serverTimezone=UTC
```

### Pre-commit hook doet niks

```bash
make setup
```

Dit activeert de git hooks opnieuw.

### Commit faalt op formatting

```bash
make lint-fix
git add .
git commit
```

---

## Meer informatie

- [Architectuur](docs/architectuur.md) - Hoe de projecten samenwerken, welke packages wat doen, endpoints overzicht
- [Development gids](docs/development.md) - Uitgebreide uitleg over dagelijks ontwikkelen, nieuwe features toevoegen, environment variabelen
