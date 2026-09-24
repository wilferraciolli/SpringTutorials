package com.wiltech.insurly.admin.providers;

import java.util.Objects;
import java.util.stream.Stream;

public enum ProviderType {
    CAR_INSURANCE("Car Insurance");

    private final String description;

    ProviderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static String resolveId(ProviderType type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return stream()
                .map(Enum::name)
                .filter(name -> name.equals(type.name()))
                .findFirst()
                .orElse(null);
    }

    public static Stream<ProviderType> stream() {
        return Stream.of(ProviderType.values());
    }
}
