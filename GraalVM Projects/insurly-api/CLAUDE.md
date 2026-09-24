# insurly-api (backend)

Spring Boot backend for the insurance quote wizard. See root `CLAUDE.md` for
project-wide decisions and `../docs/02-architecture.md` /
`../docs/03-database-schema.md` for full design.

## Stack (as actually configured — `pom.xml`)
- Java 25, Spring Boot 4.1.1 (parent `spring-boot-starter-parent`)
- Config file: `application.yaml` (base) + `application-local.yaml` /
  `application-prod.yaml` (profile-specific) — no `.properties` files
- Base package: `com.wiltech.insurly` (docs still say `com.insuraquote` — don't rename without asking)
- Spring Data JPA + Flyway (`flyway-database-postgresql`) + Bean Validation + Spring MVC
- Test deps: `spring-boot-starter-*-test` variants (JPA, Flyway, validation, webmvc) — no Testcontainers dependency yet, docs call for it (`docs/02-architecture.md`), add it when writing the first repository/integration test
- PostgreSQL driver at runtime; devtools for local hot reload

## Layering (RestService → ApplicationService → Repository)
Keep this strict — a `RestService` never talks to a `Repository` directly,
and pricing logic never talks to HTTP or persistence.

- **`*RestService`** — the HTTP/controller layer (`@RestController`). Owns
  request/response DTOs, `@Valid` input validation, and HTTP status codes.
  Delegates all business logic to an `*ApplicationService`.
- **`*ApplicationService`** — business logic / orchestration layer. Talks
  to one or more repositories and the rules engine, maps between entities
  and DTOs, has no knowledge of HTTP.
- **`*Repository`** — Spring Data JPA repositories. Return/accept entities
  only, never DTOs.

Target package shape (adapted from `docs/02-architecture.md` to the real
base package and the naming convention below):
```
src/main/java/com/wiltech/insurly/
├── quote/
│   ├── QuoteRestService.java
│   ├── QuoteApplicationService.java
│   ├── QuoteRepository.java
│   ├── dto/
│   │   └── QuoteRequest.java / QuoteResponse.java   # DTOs, never expose entities over the API
│   ├── rules/
│   │   ├── PricingEngine.java                       # interface — keep the mock engine swappable
│   │   └── CarInsuranceRulesEngine.java              # mock pricing logic, pure Java, deterministic
│   └── entity/                                      # Quote, Driver, Vehicle, CoverageOption
├── config/          # CORS, OpenAPI, security config
└── common/          # exceptions, global @ControllerAdvice, validation helpers
```

## Environment config
- Profiles: `application.yaml` sets
  `spring.profiles.active: ${SPRING_PROFILES_ACTIVE:local}` so plain
  `mvn spring-boot:run` defaults to the `local` profile. It also sets
  `spring.config.import: optional:file:.env[.properties]`, which loads
  `insurly-api/.env` (KEY=VALUE, .properties-compatible syntax) as a
  property source automatically — no extra plugin needed.
- `insurly-api/.env` (+ `.env.example`) — the only `.env` file now. Used
  for running `insurly-api` directly (IDE run config, `mvn spring-boot:run`)
  and picked up via `spring.config.import` above. There is no root-level
  `.env`/`docker-compose.yaml` anymore — local dev connects straight to
  Supabase, same as prod, so nothing needs a local Postgres container.
- `application-local.yaml` — local dev, **no defaults** for `DATABASE_URL`
  / `DATABASE_USERNAME` / `DATABASE_PASSWORD` — same as `prod`, must come
  from `insurly-api/.env` (Supabase pooled connection).
- `application-prod.yaml` — deployed (Oracle Cloud VM via
  `insurly-api/docker-compose.yml`), **no defaults** — every value
  (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`,
  `CORS_ALLOWED_ORIGIN`) must come from an env var. `SERVER_PORT`
  defaults to `5001` if unset.
- Never add secrets to `application-*.yaml` directly — env var
  placeholders only.

## Naming conventions
- **Controllers**: suffix `RestService` (e.g. `QuoteRestService`), not
  `Controller`.
- **Service layer**: suffix `ApplicationService` (e.g.
  `QuoteApplicationService`), not `Service`.
- **Repositories**: suffix `Repository` (e.g. `QuoteRepository`,
  `DriverRepository`) — standard Spring Data JPA naming.
- **IDs**: every entity's primary key is a `UUID` (matches
  `docs/03-database-schema.md` — `gen_random_uuid()` / `pgcrypto`). Don't
  introduce `Long`/sequence IDs anywhere in new tables or entities.
- **DTOs**: every repository/entity has a corresponding DTO — controllers
  (`*RestService`) and application services only ever pass DTOs across
  their boundary with each other and with callers; JPA entities must never
  be returned from a `*RestService` or serialized directly to JSON.

## Conventions
- **DTOs vs entities**: request/response DTOs are separate types from JPA
  entities. Validate DTOs with `@Valid` / Bean Validation annotations.
- **Rules engine**: pure Java, deterministic function of driver age, years
  licensed, violation/accident count, vehicle year/type, coverage tier. No
  external calls, no randomness. Always code to the `PricingEngine`
  interface so a real provider can replace it later without touching
  the `RestService` layer.
- **Migrations**: Flyway only, files in
  `src/main/resources/db/migration`, standard `V{n}__description.sql`
  naming. Never use `ddl-auto: update`/`create` outside of local
  experimentation. Table design follows `docs/03-database-schema.md`
  (UUID PKs via `pgcrypto`, guest quotes retrievable via `guest_token`,
  `user_id` nullable until Phase 2 auth lands).
- **Error handling**: one global `@ControllerAdvice` producing a
  consistent JSON error shape — don't let individual controllers roll
  their own error responses.
- **Config/secrets**: `application-local.yaml` / `application-prod.yaml`;
  secrets via env vars only, never committed. Supabase (pooled connection
  string) for every environment, local dev included — see
  `docs/03-database-schema.md`.
- **Deploying**: `insurly-api/Dockerfile` + `insurly-api/docker-compose.yml`
  build and run the backend as a standalone container — this is the
  artifact that ships to the Oracle Cloud VM. The container only serves
  plain HTTP; a reverse proxy on the VM terminates TLS in front of it.
  See `docs/04-deployment.md` for the full flow.
- **CORS**: explicit allow-list of the Angular origin, never `*`.
- **API surface** (MVP, keep in sync with `docs/02-architecture.md`):
  `POST /api/quotes/car`, `GET /api/quotes/{id}`, `GET /api/health`.
- **Auth**: identity is resolved via the `UserIdentityResolver` interface
  (`account/identity/`) — never read `Authorization`/`X-Insurly-*` directly
  in a `RestService`. `JwtUserIdentityResolver` (Clerk JWT, validated by
  `SecurityConfig`) is the real path; `HeaderUserIdentityResolver` is a
  `@Profile("local")`-only fallback for the `.http` test files.
  `CompositeUserIdentityResolver` (`@Primary`) is what everything actually
  injects. See `insurly-api/README.md` and `docs/05-security-and-accounts.md` §6.

## Testing
- JUnit 5 for unit tests.
- Integration/repository tests should use Testcontainers against a real
  Postgres (add the `testcontainers` + `testcontainers-junit-jupiter` +
  `testcontainers-postgresql` deps when the first one is written) —
  don't mock the database for anything touching Flyway-managed schema.
- Rules engine logic is pure functions — cover it with plain unit tests,
  no Spring context needed.
