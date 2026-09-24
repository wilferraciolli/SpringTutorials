-- Per-user overrides of the platform display preferences. Every value column
-- is nullable: null means "inherit the system_settings value". At most one row
-- per user; removed automatically when the account is deleted.
create table user_settings (
    user_id    uuid        primary key references app_user (id) on delete cascade,
    timezone   varchar(64),
    language   varchar(16),
    currency   varchar(3),
    updated_at timestamptz not null default now()
);

-- The shipped default timezone is now Europe/London rather than UTC. Only nudge
-- the singleton if it is still on the original V6 seed value, so an environment
-- where an admin has already chosen a zone is left untouched.
update system_settings
set timezone   = 'Europe/London',
    updated_at = now()
where timezone = 'UTC';
