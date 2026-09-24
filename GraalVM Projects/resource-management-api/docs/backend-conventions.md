# FastAPI backend conventions (portable)

Reusable conventions extracted from this project's backend setup, stripped
of project-specific names where reasonable. Copy this file into another
FastAPI app and adjust the concrete resource/field names to that app's
domain.

Assumes: FastAPI + Pydantic v2, deployed as a Cloudflare Python Worker but
designed to run unmodified under plain `uvicorn` (or any other host) too —
see Portability below. `uv` for dependency management, Docker for local
dev, D1 (or any SQLite-compatible store) for data.

## Response envelope — every endpoint follows this shape

```json
{
  "_data": { "<resource_key>": { "...": "..." } },
  "_metadata": {
    "<field>": {
      "mandatory": true,
      "values": [{ "id": "READY", "value": "Ready" }]
    }
  },
  "_metaLinks": { "self": { "href": "/beds" } }
}
```

- **`_data`** is keyed by the resource's **singular** name, for both a
  single item and a list of them — e.g. `_data.bed` is an array for
  `GET /beds`, a single object for `GET /beds/{id}`. Same shape either
  way; only the value's cardinality differs.
- Every item under `_data` carries its own **`links`** object
  (`self`/`update`/`delete`), one entry **per operation that's actually
  implemented** for that resource. Never fabricate a link to an endpoint
  that doesn't exist — if there's no delete route, there's no `delete`
  link.
- **`_metadata`** describes the resource's *fields*, not its instances —
  it's keyed by field name and appears once per response, not once per
  item. Use it for anything a client needs to validate input or build a
  selector: `mandatory` (is the field required on write), and `values` (an
  `[{id, value}]` list) when the field has a fixed set of options — `id`
  is the stable code to send back on write, `value` is the display label.
  A client can build a dropdown straight from this without hardcoding
  option labels or duplicating the enum.
- **`_metaLinks`** always points at the **collection** endpoint (e.g.
  `/beds`), regardless of whether the response itself is a list or a
  single item — it's "where does this kind of resource live", not
  "where does this specific instance live" (that's `links.self`).

Reusable helpers live in `src/payload.py` — `make_link`,
`build_resource_links`, `field_metadata`, `build_envelope`. A route builds
its response as:

```python
from payload import build_envelope, build_resource_links, field_metadata, make_link

RESOURCE_METADATA = {
    "status": field_metadata(mandatory=True, values=[("ready", "Ready"), ...]),
}

def _item_payload(item: Model) -> dict:
    return {
        **item.model_dump(),
        "links": build_resource_links(
            self_href=f"/things/{item.id}",
            update_href=f"/things/{item.id}",  # omit if no update route
        ),
    }

@router.get("")
async def list_things(...) -> dict:
    items = [_item_payload(x) for x in ...]
    return build_envelope("thing", items, metadata=RESOURCE_METADATA, meta_links={"self": make_link("/things")})
```

Because the envelope's top-level key is dynamic (the resource name), route
handlers return a plain `dict` rather than a Pydantic `response_model` —
the domain model (`Bed`, etc.) still gives you validation and typing for
the item itself, just not for the outer envelope.

## Portability — one Cloudflare-specific file

```
src/
├── main.py                 # FastAPI app — 100% portable
├── auth.py                 # session-token verification
├── models.py                # Pydantic models
├── database.py              # DB access
├── payload.py                # response-envelope helpers
├── routers/
│   └── things.py             # CRUD routes
└── cloudflare_entry.py      # ← ONLY Cloudflare-specific file
```

`cloudflare_entry.py` is a thin ASGI adapter (`from workers import asgi;
from main import app; Default = asgi.entrypoint(app)`). Everything else
runs unmodified under `uvicorn main:app` — no Cloudflare imports anywhere
outside that one file.

## Config access — per-request, not module-level

Cloudflare Workers expose `vars`/secrets through a **per-request** `env`
object (`request.scope["env"]`), **not** `os.environ` — confirmed by
testing directly against a real Worker: a module-level
`os.environ["X"]` read at import time raised `KeyError` even for a var
Wrangler had picked up from `.env` and listed as a binding. So config is
read via `get_config(request, key)` (`src/config.py`), which checks
`request.scope["env"]` first, then falls back to `os.environ` (the
plain-uvicorn/Docker path, populated by `load_dotenv()`). Never read
config as a module-level constant — thread `request: Request` through to
wherever the value is needed instead.

## Outbound HTTP — async only

Cloudflare's Python Workers run on Pyodide, which doesn't support
`urllib`/sync-socket-based HTTP clients — confirmed by watching PyJWT's
built-in `PyJWKClient` (urllib-based) crash with
`http.client.RemoteDisconnected` inside a real Worker. Any outbound HTTP
call — JWKS fetches, calling another API, D1's REST API — must use
**async httpx** (`httpx.AsyncClient`) or `aiohttp`. Never a sync client,
even for something that looks like it should be trivially synchronous.

## Database — two backends, one interface

`src/database.py` defines a `Database` protocol
(`fetch_all`/`fetch_one`/`execute`) with two implementations, chosen by a
`DATABASE_MODE` config value:

- **`sqlite`** — local dev. Runs against a SQLite file, which under Docker
  Compose lives in a **named volume**, not a bind-mounted host file — same
  behavior on Linux/Windows/macOS, no host-filesystem db file to lose
  track of between machines.
- **`d1_http`** — deployed. Talks to Cloudflare D1 over its HTTP/REST API
  via async httpx (see above), not the native Workers binding — this
  keeps `database.py` (and everything above it) identical whether it's
  running locally, in the deployed Worker, or moved to a different host
  entirely. The tradeoff (binding is lower-latency) is accepted for a
  simpler, fully portable codebase.

Migrations are plain SQL in `migrations/*.sql` — D1 is SQLite-compatible,
so the same files apply both locally (a plain `executescript()` on first
query) and remotely (`wrangler d1 migrations apply`).

## Auth

A FastAPI dependency (`get_current_user_id` in `auth.py`) verifies a
bearer session token against the auth provider's JWKS endpoint and is
attached to every route that needs a signed-in user. `/health` is the only
unauthenticated route.

## Local dev

`docker compose up --build` — see the project README for the full
walkthrough. `src/`, `migrations/`, and `scripts/` are bind-mounted for
live-reload; only `pyproject.toml`/`uv.lock` changes need a rebuild.
