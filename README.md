# Wishlist – Digital Ønskeseddel

## Projektbeskrivelse
Wishlist blev oprindeligt udviklet som en **webbaseret databaseapplikation**
på 2. semester af Datamatikeruddannelsen (miniprojekt). Den oprindelige
version brugte en **Spring Boot backend** med **MySQL**, **Thymeleaf frontend**
og et CI/CD-opsæt via **GitHub Actions** og **Azure App Service**.

Efter afleveringen har jeg arbejdet videre på projektet i min fritid:
nyt moderne design, demo-profil med H2 så det kan køre uden database,
Dockerfile til deploy på Render.com, hemmelige reservationer via delingslink
og en række oprydninger (credentials ud af koden, env-var-baseret prod-config,
m.m.).

Stack: **Spring Boot 3**, **Thymeleaf**, **JPA/Hibernate**, **MySQL** (prod)
eller **H2** (demo). Design-inspiration fra Project Manager med egen twist i
rose/amber-palet.

---

## Kør lokalt

### Demo-profil (H2 in-memory, ingen database kræves)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Åbn <http://localhost:8080> og log ind med:

- E-mail: `demo@wishlist.dk`
- Adgangskode: `demo1234`

Databasen er in-memory, så alt nulstilles ved genstart. Seed-data ligger i
`src/main/resources/sql/demo_data.sql`.

### Prod-profil (MySQL via miljøvariabler)

```bash
export DB_URL=jdbc:mysql://host:3306/wishlistdb
export DB_USERNAME=...
export DB_PASSWORD=...
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## Deploy til Render.com

Projektet er klar til Render via `Dockerfile` i roden.

1. Opret et nyt **Web Service** på Render → forbind GitHub-repoet.
2. Vælg **Docker** som runtime (ingen build-command nødvendig — Dockerfile tager over).
3. Standard-opsætningen bruger demo-profilen (H2 in-memory), så du kan deploye
   uden ekstern database. Alt data nulstilles når instansen genstarter.
4. Vil du bruge MySQL i produktion, så sæt disse env-vars i Render:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`

Render giver automatisk en `PORT`-env-variable, som appen allerede læser via
`server.port=${PORT:8080}`.

---

## Projektstruktur

```
src/main/java/com/example/
  WishlistApplication.java      – Spring Boot entry point
  controller/                   – HomeController, AuthController, WebWishlistController, WebWishController
  service/                      – UserService, WishlistService, WishService
  repository/                   – Spring Data JPA repositories
  model/                        – User, Wishlist, Wish (JPA entities)

src/main/resources/
  application.properties        – fælles config (PORT, session, Thymeleaf)
  application-demo.properties   – H2 in-memory + seed
  application-prod.properties   – MySQL via env-vars
  sql/demo_data.sql             – seed-data til demo-profilen
  static/css/app.css            – design-system
  templates/                    – Thymeleaf-skabeloner
    fragments/layout.html       – fælles head/header/footer
    index.html, login.html, register.html
    wishlists.html, wishlist.html, sharedWishlist.html
    form.html, edit.html, wishlistNotFound.html
```

---

## Funktioner

- Flere ønskesedler pr. bruger
- Hvert ønske har beskrivelse, link og pris
- Unikt delingslink pr. ønskeseddel (ingen login kræves for gæster)
- Gæster kan **reservere** ønsker — ejeren kan ophæve igen
- Responsivt design der virker på mobil, tablet og desktop

---

## Teknisk

- Java 21, Spring Boot 3.3
- Spring Data JPA + Hibernate
- MySQL (prod), H2 (demo/test)
- Thymeleaf til server-side rendering
- Maven wrapper (`./mvnw`), ingen lokal Maven nødvendig
- Multi-stage Docker build (JDK → JRE)
