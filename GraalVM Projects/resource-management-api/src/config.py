"""Config access — portable across plain uvicorn and the deployed Worker.

Cloudflare Workers exposes vars/secrets through a per-request `env`
object, not through `os.environ` (confirmed empirically: reading
`os.environ` at module import time raises `KeyError` inside the real
Worker, even for a var declared in wrangler.jsonc/.env). The ASGI adapter
(`workers.asgi`) puts that `env` object into `request.scope["env"]` for
every request, so config here is read per-request via a plain dict lookup
rather than as module-level constants — this file never imports `workers`
directly, so it still runs unmodified under plain uvicorn.

Locally (plain uvicorn), there is no `env` in scope, so this falls back to
`os.environ`, populated from `.env` via `load_dotenv()`.
"""

import os

from dotenv import load_dotenv
from fastapi import Request

load_dotenv()


def get_config(request: Request, key: str, default: str | None = None) -> str:
    env = request.scope.get("env")
    if env is not None:
        value = getattr(env, key, None)
        if value is not None:
            return value
    value = os.environ.get(key)
    if value is not None:
        return value
    if default is not None:
        return default
    raise RuntimeError(f"missing required config value: {key}")
