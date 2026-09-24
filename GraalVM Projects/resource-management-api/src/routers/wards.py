import uuid
from datetime import UTC, datetime
from typing import Any

from fastapi import APIRouter, Depends, HTTPException

from auth import require_admin_user_id
from database import Database, get_database
from models import Ward, WardCreate, WardUpdate
from payload import build_envelope, build_resource_links, make_link

router = APIRouter(prefix="/admin/wards", tags=["wards"])


async def _bed_counts(db: Database, ward_id: str) -> tuple[int, int]:
    row = await db.fetch_one(
        "SELECT COUNT(*) AS total, "
        "SUM(CASE WHEN status = 'ready' THEN 1 ELSE 0 END) AS available "
        "FROM beds WHERE ward_id = ?",
        (ward_id,),
    )
    if row is None or row["total"] is None:
        return 0, 0
    return int(row["total"]), int(row["available"] or 0)


async def _row_to_ward(db: Database, row: dict) -> Ward:
    bed_count, available_beds = await _bed_counts(db, row["id"])
    return Ward(
        id=row["id"],
        name=row["name"],
        description=row["description"],
        created_at=row["created_at"],
        updated_at=row["updated_at"],
        bed_count=bed_count,
        available_beds=available_beds,
    )


def _ward_payload(ward: Ward) -> dict[str, Any]:
    return {
        **ward.model_dump(mode="json"),
        "links": build_resource_links(
            self_href=f"/admin/wards/{ward.id}",
            update_href=f"/admin/wards/{ward.id}",
            delete_href=f"/admin/wards/{ward.id}",
        ),
    }


@router.get("")
async def list_wards(
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    rows = await db.fetch_all("SELECT * FROM wards ORDER BY name", ())
    wards = [_ward_payload(await _row_to_ward(db, row)) for row in rows]
    return build_envelope("ward", wards, meta_links={"self": make_link("/admin/wards")})


@router.get("/{ward_id}")
async def get_ward(
    ward_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM wards WHERE id = ?", (ward_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="ward not found")
    ward = _ward_payload(await _row_to_ward(db, row))
    return build_envelope("ward", ward, meta_links={"self": make_link("/admin/wards")})


@router.post("")
async def create_ward(
    body: WardCreate,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    now = datetime.now(UTC).isoformat()
    ward_id = str(uuid.uuid4())
    await db.execute(
        "INSERT INTO wards (id, name, description, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
        (ward_id, body.name, body.description, now, now),
    )
    row = await db.fetch_one("SELECT * FROM wards WHERE id = ?", (ward_id,))
    assert row is not None
    ward = _ward_payload(await _row_to_ward(db, row))
    return build_envelope("ward", ward, meta_links={"self": make_link("/admin/wards")})


@router.patch("/{ward_id}")
async def update_ward(
    ward_id: str,
    body: WardUpdate,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM wards WHERE id = ?", (ward_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="ward not found")

    updates = body.model_dump(exclude_unset=True)
    if updates:
        updates["updated_at"] = datetime.now(UTC).isoformat()
        set_clause = ", ".join(f"{field} = ?" for field in updates)
        await db.execute(f"UPDATE wards SET {set_clause} WHERE id = ?", (*updates.values(), ward_id))

    updated_row = await db.fetch_one("SELECT * FROM wards WHERE id = ?", (ward_id,))
    assert updated_row is not None
    ward = _ward_payload(await _row_to_ward(db, updated_row))
    return build_envelope("ward", ward, meta_links={"self": make_link("/admin/wards")})


@router.delete("/{ward_id}", status_code=204)
async def delete_ward(
    ward_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> None:
    row = await db.fetch_one("SELECT * FROM wards WHERE id = ?", (ward_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="ward not found")

    bed_count, _ = await _bed_counts(db, ward_id)
    if bed_count > 0:
        raise HTTPException(status_code=409, detail=f"ward still has {bed_count} bed(s) assigned")

    await db.execute("DELETE FROM wards WHERE id = ?", (ward_id,))
