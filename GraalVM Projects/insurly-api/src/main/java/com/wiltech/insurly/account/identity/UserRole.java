package com.wiltech.insurly.account.identity;

import java.util.stream.Stream;

/**
 * The catalog of known app-owned roles, for admin UI purposes (the "roles"
 * metadata dropdown) — {@link AppUser#getRoles()} itself stores plain
 * strings, not this enum, so a new role can be granted without a code change
 * or migration; this just documents/lists the ones the admin UI offers today.
 */
public enum UserRole {
    ADMIN("Admin"),
    PROVIDER_ADMIN("Provider admin"),
    SALES_ADMIN("Sales admin");

    private final String description;

    UserRole(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Stream<UserRole> stream() {
        return Stream.of(values());
    }
}
