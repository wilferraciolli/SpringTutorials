-- Supported display languages are now English (UK), Greek and Brazilian
-- Portuguese. US English (EN_US) is dropped; migrate any row still on it so the
-- SupportedLanguage enum can still be read back. Greek (EL) is new, so no
-- existing rows reference it.
update system_settings set language = 'EN_GB', updated_at = now() where language = 'EN_US';

update user_settings set language = 'EN_GB', updated_at = now() where language = 'EN_US';
