-- Shared between local SQLite (see database.py) and Cloudflare D1 — D1 is
-- SQLite-compatible, so the same schema applies to both via
-- `wrangler d1 migrations apply` for D1 and a plain executescript() locally.

-- bed_number and ward_id are stable codes, not display text — no English
-- (or any locale's) words are baked in here. The UI translates them via
-- its own label lookup, keyed on these ids. `status` is already such a
-- code (an enum, not prose). 'cleaning' covers turnover between a
-- discharge and the bed becoming 'ready' again, distinct from 'preparing'
-- (readying a bed for a specific incoming patient/procedure).
-- current_procedure_type_id/procedure_notes describe the bed's *current*
-- occupancy, cleared when it's discharged back to 'ready' — history of past
-- assignments lives in bed_duration_history below, not on this row. There's
-- no separate procedure-start-time column: status_changed_at already marks
-- when the bed became occupied.
CREATE TABLE IF NOT EXISTS beds (
    id TEXT PRIMARY KEY,
    bed_number TEXT NOT NULL,
    ward_id TEXT NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('ready', 'preparing', 'occupied', 'cleaning')),
    status_changed_at TEXT NOT NULL,
    expected_duration_minutes INTEGER,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    current_procedure_type_id TEXT,
    procedure_notes TEXT,
    duration_changed_at TEXT,
    duration_changed_by TEXT
);

CREATE TABLE IF NOT EXISTS bed_status_history (
    id TEXT PRIMARY KEY,
    bed_id TEXT NOT NULL REFERENCES beds(id),
    from_status TEXT,
    to_status TEXT NOT NULL,
    changed_at TEXT NOT NULL,
    changed_by TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS bed_duration_history (
    id TEXT PRIMARY KEY,
    bed_id TEXT NOT NULL REFERENCES beds(id),
    old_duration_minutes INTEGER,
    new_duration_minutes INTEGER NOT NULL,
    procedure_type_id TEXT,
    changed_by TEXT NOT NULL,
    change_reason TEXT NOT NULL,
    changed_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_bed_duration_history_bed ON bed_duration_history(bed_id);

-- Written once per bed, at discharge (see routers/beds.py's
-- update_bed_status) — the Rule 4 variance calculation from
-- docs/features/bed-timeframe-management.md. ward_id/expected_duration_minutes
-- are copied from the bed at discharge time rather than joined later, so a
-- bed moving wards or a changed default duration policy doesn't rewrite
-- history.
CREATE TABLE IF NOT EXISTS bed_occupancy_history (
    id TEXT PRIMARY KEY,
    bed_id TEXT NOT NULL REFERENCES beds(id),
    ward_id TEXT NOT NULL,
    procedure_type_id TEXT,
    expected_duration_minutes INTEGER NOT NULL,
    actual_duration_minutes INTEGER NOT NULL,
    variance_minutes INTEGER NOT NULL,
    variance_percent REAL NOT NULL,
    variance_class TEXT NOT NULL CHECK (variance_class IN ('underutilized', 'on_track', 'overrun')),
    started_at TEXT NOT NULL,
    ended_at TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_bed_occupancy_history_ward ON bed_occupancy_history(ward_id);
CREATE INDEX IF NOT EXISTS idx_bed_occupancy_history_ended ON bed_occupancy_history(ended_at);

-- ward_id on beds/doctors/users has no FOREIGN KEY: kept as a plain
-- unconstrained TEXT column, validated at the API layer instead.
CREATE TABLE IF NOT EXISTS wards (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- Existing beds already use the ids 'icu'/'er' (see scripts/seed_beds.py and
-- the frontend's core/i18n/labels.ts) — seeded here with the same ids so
-- they aren't orphaned; new wards created via the admin UI get a generated
-- uuid id instead.
INSERT OR IGNORE INTO wards (id, name, description, created_at, updated_at)
VALUES
    ('icu', 'ICU', NULL, '2024-01-01T00:00:00+00:00', '2024-01-01T00:00:00+00:00'),
    ('er', 'Emergency Room', NULL, '2024-01-01T00:00:00+00:00', '2024-01-01T00:00:00+00:00');

-- specialization/secondary_specializations are validated against a fixed
-- set at the API layer (see models.py), not with a CHECK constraint, since
-- secondary_specializations is a JSON array rather than a single column
-- value.
CREATE TABLE IF NOT EXISTS doctors (
    id TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL,
    license_number TEXT NOT NULL UNIQUE,
    specialization TEXT NOT NULL,
    secondary_specializations TEXT NOT NULL DEFAULT '[]', -- JSON array of strings
    ward_id TEXT,
    qualifications TEXT NOT NULL DEFAULT '[]', -- JSON array of {type, name}
    bio TEXT,
    years_of_experience INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    created_by TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_doctors_ward ON doctors(ward_id);
CREATE INDEX IF NOT EXISTS idx_doctors_specialization ON doctors(specialization);

CREATE TABLE IF NOT EXISTS procedure_types (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL CHECK (category IN ('surgery', 'diagnostic', 'treatment', 'recovery', 'other')),
    typical_duration_minutes INTEGER NOT NULL,
    min_duration_minutes INTEGER,
    max_duration_minutes INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- Clerk proves *who* is calling (see auth.py) but knows nothing about our
-- app-specific role/profile data — this table is the source of truth for
-- both. A row is created lazily (JIT) on a caller's first GET /me with
-- role='user'; promotion to 'admin' is a manual DB operation for now (see
-- scripts/promote_admin.py) since there's no user-directory UI yet.
-- `language` is nullable — null means "use the organization's
-- default_language" (see docs/features/internationalization-i18n.md's
-- resolution order); there is deliberately no per-user timezone override,
-- timezone stays organization-wide only.
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY, -- Clerk user id (JWT `sub`)
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    phone TEXT,
    job_title TEXT,
    role TEXT NOT NULL DEFAULT 'user' CHECK (role IN ('user', 'admin')),
    ward_id TEXT,
    language TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- The base of the `organizations` table from
-- docs/features/tenant-branding-customization.md — only the columns this
-- feature (internationalization-i18n.md) needs (name, default_language,
-- timezone). Deliberately minimal: a future migration can ALTER TABLE to
-- add logo_url/icon_url/theme colors once that feature needs them.
CREATE TABLE IF NOT EXISTS organizations (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    default_language TEXT NOT NULL DEFAULT 'el-GR' CHECK (default_language IN ('el-GR', 'en-GB')),
    timezone TEXT NOT NULL DEFAULT 'Europe/Athens',
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- El Greco Medical Centre — the reference customer in both docs.
INSERT OR IGNORE INTO organizations (id, name, default_language, timezone, created_at, updated_at)
VALUES ('el_greco', 'El Greco Medical Centre', 'el-GR', 'Europe/Athens', '2024-01-01T00:00:00+00:00', '2024-01-01T00:00:00+00:00');
