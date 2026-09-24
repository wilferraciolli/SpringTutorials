package com.wiltech.insurly.quote;

import java.util.stream.Stream;

public enum PrimaryUse {
    COMMUTE("Commute"),
    PLEASURE("Pleasure"),
    BUSINESS("Business");

    private final String description;

    PrimaryUse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Stream<PrimaryUse> stream() {
        return Stream.of(values());
    }
}
