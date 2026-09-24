# resource-management-api

FastAPI backend for the hospital resource-management app. Deploys as a
Cloudflare Python Worker, but `src/main.py` and every other file except
`src/cloudflare_entry.py` are plain, portable FastAPI/Python — they also
run under a normal `uvicorn` process with no Cloudflare-specific code
involved.

## Run it locally with Docker (recommended — works the same on Linux, Windows, macOS)

```bash
cp .env.example .env   # fill in CLERK_JWKS_URL, see Configuration below
docker compose up --build
```

The API is now at `http://localhost:8001` — `http://localhost:8001/docs`
for the Swagger UI. The SQLite database lives in a **named Docker volume**
(`sqlite-data`, mounted at `/data` in the container), not a bare file in
this folder — that's the point: the same `docker compose up` behaves
identically regardless of which machine or OS you're on, and there's no
host-filesystem database file to lose track of between machines.

`src/`, `migrations/`, and `scripts/` are bind-mounted into the container,
so editing them on the host live-reloads the running server (`--reload` is
on in `docker-compose.yml`) — no rebuild needed for code changes; rebuild
(`docker compose up --build`) only when `pyproject.toml`/`uv.lock` change.

Seed some demo beds (only needs running once per fresh volume):

```bash
docker compose exec api uv run python scripts/seed_beds.py
```

Or seed a fuller demo dataset — wards, doctors, procedures, and beds with a
mix of statuses (only needs running once per fresh volume):

```bash
docker compose exec api uv run python scripts/seed_demo_data.py
```

To reset the local database entirely, drop the volume:

```bash
docker compose down -v
```

## Run it locally without Docker

Dependencies are managed with [uv](https://docs.astral.sh/uv/)
(`curl -LsSf https://astral.sh/uv/install.sh | sh`).

```bash
uv sync
cp .env.example .env
uv run python scripts/seed_demo_data.py   # creates ./local.db on the host, with demo wards/doctors/procedures/beds
uv run uvicorn main:app --app-dir src --reload --port 8001
```

**Simulated Cloudflare Worker** (slower to start, but runs on the real
Pyodide runtime — use this before deploying, or whenever you want to be
sure something works under Workers, not just plain Python; needs
Node.js/npm, which it shells out to via `npx wrangler`):

```bash
uv run pywrangler dev --port 8787
```

If a command complains a port is already in use, something from a
previous run is still bound to it:

```bash
pkill -f "uvicorn main:app"
pkill -f workerd   # kills a leftover `pywrangler dev` / `wrangler dev`
```

## Auth

Every route except `/health` requires a Clerk session token:

```bash
curl http://localhost:8001/me -H "Authorization: Bearer <token>"
```

Note: outbound HTTP calls from inside the deployed Worker must use **async
httpx** (or aiohttp) — the Pyodide runtime doesn't support
`urllib`/sync-socket-based clients, which is why `auth.py` fetches Clerk's
JWKS with `httpx.AsyncClient` instead of PyJWT's built-in `PyJWKClient`.

## Configuration (env vars)

Copy `.env.example` to `.env` and fill in real values — `.env` is
gitignored, never commit it.

- `CLERK_JWKS_URL` — Clerk instance's JWKS endpoint (e.g.,
  `https://lasting-colt-8233.clerk.accounts.dev/.well-known/jwks.json`).
  Find it in Clerk Dashboard → Configure → API Keys → "Frontend API URL"
  → append `/.well-known/jwks.json`.
- `DATABASE_MODE` / `DATABASE_PATH` / `CF_*` — see Database below.

**How config is read is *not* symmetric between local uvicorn and the
deployed Worker — this tripped us up once already, worth understanding:**
Cloudflare Workers expose `vars`/secrets through a **per-request** `env`
object (`request.scope["env"]`), not through `os.environ` — confirmed by
testing directly against a real Worker, which raised `KeyError` when
`config.py` first tried a module-level `os.environ["CLERK_JWKS_URL"]` read
at import time, even though `.env` was picked up and shown as a binding by
Wrangler. So `src/config.py`'s `get_config(request, key)` checks
`request.scope["env"]` first (the Worker/`pywrangler dev` path) and falls
back to `os.environ` (the plain-uvicorn/Docker path, populated from `.env`
via `load_dotenv()`, or `docker-compose.yml`'s `environment:` block). Both
`pywrangler dev` and `wrangler dev`/deploy read `.env` automatically for
local `vars`/secrets simulation; the deployed Worker instead needs
`wrangler secret put <NAME>` (not committed anywhere, not read from
`.env`).

## Database

Two backends share one query interface (`src/database.py`):

- **Local dev** (`DATABASE_MODE=sqlite`): a SQLite file. Under Docker
  Compose it lives in the `sqlite-data` named volume at `/data/local.db`
  (set via `docker-compose.yml`'s `environment:` block, overriding
  `.env`'s host-path default); without Docker it's `./local.db` on the
  host. D1 is SQLite under the hood, so the SQL dialect matches either way
  — no Cloudflare account needed for this path.
- **Deployed** (`DATABASE_MODE=d1_http`): real Cloudflare D1, accessed
  over its HTTP/REST API via `httpx` (same async-only reasoning as the
  Clerk JWKS fetch above). **Not yet exercised against a real D1
  database** — verify once one exists (`wrangler d1 create`).

Migrations live in `migrations/*.sql` and are plain SQLite DDL, applied
both by `database.py` (on first query, for local SQLite) and — once a real
D1 database exists — via `wrangler d1 migrations apply`.

```bash
# .env (local, without Docker)
DATABASE_MODE=sqlite
DATABASE_PATH=./local.db

# wrangler secrets (deployed) — not set yet, no D1 database created yet
DATABASE_MODE=d1_http
CF_ACCOUNT_ID=...
CF_D1_DATABASE_ID=...
CF_D1_API_TOKEN=...
```

## API

- `GET /health` — no auth
- `GET /beds` (optional `?ward_id=`), `GET /beds/{id}`, `PATCH
  /beds/{id}/status` (body `{"status": "ready"|"preparing"|"occupied"}`) —
  all require a Clerk session token. Bed fields include computed
  `occupied_minutes` and `is_overdue` (overdue = occupied longer than
  `expected_duration_minutes`, default 120 — see `src/policy.py`).

Every response uses the house envelope — see **Response conventions**
below and `docs/backend-conventions.md` for the full rationale:

```json
{
  "_data": { "bed": [ { "id": "...", "bed_number": "3", "ward_id": "icu",
    "status": "occupied", "links": { "self": {...}, "update": {...} } } ] },
  "_metadata": { "status": { "mandatory": true,
    "values": [{ "id": "ready", "value": "Ready" }, ...] } },
  "_metaLinks": { "self": { "href": "/beds" } }
}
```

## Response conventions

Every endpoint returns `_data` (keyed by the resource's singular name,
`bed` here — a list or a single item depending on the route),
`_metadata` (field validation + selector options, e.g. `status`'s allowed
values with display labels), and `_metaLinks` (the collection URL). This
is why `ward_id`/`status`/`bed_number` are plain codes rather than
formatted text — the UI translates `ward_id` itself and gets `status`'s
display labels straight from `_metadata`, so a client-side selector never
hardcodes an enum's options. Reusable builders live in `src/payload.py`.
Full writeup: `docs/backend-conventions.md`.

## Deploying

Deployed via Cloudflare **Workers Builds** (Git integration), connected to this
repo with:

- Root directory: `resource-management-api`
- Build command: `pip install uv && uv sync` (build image has Python 3.13
  preinstalled, but not `uv`)
- Deploy command: `uv run pywrangler deploy` (not the default `npx wrangler
  deploy` — `pywrangler` vendors dependencies into `python_modules/` first)

Secrets (`CLERK_JWKS_URL`, `DATABASE_MODE`, and the `CF_*` D1 credentials once a
real D1 database exists) are set via `wrangler secret put <NAME>` or the
dashboard's Settings → Variables and Secrets — see `DEPLOYMENT.md` at the repo
root for the full walkthrough. The `D1Database` backend itself is still
unverified against a real D1 instance.
