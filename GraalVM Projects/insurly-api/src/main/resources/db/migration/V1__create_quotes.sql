-- pgcrypto provides gen_random_uuid() for UUID primary keys.
create extension if not exists pgcrypto;

create table quote (
    id               uuid           primary key default gen_random_uuid(),
    status           varchar(20)    not null,
    guest_token      uuid           not null,
    user_id          uuid,
    premium_basic    numeric(10, 2) not null,
    premium_standard numeric(10, 2) not null,
    premium_premium  numeric(10, 2) not null,
    created_at       timestamptz    not null,
    expires_at       timestamptz    not null
);

create index idx_quote_guest_token on quote (guest_token);
create index idx_quote_user_id on quote (user_id);
