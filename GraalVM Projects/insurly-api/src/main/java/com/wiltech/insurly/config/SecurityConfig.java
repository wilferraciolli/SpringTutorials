package com.wiltech.insurly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This app does its own request-level authorization (see
 * {@code AdminGuardInterceptor}, {@code AdminAccessService},
 * {@code UserAccessService}, {@code CurrentUserService} — all driven by
 * {@code UserIdentityResolver}). Spring Security's only job here is JWT
 * validation: if a request carries a {@code Bearer} token, verify it (JWKS
 * signature, {@code iss}, {@code aud}, {@code exp}) and expose it as a
 * {@code JwtAuthenticationToken} in the security context for
 * {@code JwtUserIdentityResolver} to read. No token -> proceed anonymously,
 * so guest routes/Swagger/health stay untouched.
 *
 * <p>{@code @EnableMethodSecurity} exists solely so {@code @PreAuthorize}
 * on the {@code /api/users/{userId}/**} controllers can call the
 * {@code UserAccessService} bean by name — it doesn't depend on Spring
 * Security's {@code GrantedAuthority}/{@code hasRole()} machinery, same as
 * the rest of the app's authorization.
 *
 * <p>See docs/05-security-and-accounts.md &sect;6.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain apiFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
