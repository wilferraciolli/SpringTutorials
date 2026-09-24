package com.wiltech.insurly.settings;

/**
 * The shipped platform display settings: Brazilian Portuguese, the Brazilian
 * real and São Paulo time. Seeded into the {@code system_settings} row by
 * {@code V10__shipped_settings_pt_br.sql} and used as the in-memory fallback if
 * that row is ever missing. Mirrored by {@code settings-defaults.ts} in
 * insurly-ui — keep the three in step.
 */
public final class SystemSettingsDefaults {

    public static final String TIMEZONE = "America/Sao_Paulo";
    public static final SupportedLanguage LANGUAGE = SupportedLanguage.PT_BR;
    public static final SupportedCurrency CURRENCY = SupportedCurrency.BRL;

    private SystemSettingsDefaults() {
    }
}
