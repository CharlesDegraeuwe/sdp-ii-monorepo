# SDP2-2526-G01 — Desktop App

JavaFX desktop applicatie voor SDP2. Communiceert met de gedeelde Java backend API.

## Vereisten

- Java 21+
- Maven
- IntelliJ IDEA
- Git
- De Java backend moet lokaal draaien (zie backend repo)

## Snel starten

### 1. Repository clonen

```bash
git clone [<repo-url>](https://github.com/HoGentProjectenII/2026-java-g01)
cd 2026-java-g01
```

### 2. Project openen

Open de root folder in IntelliJ IDEA. IntelliJ herkent de `pom.xml` automatisch en laadt de dependencies.

### 3. Environment variabelen

Maak een `.env` bestand aan in de root van het project (staat in `.gitignore`):

```
API_URL=http://localhost:8080/api
```

### 4. Applicatie starten

Via IntelliJ: rechtermuisklik op `App.java` → Run

Of via terminal:

```bash
mvn javafx:run
```

## Projectstructuur

```
2026_java-g01/
├── src/main/java/gui
│   ├── App.java           # Main klasse
│   └── util/              # Hulpklassen
├── src/main/resources/
│   ├── fxml/              # Schermen
│   ├── css/               # Stylesheets
│   └── images/            # Afbeeldingen
├── pom.xml
├── .env                   # Env variabelen (niet in git)
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
- **Web app (Next.js)**: `https://github.com/HoGentProjectenII/2026-react-g01`

## Team

Groep 01 — SDP2 2526
