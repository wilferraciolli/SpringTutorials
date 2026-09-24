package com.wiltech.insurly.quote;

import java.util.stream.Stream;

public enum QuoteStatus {
    DRAFT("Draft"),
    COMPLETE("Complete");

    private final String description;

    QuoteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Stream<QuoteStatus> stream() {
        return Stream.of(values());
    }
}
