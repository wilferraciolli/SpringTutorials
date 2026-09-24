-- Phase 2, part 2: phone numbers + the profile fields a quote can be prefilled
-- from. See docs/05-security-and-accounts.md.

create table user_phone (
    id           uuid        primary key default gen_random_uuid(),
    user_id      uuid        not null references app_user (id) on delete cascade,
    label        varchar(40) not null,            -- Mobile | Home | Work | ...
    phone_number varchar(32) not null,
    is_primary   boolean     not null default false,
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now()
);
create index idx_user_phone_user_id on user_phone (user_id);

alter table app_user
    add column first_name    varchar(80),
    add column last_name     varchar(80),
    add column date_of_birth date;
