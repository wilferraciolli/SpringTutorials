package com.wiltech.insurly.admin.access;

import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.account.identity.CurrentUserService;
import com.wiltech.insurly.exceptions.ForbiddenException;
import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authorisation checks for the admin area. {@code /api/admin/**} is already
 * gated on the {@code ADMIN} role by {@code AdminGuardInterceptor}; this adds
 * the one remaining per-target-user rule: the target must actually exist.
 *
 * <p>Admin may read and write any user regardless of {@code ownership}
 * (MANAGED vs SELF_SERVICE) — that field is informational / for filtering
 * only. Sub-resource write access (cars/addresses/phones/licenses/quotes) for
 * self-or-admin now goes through {@code UserAccessService} on the merged
 * {@code /api/users/{userId}/**} endpoints instead of this class.
 */
@Service
@RequiredArgsConstructor
public class AdminAccessService {

    private final AppUserRepository users;
    private final CurrentUserService currentUser;

    /** Confirms the caller is a signed-in admin. Throws 401/403 otherwise. */
    public void requireAdmin() {
        if (!currentUser.require().hasRole("ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
    }

    /** The target user must exist. */
    @Transactional(readOnly = true)
    public AppUser requireViewable(final UUID userId) {
        requireAdmin();
        return users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(userId)));
    }
}
