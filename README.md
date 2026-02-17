# SDP II G01 — Web App

Next.js frontend voor de SDP2 webapp. Communiceert met de gedeelde Java backend API.

## Vereisten

- Node.js 18+
- npm
- WebStorm (of andere IDE)
- Git
- De Java backend moet lokaal draaien (zie backend repo)

## Snel starten

### 1. Repository clonen

```bash
git clone https://github.com/HoGentProjectenII/2026-react-g01.git
cd 2026-react-g01
pnpm install
```

### 2. Environment variabelen

Maak een `.env.local` bestand aan in de root van het project (staat in `.gitignore`):

```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### 3. Development server starten

```bash
pnpm run dev
```

De app draait nu op `http://localhost:3000`.

## Projectstructuur

```
2026-react-g01/
├── src/
│   ├── app/           # Pages en routing
│   ├── components/    # Herbruikbare componenten
│   └── lib/           # API calls en utilities
├── public/            # Statische bestanden
├── .env.local         # Env variabelen (niet in git)
├── package.json
└── README.md
```

## Git conventies

- **main**: productie-klare code
- **dev**: integratie branch
- **feature/xxx**: nieuwe features (bv. `feature/login`)
- **bugfix/xxx**: bugfixes

Nieuwe feature starten:

```bash
git checkout dev
git pull
git checkout -b feature/jouw-feature
```

## Gerelateerde repo's

- **Backend (Java)**: `https://github.com/HoGentProjectenII/2026-backend-g01`
- **Desktop app (JavaFX)**: `https://github.com/HoGentProjectenII/2026-java-g01`

## Team

Groep 01 — SDP2 2526
