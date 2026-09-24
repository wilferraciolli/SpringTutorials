import uuid
from datetime import UTC, datetime
from typing import Any

from fastapi import APIRouter, Depends, HTTPException

from auth import get_current_user_id, require_admin_user_id
from database import Database, get_database
from models import ProcedureCategory, ProcedureType, ProcedureTypeCreate, ProcedureTypeUpdate
from payload import build_envelope, build_resource_links, field_metadata, make_link

router = APIRouter(prefix="/procedures", tags=["procedures"])

PROCEDURE_METADATA = {
    "category": field_metadata(mandatory=True, values=[(c.value, c.value) for c in ProcedureCategory])
}


def _row_to_procedure(row: dict) -> ProcedureType:
    return ProcedureType(**{**row, "is_active": bool(row["is_active"])})


def _procedure_payload(procedure: ProcedureType) -> dict[str, Any]:
    return {
        **procedure.model_dump(),
        "links": build_resource_links(
            self_href=f"/procedures/{procedure.id}",
            update_href=f"/procedures/{procedure.id}",
            delete_href=f"/procedures/{procedure.id}",
        ),
    }


@router.get("")
async def list_procedures(
    category: ProcedureCategory | None = None,
    is_active: bool | None = None,
    search: str | None = None,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    conditions: list[str] = []
    params: list[Any] = []
    if category:
        conditions.append("category = ?")
        params.append(category.value)
    if is_active is not None:
        conditions.append("is_active = ?")
        params.append(is_active)
    if search:
        conditions.append("name LIKE ?")
        params.append(f"%{search}%")
    where_clause = f" WHERE {' AND '.join(conditions)}" if conditions else ""

    rows = await db.fetch_all(f"SELECT * FROM procedure_types{where_clause} ORDER BY name", tuple(params))
    procedures = [_procedure_payload(_row_to_procedure(row)) for row in rows]
    return build_envelope(
        "procedure", procedures, metadata=PROCEDURE_METADATA, meta_links={"self": make_link("/procedures")}
    )


@router.get("/{procedure_id}")
async def get_procedure(
    procedure_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM procedure_types WHERE id = ?", (procedure_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="procedure type not found")
    procedure = _procedure_payload(_row_to_procedure(row))
    return build_envelope(
        "procedure", procedure, metadata=PROCEDURE_METADATA, meta_links={"self": make_link("/procedures")}
    )


@router.post("")
async def create_procedure(
    body: ProcedureTypeCreate,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    now = datetime.now(UTC).isoformat()
    procedure_id = str(uuid.uuid4())
    # Rule (Procedure Type Validation): min/max default to 50%/150% of the
    # typical duration when not given explicitly.
    min_duration = body.min_duration_minutes
    if min_duration is None:
        min_duration = round(body.typical_duration_minutes * 0.5)
    max_duration = body.max_duration_minutes
    if max_duration is None:
        max_duration = round(body.typical_duration_minutes * 1.5)

    await db.execute(
        "INSERT INTO procedure_types "
        "(id, name, description, category, typical_duration_minutes, min_duration_minutes, "
        "max_duration_minutes, is_active, created_at, updated_at) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        (
            procedure_id,
            body.name,
            body.description,
            body.category.value,
            body.typical_duration_minutes,
            min_duration,
            max_duration,
            True,
            now,
            now,
        ),
    )
    row = await db.fetch_one("SELECT * FROM procedure_types WHERE id = ?", (procedure_id,))
    procedure = _procedure_payload(_row_to_procedure(row))
    return build_envelope(
        "procedure", procedure, metadata=PROCEDURE_METADATA, meta_links={"self": make_link("/procedures")}
    )


@router.patch("/{procedure_id}")
async def update_procedure(
    procedure_id: str,
    body: ProcedureTypeUpdate,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM procedure_types WHERE id = ?", (procedure_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="procedure type not found")

    updates = body.model_dump(exclude_unset=True, mode="json")
    if updates:
        updates["updated_at"] = datetime.now(UTC).isoformat()
        set_clause = ", ".join(f"{field} = ?" for field in updates)
        await db.execute(
            f"UPDATE procedure_types SET {set_clause} WHERE id = ?", (*updates.values(), procedure_id)
        )

    updated_row = await db.fetch_one("SELECT * FROM procedure_types WHERE id = ?", (procedure_id,))
    procedure = _procedure_payload(_row_to_procedure(updated_row))
    return build_envelope(
        "procedure", procedure, metadata=PROCEDURE_METADATA, meta_links={"self": make_link("/procedures")}
    )


@router.delete("/{procedure_id}", status_code=204)
async def delete_procedure(
    procedure_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> None:
    row = await db.fetch_one("SELECT * FROM procedure_types WHERE id = ?", (procedure_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="procedure type not found")
    # Soft delete (like doctors) — beds already assigned to this procedure
    # keep their historical bed_duration_history rows regardless.
    await db.execute(
        "UPDATE procedure_types SET is_active = ?, updated_at = ? WHERE id = ?",
        (False, datetime.now(UTC).isoformat(), procedure_id),
    )
