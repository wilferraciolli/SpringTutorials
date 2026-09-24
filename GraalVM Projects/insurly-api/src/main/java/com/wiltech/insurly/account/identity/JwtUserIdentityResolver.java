package com.wiltech.insurly.account.identity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Real identity, backed by whatever OIDC provider issued the caller's
 * {@code Authorization: Bearer <jwt>} (Clerk today — see
 * {@code app.auth.role-claim} / {@code AUTH_ISSUER_URI}). {@code SecurityConfig}
 * validates the token (signature/JWKS, {@code iss}, {@code aud}, {@code exp})
 * before this class ever runs; it only maps claims to a
 * {@link UserIdentityResolver.ResolvedIdentity}.
 *
 * <p>See docs/05-security-and-accounts.md &sect;6.4.
 */
@Component
public class JwtUserIdentityResolver implements UserIdentityResolver {

    private final String roleClaim;

    public JwtUserIdentityResolver(@Value("${app.auth.role-claim}") final String roleClaim) {
        this.roleClaim = roleClaim;
    }

    @Override
    public Optional<ResolvedIdentity> resolve() {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token)) {
            return Optional.empty();
        }

        final Jwt jwt = token.getToken();
        return Optional.of(new ResolvedIdentity(
                jwt.getIssuer() + "|" + jwt.getSubject(),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                rolesOf(jwt)));
    }

    private Set<String> rolesOf(final Jwt jwt) {
        final List<String> roles = jwt.getClaimAsStringList(roleClaim);
        return roles == null
                ? Set.of()
                : roles.stream().map(String::toUpperCase).collect(Collectors.toSet());
    }
}
