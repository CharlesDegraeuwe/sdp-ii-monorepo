# pnpm + Bun + Next.js - Quickstart

## Installeren

### Windows

```powershell
# pnpm (package manager)
iwr https://get.pnpm.io/install.ps1 -useb | iex

# bun (runtime)
irm bun.sh/install.ps1 | iex
```

### Mac

Installeer eerst Homebrew indien nog niet aanwezig.

```homebrew
# pnpm (package manager)
brew install pnpm

# bun (runtime)
curl -fsSL https://bun.sh/install | bash
```

### Controller installatie

Herstart je terminal, check met `pnpm --version` en `bun --version`.

> Bij execution policy errors: `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`

### Dependencies installeren

We maken gebruik van een aantal dependencies, vergeet deze ook niet te installeren.

```
pnpm install
```

## Project starten

```terminal
bun run dev
```

App draait op `http://localhost:3000`. Stop met `Ctrl + C`.
Vergeet niet om de .env aan te vullen. In de root staat een .env.example.
Voor de Google Maps api ga naar `https://mapsplatform.google.com/lp/maps-apis/`.

## Cheatsheet

| Actie                      | Commando               |
| -------------------------- | ---------------------- |
| Dev server                 | `bun run dev`          |
| Productie build            | `bun run build`        |
| Productie server           | `bun run start`        |
| Pakket toevoegen           | `pnpm add <pakket>`    |
| Dev dependency             | `pnpm add -D <pakket>` |
| Pakketten (her)installeren | `pnpm install`         |
| Pakket verwijderen         | `pnpm remove <pakket>` |

## Pagina's toevoegen

App Router structuur: `src/app/<route>/page.js` → `localhost:3000/<route>`

```
src/app/
├── page.js              → /
├── about/page.js        → /about
└── blog/[slug]/page.js  → /blog/:slug
```

## Design system

We gebruiken een design system, vergeet dus zeker niet om deze componenten te gebruiken.
Vindbaar in:

```
components/design system/
├── /Button
    ├── Button.tsx
    ├── Button.types.ts
    └── index.ts
```

## Github tests

Na elke commit vinden een aantal tests plaats op code kwaliteit en linting.

## Links

- https://pnpm.io/
- https://bun.sh/docs
- https://nextjs.org/docs
