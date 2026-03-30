# pnpm + Bun + Next.js op Windows — Quickstart

## Installeren

```powershell
# pnpm (package manager)
iwr https://get.pnpm.io/install.ps1 -useb | iex

# bun (runtime)
irm bun.sh/install.ps1 | iex
```

Herstart je terminal, check met `pnpm --version` en `bun --version`.

> Bij execution policy errors: `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`

## Project aanmaken

```powershell
pnpm create next-app@latest mijn-app
cd mijn-app
bun run dev
```

App draait op `http://localhost:3000`. Stop met `Ctrl + C`.

## Cheatsheet

| Actie | Commando |
|-------|----------|
| Dev server | `bun run dev` |
| Productie build | `bun run build` |
| Productie server | `bun run start` |
| Pakket toevoegen | `pnpm add <pakket>` |
| Dev dependency | `pnpm add -D <pakket>` |
| Pakketten (her)installeren | `pnpm install` |
| Pakket verwijderen | `pnpm remove <pakket>` |

## Pagina's toevoegen

App Router structuur: `src/app/<route>/page.js` → `localhost:3000/<route>`

```
src/app/
├── page.js              → /
├── about/page.js        → /about
└── blog/[slug]/page.js  → /blog/:slug
```

## Links

- https://pnpm.io/
- https://bun.sh/docs
- https://nextjs.org/docs