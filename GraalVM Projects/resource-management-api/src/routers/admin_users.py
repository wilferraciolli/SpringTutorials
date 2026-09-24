"""Admin user directory — list users and grant/revoke the admin role.

Until now the only way to grant admin was scripts/promote_admin.py, run
directly against the database (see
docs/features/user-profile-admin-role.md's "User Directory" future
enhancement — this is that enhancement). Clerk only proves identity; role
is our own app data (see migrations/0001_initial_schema.sql).
"""

from datetime import UTC, datetime
from typing import Any

from fastapi import APIRouter, Depends, HTTPException

from auth import require_admin_user_id
from database import Database, get_database
from models import User, UserRoleUpdate
from payload import build_envelope, build_resource_links, make_link
from routers.me import row_to_user

router = APIRouter(prefix="/admin/users", tags=["admin-users"])


def _user_payload(user: User) -> dict[str, Any]:
    return {
        **user.model_dump(mode="json"),
        "links": build_resource_links(
            self_href=f"/admin/users/{user.id}", update_href=f"/admin/users/{user.id}/role"
        ),
    }


@router.get("")
async def list_users(
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    rows = await db.fetch_all("SELECT * FROM users ORDER BY name", ())
    users = [_user_payload(row_to_user(row)) for row in rows]
    return build_envelope("user", users, meta_links={"self": make_link("/admin/users")})


@router.patch("/{user_id}/role")
async def update_user_role(
    user_id: str,
    body: UserRoleUpdate,
    db: Database = Depends(get_database),
    caller_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    # Prevents an admin from locking themselves out by demoting their own
    # account — someone else with admin access can still demote them.
    if user_id == caller_id:
        raise HTTPException(status_code=400, detail="cannot change your own role")

    row = await db.fetch_one("SELECT * FROM users WHERE id = ?", (user_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="user not found")

    await db.execute(
        "UPDATE users SET role = ?, updated_at = ? WHERE id = ?",
        (body.role.value, datetime.now(UTC).isoformat(), user_id),
    )
    updated_row = await db.fetch_one("SELECT * FROM users WHERE id = ?", (user_id,))
    assert updated_row is not None
    user = row_to_user(updated_row)
    return build_envelope("user", _user_payload(user), meta_links={"self": make_link("/admin/users")})
