package com.wiltech.insurly.admin.access;

import com.wiltech.insurly.account.identity.CurrentUserService;
import com.wiltech.insurly.exceptions.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Every {@code /api/admin/**} request must carry an identity with the
 * {@code ADMIN} role (app-owned, see {@code user_role} / {@code AppUser.roles}).
 * Exceptions thrown here are turned into the standard {@code ApiError} JSON by
 * {@code GlobalExceptionHandler}.
 *
 * <p>Wired in {@code WebConfig}. Replaces the previous "world-open" admin area
 * (docs/05-security-and-accounts.md &sect;11).
 */
@Component
@RequiredArgsConstructor
public class AdminGuardInterceptor implements HandlerInterceptor {

    private final CurrentUserService currentUser;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) {
        if (!currentUser.require().hasRole("ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
        return true;
    }
}
