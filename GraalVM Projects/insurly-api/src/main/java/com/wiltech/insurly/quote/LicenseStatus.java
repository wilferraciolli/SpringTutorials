package com.wiltech.insurly.quote;

import java.util.stream.Stream;

public enum LicenseStatus {
    VALID("Valid"),
    PROVISIONAL("Provisional"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired");

    private final String description;

    LicenseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Stream<LicenseStatus> stream() {
        return Stream.of(values());
    }
}
