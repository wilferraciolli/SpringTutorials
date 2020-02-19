package com.wiltech.security.util;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wiltech.users.ApplicationUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityContextUtil {

    private SecurityContextUtil() {

    }

    public static void setSecurityContext(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String username = claims.getSubject();
            if (username == null) {
                throw new JOSEException("Username missing from JWT");
            }

            List<String> authorities = claims.getStringListClaim("authorities");
            ApplicationUser applicationUser = ApplicationUser
                    .builder()
                    .id(claims.getLongClaim("userId"))
                    .username(username)
                    .roles(String.join(",", authorities))
                    .build();

            // resolve user principal to assign tot he security context
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(applicationUser, null, createAuthorities(authorities));
            auth.setDetails(signedJWT.serialize());

            // Populate the user to the Security context
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.error("Error setting security context ", e);
            SecurityContextHolder.clearContext();
        }
    }

    private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }
}
