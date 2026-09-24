import uuid
from datetime import UTC, datetime
from typing import Any

from fastapi import APIRouter, Depends, HTTPException

from auth import get_current_user_id
from database import Database, get_database
from models import (
    Bed,
    BedDurationUpdate,
    BedProcedureAssign,
    BedStatusUpdate,
    ChangeReason,
    ProcedureTypeSummary,
)
from payload import build_envelope, build_resource_links, field_metadata, make_link
from policy import clamp_duration, compute_overdue, compute_variance

router = APIRouter(prefix="/beds", tags=["beds"])

BED_METADATA = {
    "status": field_metadata(
        mandatory=True,
        values=[("ready", "Ready"), ("preparing", "Preparing"), ("occupied", "Occupied"), ("cleaning", "Cleaning")],
    )
}


async def _procedure_lookup(db: Database, procedure_type_ids: set[str]) -> dict[str, ProcedureTypeSummary]:
    ids = [pid for pid in procedure_type_ids if pid]
    if not ids:
        return {}
    placeholders = ", ".join("?" for _ in ids)
    rows = await db.fetch_all(
        f"SELECT id, name, typical_duration_minutes FROM procedure_types WHERE id IN ({placeholders})",
        tuple(ids),
    )
    return {row["id"]: ProcedureTypeSummary(**row) for row in rows}


def _row_to_bed(row: dict, procedures: dict[str, ProcedureTypeSummary]) -> Bed:
    metrics = compute_overdue(row["status"], row["status_changed_at"], row["expected_duration_minutes"])
    procedure_type_id = row["current_procedure_type_id"]
    return Bed(
        occupied_minutes=metrics.occupied_minutes,
        is_overdue=metrics.is_overdue,
        overdue_minutes=metrics.overdue_minutes,
        remaining_minutes=metrics.remaining_minutes,
        current_procedure_type_id=procedure_type_id,
        procedure_type=procedures.get(procedure_type_id) if procedure_type_id else None,
        **{
            k: row[k]
            for k in (
                "id",
                "bed_number",
                "ward_id",
                "status",
                "status_changed_at",
                "expected_duration_minutes",
                "procedure_notes",
                "duration_changed_at",
            )
        },
    )


def _bed_payload(bed: Bed) -> dict[str, Any]:
    # No `delete_href` — deleting a bed isn't an implemented operation, so
    # no link is advertised for it.
    return {
        **bed.model_dump(),
        "links": build_resource_links(
            self_href=f"/beds/{bed.id}",
            update_href=f"/beds/{bed.id}/status",
        ),
    }


async def _occupy_bed(
    db: Database,
    row: dict,
    *,
    procedure_type_id: str | None,
    expected_duration_minutes: int | None,
    procedure_notes: str | None,
    user_id: str,
    now: str,
) -> None:
    """Shared by PATCH /beds/{id}/status (status='occupied') and POST
    /beds/{id}/procedure — Rule 1.1: procedure's typical duration, else an
    explicit override, else the existing/default duration, clamped to
    15-1440 minutes (Rule 1.2).
    """
    duration = expected_duration_minutes
    if duration is None and procedure_type_id:
        procedure_row = await db.fetch_one(
            "SELECT typical_duration_minutes FROM procedure_types WHERE id = ?", (procedure_type_id,)
        )
        if procedure_row is None:
            raise HTTPException(status_code=404, detail="procedure type not found")
        duration = procedure_row["typical_duration_minutes"]
    if duration is None:
        duration = row["expected_duration_minutes"] or 120
    duration = clamp_duration(duration)

    await db.execute(
        "UPDATE beds SET status = 'occupied', status_changed_at = ?, updated_at = ?, "
        "current_procedure_type_id = ?, expected_duration_minutes = ?, procedure_notes = ?, "
        "duration_changed_at = ?, duration_changed_by = ? WHERE id = ?",
        (now, now, procedure_type_id, duration, procedure_notes, now, user_id, row["id"]),
    )
    await db.execute(
        "INSERT INTO bed_duration_history "
        "(id, bed_id, old_duration_minutes, new_duration_minutes, procedure_type_id, changed_by, change_reason, changed_at) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        (
            str(uuid.uuid4()),
            row["id"],
            row["expected_duration_minutes"],
            duration,
            procedure_type_id,
            user_id,
            ChangeReason.procedure_assignment.value,
            now,
        ),
    )


async def _record_discharge(db: Database, row: dict, *, now: str) -> None:
    """Rule 4 (docs/features/bed-timeframe-management.md) — run once, when
    a bed actually leaves 'occupied', using the *final* expected duration
    (it may have been adjusted mid-stay via PATCH /duration).
    """
    started_at = datetime.fromisoformat(row["status_changed_at"])
    actual_minutes = int((datetime.fromisoformat(now) - started_at).total_seconds() // 60)
    expected_minutes = row["expected_duration_minutes"] or 120
    variance = compute_variance(actual_minutes, expected_minutes)
    await db.execute(
        "INSERT INTO bed_occupancy_history "
        "(id, bed_id, ward_id, procedure_type_id, expected_duration_minutes, actual_duration_minutes, "
        "variance_minutes, variance_percent, variance_class, started_at, ended_at) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        (
            str(uuid.uuid4()),
            row["id"],
            row["ward_id"],
            row["current_procedure_type_id"],
            expected_minutes,
            actual_minutes,
            variance.variance_minutes,
            variance.variance_percent,
            variance.variance_class,
            row["status_changed_at"],
            now,
        ),
    )


@router.get("")
async def list_beds(
    ward_id: str | None = None,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    if ward_id:
        rows = await db.fetch_all("SELECT * FROM beds WHERE ward_id = ?", (ward_id,))
    else:
        rows = await db.fetch_all("SELECT * FROM beds", ())
    procedures = await _procedure_lookup(db, {row["current_procedure_type_id"] for row in rows})
    beds = [_bed_payload(_row_to_bed(row, procedures)) for row in rows]
    return build_envelope("bed", beds, metadata=BED_METADATA, meta_links={"self": make_link("/beds")})


@router.get("/{bed_id}")
async def get_bed(
    bed_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="bed not found")
    procedures = await _procedure_lookup(db, {row["current_procedure_type_id"]})
    bed = _bed_payload(_row_to_bed(row, procedures))
    return build_envelope("bed", bed, metadata=BED_METADATA, meta_links={"self": make_link("/beds")})


@router.patch("/{bed_id}/status")
async def update_bed_status(
    bed_id: str,
    body: BedStatusUpdate,
    db: Database = Depends(get_database),
    user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="bed not found")

    now = datetime.now(UTC).isoformat()
    if body.status.value == "occupied":
        await _occupy_bed(
            db,
            row,
            procedure_type_id=body.procedure_type_id,
            expected_duration_minutes=body.expected_duration_minutes,
            procedure_notes=body.procedure_notes,
            user_id=user_id,
            now=now,
        )
    else:
        if row["status"] == "occupied":
            await _record_discharge(db, row, now=now)
        # Discharged (or moved to preparing/cleaning) — current_procedure_type_id
        # etc. describe the bed's *current* occupancy, so they're cleared;
        # the assignment itself stays in bed_duration_history.
        await db.execute(
            "UPDATE beds SET status = ?, status_changed_at = ?, updated_at = ?, "
            "current_procedure_type_id = NULL, expected_duration_minutes = NULL, "
            "procedure_notes = NULL, duration_changed_at = NULL, duration_changed_by = NULL "
            "WHERE id = ?",
            (body.status.value, now, now, bed_id),
        )
    await db.execute(
        "INSERT INTO bed_status_history (id, bed_id, from_status, to_status, changed_at, changed_by) "
        "VALUES (?, ?, ?, ?, ?, ?)",
        (str(uuid.uuid4()), bed_id, row["status"], body.status.value, now, user_id),
    )
    updated = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    procedures = await _procedure_lookup(db, {updated["current_procedure_type_id"]})
    bed = _bed_payload(_row_to_bed(updated, procedures))
    return build_envelope("bed", bed, metadata=BED_METADATA, meta_links={"self": make_link("/beds")})


@router.post("/{bed_id}/procedure")
async def assign_procedure(
    bed_id: str,
    body: BedProcedureAssign,
    db: Database = Depends(get_database),
    user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="bed not found")

    now = datetime.now(UTC).isoformat()
    await _occupy_bed(
        db,
        row,
        procedure_type_id=body.procedure_type_id,
        expected_duration_minutes=body.expected_duration_minutes,
        procedure_notes=body.procedure_notes,
        user_id=user_id,
        now=now,
    )
    if row["status"] != "occupied":
        await db.execute(
            "INSERT INTO bed_status_history (id, bed_id, from_status, to_status, changed_at, changed_by) "
            "VALUES (?, ?, ?, 'occupied', ?, ?)",
            (str(uuid.uuid4()), bed_id, row["status"], now, user_id),
        )

    updated = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    procedures = await _procedure_lookup(db, {updated["current_procedure_type_id"]})
    bed = _bed_payload(_row_to_bed(updated, procedures))
    return build_envelope("bed", bed, metadata=BED_METADATA, meta_links={"self": make_link("/beds")})


@router.patch("/{bed_id}/duration")
async def update_bed_duration(
    bed_id: str,
    body: BedDurationUpdate,
    db: Database = Depends(get_database),
    user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="bed not found")
    if row["status"] != "occupied":
        # Rule 2.2 — duration only makes sense while the bed is occupied.
        raise HTTPException(status_code=409, detail="can only adjust duration for an occupied bed")

    new_duration = clamp_duration(body.expected_duration_minutes)
    now = datetime.now(UTC).isoformat()

    await db.execute(
        "UPDATE beds SET expected_duration_minutes = ?, duration_changed_at = ?, duration_changed_by = ?, "
        "updated_at = ? WHERE id = ?",
        (new_duration, now, user_id, now, bed_id),
    )
    await db.execute(
        "INSERT INTO bed_duration_history "
        "(id, bed_id, old_duration_minutes, new_duration_minutes, procedure_type_id, changed_by, change_reason, changed_at) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        (
            str(uuid.uuid4()),
            bed_id,
            row["expected_duration_minutes"],
            new_duration,
            row["current_procedure_type_id"],
            user_id,
            body.change_reason.value,
            now,
        ),
    )

    updated = await db.fetch_one("SELECT * FROM beds WHERE id = ?", (bed_id,))
    procedures = await _procedure_lookup(db, {updated["current_procedure_type_id"]})
    bed = _bed_payload(_row_to_bed(updated, procedures))
    return build_envelope("bed", bed, metadata=BED_METADATA, meta_links={"self": make_link("/beds")})


@router.get("/{bed_id}/duration-history")
async def get_duration_history(
    bed_id: str,
    limit: int = 20,
    offset: int = 0,
    db: Database = Depends(get_database),
    _user_id: str = Depends(get_current_user_id),
) -> dict[str, Any]:
    bed_row = await db.fetch_one("SELECT id FROM beds WHERE id = ?", (bed_id,))
    if bed_row is None:
        raise HTTPException(status_code=404, detail="bed not found")

    rows = await db.fetch_all(
        "SELECT * FROM bed_duration_history WHERE bed_id = ? ORDER BY changed_at DESC LIMIT ? OFFSET ?",
        (bed_id, limit, offset),
    )
    return build_envelope(
        "duration_history", rows, meta_links={"self": make_link(f"/beds/{bed_id}/duration-history")}
    )
