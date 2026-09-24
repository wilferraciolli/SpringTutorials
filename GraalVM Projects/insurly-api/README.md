# Getting Started

## Running the API locally

### 1. Database

There is no local Postgres container — both `local` and `prod` profiles
connect straight to **Supabase** Postgres (pooled connection). Get the
connection details from the Supabase dashboard (Project Settings →
Database → Connection pooling) and put them in `insurly-api/.env` (see
Configuration below).

Flyway migrations (`src/main/resources/db/migration`) run automatically
on startup — no manual schema setup needed once the DB is reachable.

### 2. Configuration

Config is layered: `application.yaml` (base) + a profile file selected by
`SPRING_PROFILES_ACTIVE` (defaults to `local`):
- `application-local.yaml` — local dev, no defaults for `DATABASE_URL` /
  `DATABASE_USERNAME` / `DATABASE_PASSWORD` — same as `prod`, these must
  come from `insurly-api/.env` (see below).
- `application-prod.yaml` — deployed profile, requires `DATABASE_URL`,
  `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `CORS_ALLOWED_ORIGIN` to be
  set via environment — no local defaults.

To override locally without touching the yaml files, copy
`insurly-api/.env.example` to `insurly-api/.env` and edit the values —
it's picked up automatically via `spring.config.import` in
`application.yaml`.

### 3. Start the server

```bash
# from insurly-api/, using the Maven wrapper
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

The API comes up on `http://localhost:5001`, mounted under the
`/api/` context path (e.g. `http://localhost:5001/api/health`). Override
the port with `SERVER_PORT`.

## API surface (high level)

| Area | Endpoints | Auth |
|---|---|---|
| Health | `GET /api/health` | none |
| Guest quotes | `POST /api/quotes/car`, `GET /api/quotes/car/template`, `GET /api/quotes/{id}?guestToken=…` | none |
| Providers | `GET/POST/PUT/DELETE /api/admin/providers` | admin |
| Accounts | `GET/PUT/DELETE /api/me`, `…/me/cars`, `…/me/addresses`, `…/me/phones`, `…/me/licenses`, `…/me/quotes`, `GET /api/me/quote-prefill`, `POST /api/me/quotes/claim` | signed-in user |
| Admin — customers | `/api/admin/users` and `…/users/{id}/{cars,addresses,phones,licenses,quotes}` | admin |
| Admin — dashboard | `GET /api/admin/dashboard` | admin |
| System settings | `GET /api/system-settings` (public), `GET/PUT /api/admin/system-settings` | read: none · write: admin |

Responses use a HAL-ish envelope: the resource under `_data.<rootName>`, plus
`_metadata` / `_metaLinks` / `_messages`. Errors go through one
`@RestControllerAdvice` (`GlobalExceptionHandler`) with a consistent JSON shape.

See `docs/02-architecture.md` for the full surface and `docs/05-security-and-accounts.md`
for the auth model.

## Auth — Clerk JWT, with a local dev fallback

Identity is resolved via `UserIdentityResolver` (`account/identity/`), and
every consumer (`CurrentUserService`, `AdminGuardInterceptor`,
`AdminAccessService`) injects `CompositeUserIdentityResolver`, which tries
each of these in order:

1. **`JwtUserIdentityResolver`** — the real path. `SecurityConfig` validates
   the `Authorization: Bearer <jwt>` header against Clerk's JWKS (`iss`,
   `aud`, `exp`, signature) before this resolver ever runs; it just maps
   claims to an identity. Configure via `AUTH_ISSUER_URI`, `AUTH_AUDIENCE`,
   `AUTH_ROLE_CLAIM` (see `.env.example`) — required in every profile, since
   Clerk is a SaaS with no local instance.
2. **`HeaderUserIdentityResolver`** — `@Profile("local")` only, never active
   in `prod`. Trivially-spoofable `X-Insurly-*` request headers, kept purely
   so `/api/me/**` and `/api/admin/**` are exercisable from the `.http` files
   below without a real Clerk token:

   | Header | Meaning |
   |---|---|
   | `X-Insurly-User` | subject id (required) — e.g. `dev\|alice` |
   | `X-Insurly-Email` | email |
   | `X-Insurly-Name` | display name |
   | `X-Insurly-Roles` | comma-separated, e.g. `admin` — `/api/admin/**` needs `admin` |

`/api/admin/**` is gated by `AdminGuardInterceptor` on the `ADMIN` role from
whichever resolver above produced an identity. See `docs/05` §6 for the full
design.

Ready-made request collections: `src/test/resources/*.http`
(`accounts.http`, `providers.http`) — these still work locally via the
header fallback.

## API docs (OpenAPI / Swagger UI)

springdoc-openapi is wired in. With the app running:

- Swagger UI — `http://localhost:5001/api/swagger-ui.html`
- Raw spec — `http://localhost:5001/api/v3/api-docs`

Both are **enabled in `local`/`dev`** and **disabled by default in `prod`**
(`API_DOCS_ENABLED` / `SWAGGER_UI_ENABLED` env flags in `application-prod.yaml`).

## Extra config

- `LICENSE_ENC_KEY` — base64 of 32 random bytes; AES-256-GCM key for the
  encrypted `user_license.license_number` column. A dev fallback is used if
  unset (logs a warning). See `.env.example`.

## Customer & provider search

- **`GET /api/admin/users`** takes optional `q` (name/email contains),
  `ownership` (`MANAGED`|`SELF_SERVICE`), `filter`
  (`ALL`|`RENEWAL_DUE`|`HAS_OPEN_QUOTES`|`NO_QUOTES`|`PREMIUM_VEHICLE`) and
  `sort` (`RECENT`|`NAME`|`RENEWAL_SOONEST`). Filtering runs in memory over two
  batch loads (`AdminUserAppService.search`) — fine at admin-tool scale.
  `PREMIUM_VEHICLE` matches `user_car.make` against a curated marque list.
- **`GET /api/admin/providers`** takes optional `q` (name contains).

## Email & notifications

Three HTML email templates under `src/main/resources/templates/email/`, rendered
with Thymeleaf:

| Template | To | Trigger |
|---|---|---|
| `admin-new-quote.html` | admin recipients | wired — every `POST /api/quotes/car` (fire-and-forget) |
| `user-top-quotes.html` | the requester | ready, not triggered — `QuoteEmailService.sendTopQuotesToUser` |
| `admin-weekly-digest.html` | admin recipients | wired — `WeeklyAdminDigestJob`, `@Scheduled` Mondays 08:00 |

Transport is `app.email.enabled` (`.env`: `EMAIL_ENABLED`):
- `false` — `LoggingEmailSender` renders and logs every message, never sends.
- `true` — `JavaMailEmailSender` sends over `spring.mail.*` (any SMTP provider,
  set via `MAIL_*` env vars). For production pick a free provider — SMTP2GO
  (1,000/mo, single-sender verification), Mailjet, Resend — see `.env.example`.

For local dev, catch mail instead of sending it with a **Mailtrap sandbox**
(hosted, free, no Docker): grab the inbox's SMTP username/password from
mailtrap.io → Email Testing → Inboxes → Integrations → SMTP, then set
`MAIL_HOST=sandbox.smtp.mailtrap.io`, `MAIL_PORT=2525`, `MAIL_USERNAME` /
`MAIL_PASSWORD`. This is how the checked-in `.env` is wired. With
`EMAIL_ENABLED=true`, every `POST /api/quotes/car` shows up in the Mailtrap
inbox. See `.env.example` for the full setup.

Full design: `../docs/07-notifications.md`.

## Simulating an insurer panel

`../mock-insurers/` runs fake insurer quote APIs you can fan out to. Nothing
here calls them yet — it's Phase 3 groundwork (`docs/06`).

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.1/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.1/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.1/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/4.1.1/reference/io/validation.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.1.1/reference/using/devtools.html)
* [Flyway Migration](https://docs.spring.io/spring-boot/4.1.1/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

