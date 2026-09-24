-- The shipped platform settings are now Brazilian: Portuguese (Brazil), the
-- Brazilian real and São Paulo time. Mirrors SystemSettingsDefaults on the API
-- and settings-defaults.ts in the UI.

-- There must always be a platform row to read, so seed it if it is missing.
insert into system_settings (timezone, language, currency)
select 'America/Sao_Paulo', 'PT_BR', 'BRL'
where not exists (select 1 from system_settings);

-- Move each column off the previous shipped default. Same rule as V8: a value
-- an admin has already changed away from the old default is left untouched.
update system_settings
set language   = 'PT_BR',
    updated_at = now()
where language = 'EN_GB';

update system_settings
set currency   = 'BRL',
    updated_at = now()
where currency = 'USD';

update system_settings
set timezone   = 'America/Sao_Paulo',
    updated_at = now()
where timezone in ('Europe/London', 'UTC');
