# Architectuur

## Overzicht

Delaware Suite bestaat uit drie applicaties die samenwerken:

```
Browser/Desktop
    |
    v
+--------+     +--------+     +-------+
|  web   | --> |  api   | --> | MySQL |
| :3000  |     | :8080  |     |       |
+--------+     +--------+     +-------+
    ^               ^
    |               |
+----------+       |
| desktop  | ------+
| (JavaFX) |
+----------+
```

- **web** en **desktop** zijn beide clients die met de **api** communiceren
- **api** is de enige die met de database praat
- Communicatie gaat via REST (HTTP) en WebSockets (chat)

## API (Spring Boot)

### Packages

```
hogent.sdp2.backend/
  auth/           Authenticatie (JWT) en verificatie
  config/         Spring configuratie (CORS, Security, WebSocket)
  domain/         JPA entities (Werknemer, Team, Taken, Site, Shift, Verlof, ...)
  rest/
    controller/   REST endpoints (14 controllers)
    dto/          Request- en response-objecten
    repository/   JPA repositories (data access)
    service/      Business logica
  websocket/      WebSocket handlers voor chat
```

### Belangrijke endpoints

| Prefix | Verantwoordelijk voor |
|--------|----------------------|
| `/api/werknemers` | Werknemers beheren, login, activatie |
| `/api/teams` | Teams CRUD |
| `/api/taken` | Taken CRUD en toewijzing |
| `/api/planning` | Shifts en planning |
| `/api/afwezigheden` | Afwezigheden en verlof |
| `/api/locaties` | Sites/locaties |
| `/api/dashboard` | Dashboard statistieken |
| `/api/notificaties` | Notificaties |
| `/ws/chat` | WebSocket chat |

### Authenticatie

1. Gebruiker logt in via `/api/werknemers/login-password` of `/api/werknemers/login-token`
2. API geeft JWT token terug
3. Elke request bevat `Authorization: Bearer <token>` header
4. `JwtService` valideert tokens, `SecurityConfig` beschermt endpoints

## Web (Next.js)

### Route structuur

```
web/app/
  (pages)/
    (auth)/login/          Inlogpagina
    (app)/
      overzicht/           Dashboard
      planner/             Planning
      taken/               Taken
      teams/               Teams (lijst, detail, aanmaken)
      afwezigheden/        Melden en geschiedenis
      chat/                Chat (WebSocket)
      locaties/            Locaties (Google Maps)
      notificaties/        Notificaties (SSE)
      admin/               Beheer gebruikers, managers/werknemers aanmaken
      account/             Account instellingen
      instellingen/        App instellingen
  api/                     API routes (NextAuth, planning proxy)
```

### Belangrijke patronen

- **NextAuth.js** voor sessie-management (wraps JWT van API)
- **Custom hooks** (`web/hooks/`) voor data fetching per feature
- **Design system** (`web/components/design-system/`) voor herbruikbare UI componenten
- **SSE** voor real-time notificaties
- **WebSocket** voor chat

## Desktop (JavaFX)

### Packages

```
domain/
  auth/       Authenticatie
  dto/        Data transfer objects
  facades/    Business facades (1 per feature)
  services/   HTTP services naar API
  util/       Hulpklassen

hogent.sdp2.sdpii.gui/
  admin/      Admin schermen
  app/        Feature schermen (planning, taken, teams, afwezigheden, ...)
  auth/       Login schermen
  components/ Herbruikbare UI componenten
  router/     Navigatie
```

### Patroon

Desktop volgt een facade-patroon:
1. **Controller** (GUI) roept **Facade** aan
2. **Facade** roept **Service** aan
3. **Service** doet HTTP request naar API
4. Response wordt via **DTO** terug naar controller gestuurd

## Database

MySQL database met de volgende hoofdtabellen:

- `werknemers` - Alle gebruikers (werknemers, managers, admins)
- `teams` - Teams met teamleider
- `taken` - Taken gekoppeld aan werknemers
- `shifts` - Geplande shifts
- `sites` - Fysieke locaties
- `verlof` - Verlofaanvragen
- `afwezigheden` - Geregistreerde afwezigheden
- `notificaties` - Notificaties per gebruiker

JPA `ddl-auto` staat op `none`. Schema wordt niet automatisch gegenereerd.
