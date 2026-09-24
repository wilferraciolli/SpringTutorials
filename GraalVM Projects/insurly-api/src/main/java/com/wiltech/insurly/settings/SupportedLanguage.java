package com.wiltech.insurly.settings;

import java.util.stream.Stream;

/**
 * Display languages the platform offers. {@code tag} is the BCP-47 locale the
 * frontend uses for number / date formatting once locale switching is wired.
 */
public enum SupportedLanguage {
    EN_GB("English (UK)", "en-GB"),
    EL("Greek", "el"),
    PT_BR("Português (Brasil)", "pt-BR");

    private final String displayName;
    private final String tag;

    SupportedLanguage(final String displayName, final String tag) {
        this.displayName = displayName;
        this.tag = tag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTag() {
        return tag;
    }

    public static Stream<SupportedLanguage> stream() {
        return Stream.of(values());
    }
}
