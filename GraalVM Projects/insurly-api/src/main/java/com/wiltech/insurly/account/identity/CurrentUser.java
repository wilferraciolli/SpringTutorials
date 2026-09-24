package com.wiltech.insurly.account.identity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * The resolved, locally-provisioned account behind an authenticated request.
 * Passed into {@code *AppService} methods so the service layer stays HTTP-free,
 * matching the rest of the codebase.
 */
public record CurrentUser(
        UUID id,
        String subject,
        String email,
        String displayName,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Set<String> roles) {

    public boolean hasRole(final String role) {
        return roles.contains(role.toUpperCase());
    }
}
