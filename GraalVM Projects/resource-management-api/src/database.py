"""Database access — portable across plain uvicorn/Docker and the deployed Worker.

Two backends share one query interface:
- `SQLiteDatabase`: local dev. A SQLite file — persisted in a named Docker
  volume by docker-compose.yml, not a bare host file, so behavior is
  identical whether you're on Linux, Windows, or macOS.
- `D1Database`: the deployed Worker, talking to Cloudflare D1 over its
  HTTP/REST API via async httpx (see auth.py for why it must be async —
  same Pyodide-runtime constraint applies here). D1 is SQLite-compatible,
  so the same migration SQL in migrations/ works for both backends.
  NOTE: not yet exercised against a real D1 database — verify once one
  exists.

`get_database(request)` is a FastAPI dependency that picks the backend
from the `DATABASE_MODE` config value ("sqlite" | "d1_http").
"""

from pathlib import Path
from typing import Any, Protocol

import aiosqlite
import httpx
from fastapi import Request

from config import get_config

MIGRATIONS_DIR = Path(__file__).resolve().parent.parent / "migrations"


class Database(Protocol):
    async def fetch_all(self, sql: str, params: tuple[Any, ...] = ()) -> list[dict[str, Any]]: ...
    async def fetch_one(self, sql: str, params: tuple[Any, ...] = ()) -> dict[str, Any] | None: ...
    async def execute(self, sql: str, params: tuple[Any, ...] = ()) -> None: ...


_migrated_paths: set[str] = set()


class SQLiteDatabase:
    def __init__(self, path: str) -> None:
        self._path = path

    async def _ensure_migrated(self) -> None:
        if self._path in _migrated_paths:
            return
        Path(self._path).parent.mkdir(parents=True, exist_ok=True)
        async with aiosqlite.connect(self._path) as conn:
            # Tracked durably (not just the in-memory `_migrated_paths`
            # cache below) so each migration file runs at most once ever
            # against a given db file — required once a migration isn't
            # itself idempotent (e.g. a plain `ALTER TABLE ADD COLUMN`,
            # which errors if replayed against an already-migrated file
            # on the next process restart).
            await conn.execute("CREATE TABLE IF NOT EXISTS schema_migrations (filename TEXT PRIMARY KEY)")
            cursor = await conn.execute("SELECT filename FROM schema_migrations")
            applied = {row[0] for row in await cursor.fetchall()}
            for migration in sorted(MIGRATIONS_DIR.glob("*.sql")):
                if migration.name in applied:
                    continue
                await conn.executescript(migration.read_text())
                await conn.execute("INSERT INTO schema_migrations (filename) VALUES (?)", (migration.name,))
            await conn.commit()
        _migrated_paths.add(self._path)

    async def fetch_all(self, sql: str, params: tuple[Any, ...] = ()) -> list[dict[str, Any]]:
        await self._ensure_migrated()
        async with aiosqlite.connect(self._path) as conn:
            conn.row_factory = aiosqlite.Row
            cursor = await conn.execute(sql, params)
            rows = await cursor.fetchall()
            return [dict(row) for row in rows]

    async def fetch_one(self, sql: str, params: tuple[Any, ...] = ()) -> dict[str, Any] | None:
        rows = await self.fetch_all(sql, params)
        return rows[0] if rows else None

    async def execute(self, sql: str, params: tuple[Any, ...] = ()) -> None:
        await self._ensure_migrated()
        async with aiosqlite.connect(self._path) as conn:
            await conn.execute(sql, params)
            await conn.commit()


class D1Database:
    def __init__(self, account_id: str, database_id: str, api_token: str) -> None:
        self._url = (
            f"https://api.cloudflare.com/client/v4/accounts/{account_id}"
            f"/d1/database/{database_id}/query"
        )
        self._headers = {"Authorization": f"Bearer {api_token}"}

    async def _query(self, sql: str, params: tuple[Any, ...]) -> list[dict[str, Any]]:
        async with httpx.AsyncClient(timeout=10) as client:
            response = await client.post(
                self._url,
                headers=self._headers,
                json={"sql": sql, "params": list(params)},
            )
            response.raise_for_status()
        body = response.json()
        if not body.get("success"):
            raise RuntimeError(f"D1 query failed: {body.get('errors')}")
        return body["result"][0]["results"]

    async def fetch_all(self, sql: str, params: tuple[Any, ...] = ()) -> list[dict[str, Any]]:
        return await self._query(sql, params)

    async def fetch_one(self, sql: str, params: tuple[Any, ...] = ()) -> dict[str, Any] | None:
        rows = await self._query(sql, params)
        return rows[0] if rows else None

    async def execute(self, sql: str, params: tuple[Any, ...] = ()) -> None:
        await self._query(sql, params)


def get_database(request: Request) -> Database:
    mode = get_config(request, "DATABASE_MODE")
    if mode == "sqlite":
        return SQLiteDatabase(get_config(request, "DATABASE_PATH"))
    if mode == "d1_http":
        return D1Database(
            account_id=get_config(request, "CF_ACCOUNT_ID"),
            database_id=get_config(request, "CF_D1_DATABASE_ID"),
            api_token=get_config(request, "CF_D1_API_TOKEN"),
        )
    raise RuntimeError(f"unknown DATABASE_MODE: {mode!r}")
