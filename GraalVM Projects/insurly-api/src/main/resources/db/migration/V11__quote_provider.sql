-- Mock insurer panel (see docs/06). The `provider` table (V2) existed but was
-- never linked to a quote or seeded. Seed a couple of mock providers and
-- link every quote to one, so the admin dashboard can show "which provider"
-- alongside a quote.

insert into provider (id, name, email, phone_number, website, provider_type)
values
    (gen_random_uuid(), 'Harbourline Insurance', 'quotes@harbourline.example', '+44 20 7946 0001', 'https://harbourline.example', 'CAR_INSURANCE'),
    (gen_random_uuid(), 'Northgate Mutual', 'quotes@northgate.example', '+44 20 7946 0002', 'https://northgate.example', 'CAR_INSURANCE'),
    (gen_random_uuid(), 'BlueOak Direct', 'quotes@blueoak.example', '+44 20 7946 0003', 'https://blueoak.example', 'CAR_INSURANCE');

alter table quote
    add column provider_id uuid references provider (id);

-- Backfill existing quotes round-robin across whatever providers exist now
-- (QuoteAppService assigns new quotes deterministically from the vehicle;
-- this is just a one-off backfill for rows that predate the column).
with numbered_quotes as (
    select id, row_number() over (order by created_at) - 1 as rn
    from quote
),
provider_pool as (
    select id, row_number() over (order by id) - 1 as rn
    from provider
)
update quote
set provider_id = provider_pool.id
from numbered_quotes
join provider_pool on numbered_quotes.rn % (select count(*) from provider) = provider_pool.rn
where quote.id = numbered_quotes.id;

create index idx_quote_provider_id on quote (provider_id);
