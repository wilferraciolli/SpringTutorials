import json
import uuid
from datetime import UTC, datetime
from typing import Any

from fastapi import APIRouter, Depends, HTTPException

from auth import require_admin_user_id
from database import Database, get_database
from models import Doctor, DoctorCreate, DoctorUpdate, Specialization
from payload import build_envelope, build_resource_links, field_metadata, make_link

router = APIRouter(prefix="/admin/doctors", tags=["doctors"])

DOCTOR_METADATA = {
    "specialization": field_metadata(
        mandatory=True,
        values=[(s.value, s.value) for s in Specialization],
    )
}

_JSON_COLUMNS = ("secondary_specializations", "qualifications")


def _row_to_doctor(row: dict) -> Doctor:
    return Doctor(
        **{k: v for k, v in row.items() if k not in (*_JSON_COLUMNS, "is_active", "created_by")},
        secondary_specializations=json.loads(row["secondary_specializations"]),
        qualifications=json.loads(row["qualifications"]),
        is_active=bool(row["is_active"]),
    )


def _doctor_payload(doctor: Doctor) -> dict[str, Any]:
    return {
        **doctor.model_dump(mode="json"),
        "links": build_resource_links(
            self_href=f"/admin/doctors/{doctor.id}",
            update_href=f"/admin/doctors/{doctor.id}",
            delete_href=f"/admin/doctors/{doctor.id}",
        ),
    }


async def _find_conflict(
    db: Database, email: str, license_number: str, exclude_id: str | None = None
) -> str | None:
    row = await db.fetch_one(
        "SELECT email, license_number FROM doctors WHERE (email = ? OR license_number = ?) AND id != ?",
        (email, license_number, exclude_id or ""),
    )
    if row is None:
        return None
    if row["email"] == email:
        return "email already in use"
    return "license number already in use"


@router.get("/specializations")
async def list_specializations(
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    values = [s.value for s in Specialization]
    return build_envelope(
        "specialization", values, meta_links={"self": make_link("/admin/doctors/specializations")}
    )


@router.get("")
async def list_doctors(
    ward_id: str | None = None,
    specialization: Specialization | None = None,
    is_active: bool | None = None,
    search: str | None = None,
    limit: int = 20,
    offset: int = 0,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    conditions: list[str] = []
    params: list[Any] = []
    if ward_id:
        conditions.append("ward_id = ?")
        params.append(ward_id)
    if specialization:
        conditions.append("specialization = ?")
        params.append(specialization.value)
    if is_active is not None:
        conditions.append("is_active = ?")
        params.append(is_active)
    if search:
        conditions.append("(first_name LIKE ? OR last_name LIKE ? OR email LIKE ?)")
        needle = f"%{search}%"
        params.extend([needle, needle, needle])
    where_clause = f" WHERE {' AND '.join(conditions)}" if conditions else ""

    count_row = await db.fetch_one(f"SELECT COUNT(*) AS total FROM doctors{where_clause}", tuple(params))
    total_count = count_row["total"] if count_row else 0

    rows = await db.fetch_all(
        f"SELECT * FROM doctors{where_clause} ORDER BY last_name, first_name LIMIT ? OFFSET ?",
        (*params, limit, offset),
    )
    doctors = [_doctor_payload(_row_to_doctor(row)) for row in rows]
    metadata = {
        **DOCTOR_METADATA,
        "total_count": total_count,
        "page_size": limit,
        "current_page": (offset // limit + 1) if limit else 1,
    }
    return build_envelope(
        "doctor", doctors, metadata=metadata, meta_links={"self": make_link("/admin/doctors")}
    )


@router.get("/{doctor_id}")
async def get_doctor(
    doctor_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM doctors WHERE id = ?", (doctor_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="doctor not found")
    doctor = _doctor_payload(_row_to_doctor(row))
    return build_envelope(
        "doctor", doctor, metadata=DOCTOR_METADATA, meta_links={"self": make_link("/admin/doctors")}
    )


@router.post("")
async def create_doctor(
    body: DoctorCreate,
    db: Database = Depends(get_database),
    user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    conflict = await _find_conflict(db, body.email, body.license_number)
    if conflict:
        raise HTTPException(status_code=409, detail=conflict)

    now = datetime.now(UTC).isoformat()
    doctor_id = str(uuid.uuid4())
    data = body.model_dump(mode="json")
    await db.execute(
        "INSERT INTO doctors "
        "(id, first_name, last_name, email, phone, license_number, specialization, "
        "secondary_specializations, ward_id, qualifications, bio, years_of_experience, "
        "is_active, created_at, updated_at, created_by) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        (
            doctor_id,
            data["first_name"],
            data["last_name"],
            data["email"],
            data["phone"],
            data["license_number"],
            data["specialization"],
            json.dumps(data["secondary_specializations"]),
            data["ward_id"],
            json.dumps(data["qualifications"]),
            data["bio"],
            data["years_of_experience"],
            True,
            now,
            now,
            user_id,
        ),
    )
    row = await db.fetch_one("SELECT * FROM doctors WHERE id = ?", (doctor_id,))
    doctor = _doctor_payload(_row_to_doctor(row))
    return build_envelope(
        "doctor", doctor, metadata=DOCTOR_METADATA, meta_links={"self": make_link("/admin/doctors")}
    )


@router.patch("/{doctor_id}")
async def update_doctor(
    doctor_id: str,
    body: DoctorUpdate,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> dict[str, Any]:
    row = await db.fetch_one("SELECT * FROM doctors WHERE id = ?", (doctor_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="doctor not found")

    updates = body.model_dump(exclude_unset=True, mode="json")
    if not updates:
        doctor = _doctor_payload(_row_to_doctor(row))
        return build_envelope(
            "doctor", doctor, metadata=DOCTOR_METADATA, meta_links={"self": make_link("/admin/doctors")}
        )

    new_email = updates.get("email", row["email"])
    new_license = updates.get("license_number", row["license_number"])
    conflict = await _find_conflict(db, new_email, new_license, exclude_id=doctor_id)
    if conflict:
        raise HTTPException(status_code=409, detail=conflict)

    for field in _JSON_COLUMNS:
        if field in updates:
            updates[field] = json.dumps(updates[field])
    updates["updated_at"] = datetime.now(UTC).isoformat()

    set_clause = ", ".join(f"{field} = ?" for field in updates)
    await db.execute(
        f"UPDATE doctors SET {set_clause} WHERE id = ?",
        (*updates.values(), doctor_id),
    )
    updated_row = await db.fetch_one("SELECT * FROM doctors WHERE id = ?", (doctor_id,))
    doctor = _doctor_payload(_row_to_doctor(updated_row))
    return build_envelope(
        "doctor", doctor, metadata=DOCTOR_METADATA, meta_links={"self": make_link("/admin/doctors")}
    )


@router.delete("/{doctor_id}", status_code=204)
async def delete_doctor(
    doctor_id: str,
    db: Database = Depends(get_database),
    _user_id: str = Depends(require_admin_user_id),
) -> None:
    row = await db.fetch_one("SELECT * FROM doctors WHERE id = ?", (doctor_id,))
    if row is None:
        raise HTTPException(status_code=404, detail="doctor not found")
    await db.execute(
        "UPDATE doctors SET is_active = ?, updated_at = ? WHERE id = ?",
        (False, datetime.now(UTC).isoformat(), doctor_id),
    )
