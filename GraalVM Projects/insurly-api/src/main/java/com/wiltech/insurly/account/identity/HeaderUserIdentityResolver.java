package com.wiltech.insurly.account.identity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * DEVELOPMENT ONLY identity seam. Reads the caller from request headers:
 *
 * <pre>
 *   X-Insurly-User   required — the subject (any stable string, e.g. "alice")
 *   X-Insurly-Email  optional
 *   X-Insurly-Name   optional
 *   X-Insurly-Roles  optional — comma separated (e.g. "admin")
 * </pre>
 *
 * <p>This is trivially spoofable, so it only exists under the {@code local}
 * profile (never {@code prod}) as a fallback for the {@code .http} test
 * files when a real Clerk JWT isn't at hand — see
 * {@link CompositeUserIdentityResolver}, which prefers
 * {@link JwtUserIdentityResolver} and only falls back to this one.
 * See docs/05-security-and-accounts.md &sect;6 and &sect;12.
 */
@Component
@Profile("local")
public class HeaderUserIdentityResolver implements UserIdentityResolver {

    private static final Logger log = LoggerFactory.getLogger(HeaderUserIdentityResolver.class);

    private static final String SUBJECT_HEADER = "X-Insurly-User";
    private static final String EMAIL_HEADER = "X-Insurly-Email";
    private static final String NAME_HEADER = "X-Insurly-Name";
    private static final String ROLES_HEADER = "X-Insurly-Roles";

    public HeaderUserIdentityResolver() {
        log.warn("HeaderUserIdentityResolver active — /api/users/** and /api/admin/** trust the X-Insurly-User header. "
                + "Replace with a JWT resolver before deploying (docs/05-security-and-accounts.md).");
    }

    @Override
    public Optional<ResolvedIdentity> resolve() {
        final HttpServletRequest request = currentRequest();
        if (request == null) {
            return Optional.empty();
        }

        final String subject = request.getHeader(SUBJECT_HEADER);
        if (StringUtils.isBlank(subject)) {
            return Optional.empty();
        }

        final Set<String> roles = Optional.ofNullable(request.getHeader(ROLES_HEADER))
                .map(raw -> Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(StringUtils::isNotBlank)
                        .map(String::toUpperCase)
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);

        return Optional.of(new ResolvedIdentity(
                "dev|" + subject,
                request.getHeader(EMAIL_HEADER),
                request.getHeader(NAME_HEADER),
                roles));
    }

    private static HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }
}
