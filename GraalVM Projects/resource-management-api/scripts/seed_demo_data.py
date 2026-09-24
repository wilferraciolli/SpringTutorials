"""Seed a full demo dataset (wards, doctors, procedures, beds) for manual
testing and demos. Safe to re-run — every row uses a stable id and
INSERT OR IGNORE, so re-running just fills in anything missing.

Usage:
    uv run python scripts/seed_demo_data.py
    # or, against the Docker Compose volume:
    docker compose exec api uv run python scripts/seed_demo_data.py
"""

import json
import os
import sqlite3
import uuid
from datetime import UTC, datetime, timedelta
from pathlib import Path

MIGRATIONS_DIR = Path(__file__).resolve().parent.parent / "migrations"
DB_PATH = os.environ.get("DATABASE_PATH", "./local.db")

NOW = datetime.now(UTC)


def iso(dt: datetime) -> str:
    return dt.isoformat()


def ago(minutes: int) -> str:
    return iso(NOW - timedelta(minutes=minutes))


# icu/er already come from migrations/0001_initial_schema.sql — this adds the rest.
WARDS = [
    ("general", "General Ward", "General medical and surgical admissions"),
    ("maternity", "Maternity", "Labour, delivery and postnatal care"),
    ("surgery", "Surgical Ward", "Pre- and post-operative surgical care"),
    ("paediatrics", "Paediatrics", "Care for infants, children and adolescents"),
]

DOCTORS = [
    # (id, first, last, email, phone, license, specialization, ward_id, years_exp, qualification)
    ("doc-vasquez", "Elena", "Vasquez", "elena.vasquez@elgreco-medical.example", "+30 210 555 0101",
     "GR-MD-10234", "Cardiology", "icu", 14, ("board_certification", "Board Certified in Cardiology")),
    ("doc-nakamura", "Hiro", "Nakamura", "hiro.nakamura@elgreco-medical.example", "+30 210 555 0102",
     "GR-MD-10235", "Emergency Medicine", "er", 9, ("degree", "MD, National and Kapodistrian University of Athens")),
    ("doc-osei", "Amara", "Osei", "amara.osei@elgreco-medical.example", "+30 210 555 0103",
     "GR-MD-10236", "Trauma", "er", 11, ("fellowship", "Fellowship in Trauma Surgery")),
    ("doc-liang", "Wei", "Liang", "wei.liang@elgreco-medical.example", "+30 210 555 0104",
     "GR-MD-10237", "Surgery", "surgery", 18, ("board_certification", "Board Certified in General Surgery")),
    ("doc-fitzgerald", "Maura", "Fitzgerald", "maura.fitzgerald@elgreco-medical.example", "+30 210 555 0105",
     "GR-MD-10238", "Orthopedics", "surgery", 7, ("certification", "Certified in Orthopaedic Trauma")),
    ("doc-papadopoulos", "Sofia", "Papadopoulos", "sofia.papadopoulos@elgreco-medical.example", "+30 210 555 0106",
     "GR-MD-10239", "Internal Medicine", "general", 12, ("degree", "MD, Aristotle University of Thessaloniki")),
    ("doc-mensah", "Kwame", "Mensah", "kwame.mensah@elgreco-medical.example", "+30 210 555 0107",
     "GR-MD-10240", "Pediatrics", "paediatrics", 8, ("board_certification", "Board Certified in Pediatrics")),
    ("doc-rossi", "Giulia", "Rossi", "giulia.rossi@elgreco-medical.example", "+30 210 555 0108",
     "GR-MD-10241", "Neurology", "icu", 15, ("fellowship", "Fellowship in Neurocritical Care")),
]

PROCEDURES = [
    # (id, name, description, category, typical, min, max)
    ("proc-appendectomy", "Appendectomy", "Surgical removal of the appendix", "surgery", 60, 45, 90),
    ("proc-hip-replacement", "Hip Replacement", "Total hip arthroplasty", "surgery", 120, 90, 180),
    ("proc-c-section", "Caesarean Section", "Surgical delivery of a baby", "surgery", 45, 30, 75),
    ("proc-mri-brain", "MRI Brain Scan", "Magnetic resonance imaging of the brain", "diagnostic", 45, 30, 60),
    ("proc-ct-chest", "CT Chest Scan", "Computed tomography of the chest", "diagnostic", 20, 15, 30),
    ("proc-bloodwork", "Blood Panel", "Comprehensive blood test panel", "diagnostic", 15, 10, 25),
    ("proc-dialysis", "Dialysis Session", "Haemodialysis treatment session", "treatment", 240, 180, 300),
    ("proc-chemo", "Chemotherapy Infusion", "Scheduled chemotherapy infusion", "treatment", 180, 120, 240),
    ("proc-post-op-recovery", "Post-Op Recovery", "Post-operative monitoring and recovery", "recovery", 90, 60, 150),
    ("proc-observation", "General Observation", "Short-stay observation, no active procedure", "other", 30, 15, 60),
]

# (bed_number, ward_id, status, minutes_since_status_change, expected_duration_minutes, procedure_id, notes)
BEDS = [
    # ICU — mix of overdue occupied, on-track occupied, preparing, ready
    ("ICU-1", "icu", "occupied", 150, 90, "proc-post-op-recovery", "Post-op monitoring, stable vitals"),
    ("ICU-2", "icu", "occupied", 40, 120, "proc-dialysis", "Second session this week"),
    ("ICU-3", "icu", "preparing", 10, None, None, None),
    ("ICU-4", "icu", "ready", 5, None, None, None),
    # ER — busy, several overdue
    ("ER-1", "er", "occupied", 200, 60, "proc-observation", "Awaiting specialist review"),
    ("ER-2", "er", "occupied", 25, 45, "proc-ct-chest", "Suspected pneumonia"),
    ("ER-3", "er", "preparing", 5, None, None, None),
    ("ER-4", "er", "ready", 15, None, None, None),
    ("ER-5", "er", "ready", 15, None, None, None),
    # General ward
    ("GEN-1", "general", "occupied", 60, 90, "proc-bloodwork", "Routine panel, results pending"),
    ("GEN-2", "general", "occupied", 300, 180, "proc-chemo", "Cycle 3 of 6"),
    ("GEN-3", "general", "ready", 20, None, None, None),
    ("GEN-4", "general", "ready", 20, None, None, None),
    ("GEN-5", "general", "preparing", 8, None, None, None),
    # Maternity
    ("MAT-1", "maternity", "occupied", 30, 45, "proc-c-section", "Scheduled c-section, family present"),
    ("MAT-2", "maternity", "ready", 12, None, None, None),
    ("MAT-3", "maternity", "preparing", 6, None, None, None),
    # Surgical ward
    ("SURG-1", "surgery", "occupied", 100, 120, "proc-hip-replacement", "Surgeon: Dr. Liang"),
    ("SURG-2", "surgery", "occupied", 70, 60, "proc-appendectomy", "Uncomplicated, closing up"),
    ("SURG-3", "surgery", "ready", 18, None, None, None),
    ("SURG-4", "surgery", "preparing", 4, None, None, None),
    # Paediatrics
    ("PED-1", "paediatrics", "occupied", 20, 30, "proc-observation", "Fever, awaiting bloodwork"),
    ("PED-2", "paediatrics", "ready", 9, None, None, None),
    ("PED-3", "paediatrics", "ready", 9, None, None, None),
]


def seed_wards(conn: sqlite3.Connection) -> None:
    now = iso(NOW)
    for ward_id, name, description in WARDS:
        conn.execute(
            "INSERT OR IGNORE INTO wards (id, name, description, created_at, updated_at) "
            "VALUES (?, ?, ?, ?, ?)",
            (ward_id, name, description, now, now),
        )


def seed_doctors(conn: sqlite3.Connection) -> None:
    now = iso(NOW)
    for doctor_id, first, last, email, phone, license_number, specialization, ward_id, years_exp, qual in DOCTORS:
        qualifications = json.dumps([{"type": qual[0], "name": qual[1]}])
        conn.execute(
            "INSERT OR IGNORE INTO doctors "
            "(id, first_name, last_name, email, phone, license_number, specialization, "
            "secondary_specializations, ward_id, qualifications, bio, years_of_experience, "
            "is_active, created_at, updated_at, created_by) "
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (
                doctor_id, first, last, email, phone, license_number, specialization,
                "[]", ward_id, qualifications, None, years_exp,
                True, now, now, "demo-seed",
            ),
        )


def seed_procedures(conn: sqlite3.Connection) -> None:
    now = iso(NOW)
    for procedure_id, name, description, category, typical, min_d, max_d in PROCEDURES:
        conn.execute(
            "INSERT OR IGNORE INTO procedure_types "
            "(id, name, description, category, typical_duration_minutes, min_duration_minutes, "
            "max_duration_minutes, is_active, created_at, updated_at) "
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (procedure_id, name, description, category, typical, min_d, max_d, True, now, now),
        )


def seed_beds(conn: sqlite3.Connection) -> None:
    now = iso(NOW)
    for bed_number, ward_id, status, minutes_ago, expected_duration, procedure_id, notes in BEDS:
        existing = conn.execute(
            "SELECT id FROM beds WHERE bed_number = ? AND ward_id = ?", (bed_number, ward_id)
        ).fetchone()
        if existing:
            continue
        bed_id = str(uuid.uuid4())
        status_changed_at = ago(minutes_ago)
        duration_changed_at = status_changed_at if status == "occupied" else None
        conn.execute(
            "INSERT INTO beds "
            "(id, bed_number, ward_id, status, status_changed_at, expected_duration_minutes, "
            "current_procedure_type_id, procedure_notes, duration_changed_at, duration_changed_by, "
            "created_at, updated_at) "
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (
                bed_id, bed_number, ward_id, status, status_changed_at, expected_duration,
                procedure_id, notes, duration_changed_at,
                "demo-seed" if status == "occupied" else None,
                now, now,
            ),
        )
        conn.execute(
            "INSERT INTO bed_status_history (id, bed_id, from_status, to_status, changed_at, changed_by) "
            "VALUES (?, ?, NULL, ?, ?, ?)",
            (str(uuid.uuid4()), bed_id, status, status_changed_at, "demo-seed"),
        )
        if status == "occupied":
            conn.execute(
                "INSERT INTO bed_duration_history "
                "(id, bed_id, old_duration_minutes, new_duration_minutes, procedure_type_id, "
                "changed_by, change_reason, changed_at) "
                "VALUES (?, ?, NULL, ?, ?, ?, ?, ?)",
                (str(uuid.uuid4()), bed_id, expected_duration, procedure_id, "demo-seed",
                 "procedure_assignment", status_changed_at),
            )


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

    seed_wards(conn)
    seed_doctors(conn)
    seed_procedures(conn)
    seed_beds(conn)

    conn.commit()
    conn.close()
    print(f"Seeded demo data ({len(WARDS)} wards, {len(DOCTORS)} doctors, "
          f"{len(PROCEDURES)} procedures, {len(BEDS)} beds) into {DB_PATH}")


if __name__ == "__main__":
    main()
