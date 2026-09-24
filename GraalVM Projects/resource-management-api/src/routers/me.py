from datetime import UTC, datetime
from typing import Any

from fastapi import APIRouter, Depends

from auth import get_current_claims, get_current_user_id
from database import Database, get_database
from models import AdminProfile, User, UserUpdate
from payload import build_envelope, make_link

router = APIRouter(tags=["me"])

ADMIN_PERMISSIONS = ["beds:read", "beds:write", "wards:read", "wards:write"]


def row_to_user(row: dict) -> User:
    admin_profile = None
    if row["role"] == "admin":
        admin_profile = AdminProfile(
            can_manage_wards=True,
            can_manage_beds=True,
            can_manage_users=False,
            permissions=ADMIN_PERMISSIONS,
        )
    return User(
        id=row["id"],
        name=row["name"],
        email=row["email"],
        phone=row["phone"],
        job_title=row["job_title"],
        role=row["role"],
        ward_id=row["ward_id"],
        language=row["language"],
        admin_profile=admin_profile,
    )


async def _get_or_provision_user(db: Database, user_id: str, claims: dict[str, Any]) -> dict:
    row = await db.fetch_one("SELECT * FROM users WHERE id = ?", (user_id,))
    if row is not None:
        return row

    # First time this caller has hit an endpoint that needs a profile —
    # Clerk session tokens don't reliably carry name/email unless a custom
    # claims template is configured, so these fall back to placeholders
    # the user can fill in via PATCH /me.
    now = datetime.now(UTC).isoformat()
    name = claims.get("name") or claims.get("email") or "New user"
    email = claims.get("email") or ""

    # Bootstraps a fresh deployment: with no users yet, there's no one to
    # run scripts/promote_admin.py (or the admin/users screen) as, so the
    # very first caller becomes admin automatically. Every subsequent
    # sign-up is a plain 'user' — promote them via /admin/users (see
    # routers/admin_users.py) or the script.
    count_row = await db.fetch_one("SELECT COUNT(*) AS count FROM users", ())
    role = "admin" if count_row is None or count_row["count"] == 0 else "user"

    await db.execute(
        "INSERT INTO users (id, name, email, phone, job_title, role, ward_id, created_at, updated_at) "
        "VALUES (?, ?, ?, NULL, NULL, ?, NULL, ?, ?)",
        (user_id, name, email, role, now, now),
    )
    row = await db.fetch_one("SELECT * FROM users WHERE id = ?", (user_id,))
    assert row is not None
    return row


@router.get("/me")
async def get_me(
    db: Database = Depends(get_database),
    user_id: str = Depends(get_current_user_id),
    claims: dict[str, Any] = Depends(get_current_claims),
) -> dict[str, Any]:
    row = await _get_or_provision_user(db, user_id, claims)
    user = row_to_user(row)
    return build_envelope("user", user.model_dump(mode="json"), meta_links={"self": make_link("/me")})


@router.patch("/me")
async def update_me(
    body: UserUpdate,
    db: Database = Depends(get_database),
    user_id: str = Depends(get_current_user_id),
    claims: dict[str, Any] = Depends(get_current_claims),
) -> dict[str, Any]:
    await _get_or_provision_user(db, user_id, claims)

    updates = body.model_dump(exclude_unset=True, mode="json")
    if updates:
        updates["updated_at"] = datetime.now(UTC).isoformat()
        set_clause = ", ".join(f"{field} = ?" for field in updates)
        await db.execute(f"UPDATE users SET {set_clause} WHERE id = ?", (*updates.values(), user_id))

    row = await db.fetch_one("SELECT * FROM users WHERE id = ?", (user_id,))
    assert row is not None
    user = row_to_user(row)
    return build_envelope("user", user.model_dump(mode="json"), meta_links={"self": make_link("/me")})
