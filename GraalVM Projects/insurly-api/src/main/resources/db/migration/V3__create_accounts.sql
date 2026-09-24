-- Phase 2 accounts (see docs/05-security-and-accounts.md).
-- Additive only. `app_user` mirrors the OIDC provider's user; credentials live
-- at the provider, never here.

create table app_user (
    id           uuid         primary key default gen_random_uuid(),
    auth_subject varchar(255) not null unique,   -- "<issuer>|<sub>"
    email        varchar(320),
    display_name varchar(255),
    created_at   timestamptz  not null default now(),
    updated_at   timestamptz  not null default now()
);

create table user_car (
    id          uuid        primary key default gen_random_uuid(),
    user_id     uuid        not null references app_user (id) on delete cascade,
    make        varchar(40) not null,
    model       varchar(40) not null,
    year        int         not null,
    vin         varchar(32),
    primary_use varchar(16) not null,            -- COMMUTE | PLEASURE | BUSINESS
    nickname    varchar(60),
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);
create index idx_user_car_user_id on user_car (user_id);

create table user_address (
    id           uuid         primary key default gen_random_uuid(),
    user_id      uuid         not null references app_user (id) on delete cascade,
    label        varchar(40)  not null,          -- Home | Work | ...
    line1        varchar(120) not null,
    line2        varchar(120),
    city         varchar(80)  not null,
    region       varchar(80),
    postal_code  varchar(16)  not null,
    country_code varchar(2)   not null,
    is_primary   boolean      not null default false,
    created_at   timestamptz  not null default now(),
    updated_at   timestamptz  not null default now()
);
create index idx_user_address_user_id on user_address (user_id);

create table user_license (
    id             uuid        primary key default gen_random_uuid(),
    user_id        uuid        not null references app_user (id) on delete cascade,
    license_number bytea       not null,         -- AES-GCM ciphertext (LicenseNumberConverter)
    status         varchar(16) not null,         -- reuses com.wiltech.insurly.quote.LicenseStatus
    issuing_region varchar(80),
    issued_on      date,
    expires_on     date,
    years_held     int,
    created_at     timestamptz not null default now(),
    updated_at     timestamptz not null default now()
);
create index idx_user_license_user_id on user_license (user_id);

-- Link the existing quote table (V1) to accounts. Stays nullable: guest quotes
-- keep user_id null and are reached by guest_token only.
alter table quote
    add constraint fk_quote_user foreign key (user_id) references app_user (id) on delete set null;
