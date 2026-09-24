package com.wiltech.insurly.account.identity;

import java.util.stream.Stream;

/**
 * Who created an {@link AppUser}'s record — informational / for admin search
 * filtering only. Doesn't gate write access: an admin can manage either kind
 * of user's cars/addresses/phones/licences/quotes and profile via
 * {@code /api/users/{userId}/**} and {@code /api/admin/users/{id}}.
 *
 * <ul>
 *   <li>{@code SELF_SERVICE} — the person signed up themselves (via the
 *       identity provider) and manages their own details day-to-day.</li>
 *   <li>{@code MANAGED} — an admin created the record (no login).</li>
 * </ul>
 */
public enum UserOwnership {
    SELF_SERVICE("Self-service"),
    MANAGED("Admin-managed");

    private final String description;

    UserOwnership(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Stream<UserOwnership> stream() {
        return Stream.of(values());
    }
}
