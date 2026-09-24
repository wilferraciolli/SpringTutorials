"""Seed demo beds into the local SQLite database for manual testing.

Usage:
    uv run python scripts/seed_beds.py
    # or, against the Docker Compose volume:
    docker compose exec api uv run python scripts/seed_beds.py
"""

import os
import sqlite3
import uuid
from datetime import UTC, datetime
from pathlib import Path

MIGRATIONS_DIR = Path(__file__).resolve().parent.parent / "migrations"
DB_PATH = os.environ.get("DATABASE_PATH", "./local.db")

DEMO_BEDS = [
    ("1", "icu"),
    ("2", "icu"),
    ("3", "er"),
    ("4", "er"),
    ("5", "er"),
]


def _apply_migrations(conn: sqlite3.Connection) -> None:
    # Mirrors database.py's SQLiteDatabase._ensure_migrated — the API
    # container already tracks applied migrations in this table, so
    # replaying every migration file unconditionally (the naive approach)
    # crashes on a non-idempotent `ALTER TABLE ADD COLUMN` the second time
    # this runs against a db the app has already migrated (e.g. the
    # docker-compose volume after the api service has started once).
    conn.execute("CREATE TABLE IF NOT EXISTS schema_migrations (filename TEXT PRIMARY KEY)")
    applied = {row[0] for row in conn.execute("SELECT filename FROM schema_migrations")}
    for migration in sorted(MIGRATIONS_DIR.glob("*.sql")):
        if migration.name in applied:
            continue
        conn.executescript(migration.read_text())
        conn.execute("INSERT INTO schema_migrations (filename) VALUES (?)", (migration.name,))


def main() -> None:
    Path(DB_PATH).parent.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    _apply_migrations(conn)

    now = datetime.now(UTC).isoformat()
    for bed_number, ward_id in DEMO_BEDS:
        conn.execute(
            "INSERT INTO beds "
            "(id, bed_number, ward_id, status, status_changed_at, expected_duration_minutes, created_at, updated_at) "
            "VALUES (?, ?, ?, 'ready', ?, NULL, ?, ?)",
            (str(uuid.uuid4()), bed_number, ward_id, now, now, now),
        )
    conn.commit()
    conn.close()
    print(f"Seeded {len(DEMO_BEDS)} beds into {DB_PATH}")


if __name__ == "__main__":
    main()
