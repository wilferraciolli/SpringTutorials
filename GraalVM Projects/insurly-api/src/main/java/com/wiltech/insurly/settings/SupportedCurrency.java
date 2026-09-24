package com.wiltech.insurly.settings;

import java.util.stream.Stream;

/**
 * Currencies the platform can display amounts in. The enum name is the ISO 4217
 * code, so it doubles as the value passed to the frontend's currency formatting.
 */
public enum SupportedCurrency {
    USD("US Dollar", "$"),
    GBP("British Pound", "£"),
    EUR("Euro", "€"),
    BRL("Brazilian Real", "R$");

    private final String displayName;
    private final String symbol;

    SupportedCurrency(final String displayName, final String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Stream<SupportedCurrency> stream() {
        return Stream.of(values());
    }
}
