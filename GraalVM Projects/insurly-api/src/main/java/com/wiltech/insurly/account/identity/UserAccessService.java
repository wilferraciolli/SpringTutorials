package com.wiltech.insurly.account.identity;

import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The permission check behind every {@code /api/users/{userId}/**} endpoint
 * (cars, addresses, phones, licenses, quotes). {@code userId} is either the
 * literal token {@code "me"} or another user's UUID; the caller may act on a
 * path if it resolves to themselves, or if they hold the {@code ADMIN} role
 * (admin acting on behalf of any user, self-service or otherwise).
 *
 * <p>Referenced from {@code @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")}
 * on the merged {@code RestService} methods.
 */
@Service("userAccess")
@RequiredArgsConstructor
public class UserAccessService {

    private static final String ME = "me";

    private final CurrentUserService currentUserService;

    /** SpEL target for {@code @PreAuthorize}. Throws 401 if unauthenticated; returns false (403) otherwise. */
    public boolean isSelfOrAdmin(final String userId) {
        final CurrentUser me = currentUserService.require();
        if (ME.equalsIgnoreCase(userId)) {
            return true;
        }
        if (me.hasRole("ADMIN")) {
            return true;
        }
        return parseUuid(userId).map(id -> id.equals(me.id())).orElse(false);
    }

    /** Resolves the path variable to the actual target user id. Call only after {@link #isSelfOrAdmin}. */
    public UUID resolve(final String userId) {
        if (ME.equalsIgnoreCase(userId)) {
            return currentUserService.require().id();
        }
        return parseUuid(userId).orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(userId)));
    }

    private static Optional<UUID> parseUuid(final String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (final IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
