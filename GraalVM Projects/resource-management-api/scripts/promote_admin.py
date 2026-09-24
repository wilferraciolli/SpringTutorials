"""Promote a user to the 'admin' role in the local SQLite database.

There's no user-directory UI yet (see docs/features/user-profile-admin-role.md's
Future Enhancements), so this is the only way to grant admin access locally.
The user must have hit GET /me at least once already (so their `users` row
exists) — run this after signing in through the app once.

Usage:
    uv run python scripts/promote_admin.py <clerk_user_id>
    # or, against the Docker Compose volume:
    docker compose exec api uv run python scripts/promote_admin.py <clerk_user_id>
"""

import os
import sqlite3
import sys
from datetime import UTC, datetime

DB_PATH = os.environ.get("DATABASE_PATH", "./local.db")


def main() -> None:
    if len(sys.argv) != 2:
        print("usage: promote_admin.py <clerk_user_id>", file=sys.stderr)
        sys.exit(1)
    user_id = sys.argv[1]

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.execute("SELECT id FROM users WHERE id = ?", (user_id,))
    if cursor.fetchone() is None:
        print(f"No user {user_id!r} found — sign in and load GET /me at least once first.", file=sys.stderr)
        sys.exit(1)

    conn.execute(
        "UPDATE users SET role = 'admin', updated_at = ? WHERE id = ?",
        (datetime.now(UTC).isoformat(), user_id),
    )
    conn.commit()
    conn.close()
    print(f"Promoted {user_id} to admin")


if __name__ == "__main__":
    main()
