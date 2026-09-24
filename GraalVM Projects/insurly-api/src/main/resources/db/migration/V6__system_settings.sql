-- Platform-wide display preferences (timezone / language / currency). Exactly
-- one row: the application reads and updates this singleton, never inserts
-- another. Seeded here so the very first request has something to read.
create table system_settings (
    id         uuid        primary key default gen_random_uuid(),
    timezone   varchar(64) not null,
    language   varchar(16) not null,
    currency   varchar(3)  not null,
    updated_at timestamptz not null default now()
);

insert into system_settings (timezone, language, currency)
values ('UTC', 'EN_US', 'USD');
