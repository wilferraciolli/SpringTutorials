package com.wiltech.insurly.account.identity;

import java.util.Optional;
import java.util.Set;

/**
 * The single seam between "an authenticated HTTP request" and "who that is".
 *
 * <p>{@link JwtUserIdentityResolver} reads the validated {@code JwtAuthenticationToken}
 * (Clerk today) from the security context; {@link HeaderUserIdentityResolver} is a
 * {@code local}-profile-only fallback for the {@code .http} test files. Every
 * other consumer injects {@link CompositeUserIdentityResolver}, which tries the
 * former then falls back to the latter — nothing else in the account package
 * had to change when the real provider landed.
 *
 * <p>See docs/05-security-and-accounts.md &sect;6.
 */
public interface UserIdentityResolver {

    /** Empty when the request carries no usable identity. */
    Optional<ResolvedIdentity> resolve();

    /**
     * The provider-independent facts about the caller.
     *
     * @param subject     stable, unique per person, prefixed by the issuer so a
     *                    future provider swap can't collide two people onto one row
     * @param email       may be null
     * @param displayName may be null
     * @param roles       upper-cased, never null (may be empty)
     */
    record ResolvedIdentity(String subject, String email, String displayName, Set<String> roles) {
    }
}
