"""Clerk session-token verification for the walking-skeleton spike.

Cloudflare Python Workers only support outbound HTTP requests through
async-compatible libraries (httpx, aiohttp) or the JS fetch FFI — PyJWT's
built-in `PyJWKClient` uses `urllib` under the hood, which fails inside the
Worker runtime (confirmed by running this spike against a real Worker: it
raised `http.client.RemoteDisconnected`). So JWKS fetching is done here
directly with an async httpx client, and the matching key is handed to
`jwt.decode()` via `jwt.algorithms.RSAAlgorithm`.
"""

import time
from typing import Any

import httpx
import jwt
from fastapi import Depends, Header, HTTPException, Request
from jwt.algorithms import RSAAlgorithm

from config import get_config
from database import Database, get_database

_JWKS_TTL_SECONDS = 3600

_jwks_cache: dict[str, Any] = {}
_jwks_fetched_at = 0.0


async def _get_jwks(jwks_url: str, force_refresh: bool = False) -> dict[str, Any]:
    global _jwks_cache, _jwks_fetched_at
    now = time.time()
    if force_refresh or not _jwks_cache or now - _jwks_fetched_at > _JWKS_TTL_SECONDS:
        async with httpx.AsyncClient(timeout=5) as client:
            response = await client.get(jwks_url)
            response.raise_for_status()
        _jwks_cache = response.json()
        _jwks_fetched_at = now
    return _jwks_cache


async def _get_signing_key(jwks_url: str, kid: str) -> Any:
    for force_refresh in (False, True):  # retry once in case of key rotation
        jwks = await _get_jwks(jwks_url, force_refresh=force_refresh)
        for key in jwks.get("keys", []):
            if key.get("kid") == kid:
                return RSAAlgorithm.from_jwk(key)
    raise HTTPException(status_code=401, detail="no matching signing key")


async def verify_session_token(jwks_url: str, token: str, audience: str) -> dict[str, Any]:
    try:
        kid = jwt.get_unverified_header(token)["kid"]
        signing_key = await _get_signing_key(jwks_url, kid)
        return jwt.decode(token, signing_key, algorithms=["RS256"], audience=audience)
    except jwt.PyJWTError as exc:
        raise HTTPException(status_code=401, detail=f"invalid session token: {exc}") from exc


async def get_current_claims(request: Request, authorization: str = Header(...)) -> dict[str, Any]:
    if not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="missing bearer token")
    jwks_url = get_config(request, "CLERK_JWKS_URL")
    # Must match the `aud` claim baked into the Clerk JWT template the
    # frontend requests via `getToken({ template: 'resource-management-api' })`
    # (see auth.store.ts) — Clerk's *default* session token (no template)
    # carries no custom claims (name/email) and isn't scoped to this API by
    # `aud`, so a token minted for some other Clerk-backed app couldn't be
    # replayed against this one. Rejecting on audience mismatch is the
    # actual security purpose; getting the extra claims is a side benefit.
    audience = get_config(request, "CLERK_AUDIENCE", default="resource-management-api")
    return await verify_session_token(jwks_url, authorization.removeprefix("Bearer "), audience)


async def get_current_user_id(claims: dict[str, Any] = Depends(get_current_claims)) -> str:
    return claims["sub"]


async def require_admin_user_id(
    user_id: str = Depends(get_current_user_id),
    db: Database = Depends(get_database),
) -> str:
    """Admin-only route guard — checks the role recorded in `users`, not the
    Clerk token itself (Clerk only proves *who* the caller is; *what role*
    they have is our own app data, set via scripts/promote_admin.py until
    a user-directory UI exists — see docs/features/user-profile-admin-role.md).
    A caller with no `users` row yet (never hit GET /me) is treated as a
    plain user, same default JIT-provisioning would give them.
    """
    row = await db.fetch_one("SELECT role FROM users WHERE id = ?", (user_id,))
    role = row["role"] if row else "user"
    if role != "admin":
        raise HTTPException(status_code=403, detail="admin role required")
    return user_id
