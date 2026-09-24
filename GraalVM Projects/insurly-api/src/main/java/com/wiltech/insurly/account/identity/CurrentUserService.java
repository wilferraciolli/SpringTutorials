package com.wiltech.insurly.account.identity;

import java.time.Clock;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Turns an authenticated request into a {@link CurrentUser}, provisioning the
 * local {@link AppUser} row on first sight (JIT). Injected into
 * {@code *RestService} classes; the resolved user is then passed down to the
 * app-service layer.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserIdentityResolver identityResolver;
    private final AppUserRepository users;
    private final Clock clock;

    /** Never returns null. Throws {@link UnauthenticatedException} (HTTP 401) if the caller is anonymous. */
    @Transactional
    public CurrentUser require() {
        final UserIdentityResolver.ResolvedIdentity identity = identityResolver.resolve()
                .orElseThrow(() -> new UnauthenticatedException("Authentication required"));

        AppUser user = users.findByAuthSubject(identity.subject())
                .map(existing -> {
                    if (providerProfileChanged(existing, identity)) {
                        existing.syncFromProvider(identity.email(), identity.displayName(), clock.instant());
                        return users.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> users.save(AppUser.builder()
                        .authSubject(identity.subject())
                        .ownership(UserOwnership.SELF_SERVICE)
                        .email(identity.email())
                        .displayName(identity.displayName())
                        .createdAt(clock.instant())
                        .updatedAt(clock.instant())
                        .build()));

        // Roles are app-owned (user_role table) once populated. The very first time
        // we see this user with no roles recorded yet, seed them from the identity
        // provider's roles claim so an admin already configured in Clerk isn't
        // locked out; every login after that, the DB is authoritative and the
        // provider's roles claim is ignored (see V7__user_roles.sql).
        if (user.getRoles().isEmpty() && !identity.roles().isEmpty()) {
            user.replaceRoles(identity.roles());
            user = users.save(user);
        }

        return toCurrentUser(user);
    }

    public CurrentUser toCurrentUser(final AppUser user) {
        return new CurrentUser(
                user.getId(),
                user.getAuthSubject(),
                user.getEmail(),
                user.getDisplayName(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth(),
                user.getRoles());
    }

    private static boolean providerProfileChanged(final AppUser user, final UserIdentityResolver.ResolvedIdentity identity) {
        return !Objects.equals(user.getEmail(), identity.email())
                || !Objects.equals(user.getDisplayName(), identity.displayName());
    }
}
