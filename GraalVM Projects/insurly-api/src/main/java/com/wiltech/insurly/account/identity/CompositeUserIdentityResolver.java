package com.wiltech.insurly.account.identity;

import jakarta.annotation.Nullable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * The {@link UserIdentityResolver} every consumer (`CurrentUserService`,
 * `AdminGuardInterceptor`, `AdminAccessService`) actually gets injected —
 * {@code @Primary} so none of them need to change.
 *
 * <p>Tries the real, JWT-backed identity first; falls back to the
 * spoofable {@code X-Insurly-*} dev headers only when
 * {@link HeaderUserIdentityResolver} is in play, which is itself
 * {@code @Profile("local")} — so the fallback never exists outside local dev.
 */
@Component
@Primary
public class CompositeUserIdentityResolver implements UserIdentityResolver {

    private final JwtUserIdentityResolver jwtResolver;
    private final HeaderUserIdentityResolver devHeaderResolver;

    @Autowired
    public CompositeUserIdentityResolver(
            final JwtUserIdentityResolver jwtResolver,
            @Nullable final HeaderUserIdentityResolver devHeaderResolver) {
        this.jwtResolver = jwtResolver;
        this.devHeaderResolver = devHeaderResolver;
    }

    @Override
    public Optional<ResolvedIdentity> resolve() {
        final Optional<ResolvedIdentity> identity = jwtResolver.resolve();
        if (identity.isPresent() || devHeaderResolver == null) {
            return identity;
        }
        return devHeaderResolver.resolve();
    }
}
