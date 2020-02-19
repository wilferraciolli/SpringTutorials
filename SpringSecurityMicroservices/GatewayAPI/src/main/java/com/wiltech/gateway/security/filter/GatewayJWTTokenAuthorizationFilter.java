package com.wiltech.gateway.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import com.netflix.zuul.context.RequestContext;
import com.nimbusds.jwt.SignedJWT;
import com.wiltech.core.properties.JwtConfiguration;
import com.wiltech.security.filter.JwtTokenAuthorizationFilter;
import com.wiltech.security.token.converter.TokenConverter;
import com.wiltech.security.util.SecurityContextUtil;

import lombok.SneakyThrows;

/**
 * The type Gateway jwt token authorization filter.
 * Filter used on the gateway server to authorize requests through.
 */
public class GatewayJWTTokenAuthorizationFilter extends JwtTokenAuthorizationFilter {

    /**
     * Instantiates a new Gateway jwt token authorization filter.
     * @param jwtConfiguration the jwt configuration
     * @param tokenConverter the token converter
     */
    public GatewayJWTTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
        super(jwtConfiguration, tokenConverter);
    }

    @SneakyThrows @Override
    @SuppressWarnings("Duplicates")
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(jwtConfiguration.getHeader().getName());

        // stop and return when in this condition as authentication is not required eg login...
        if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        // as the requests coming here, need to have the token encrypted before it can validate
        String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();
        final String signedToken = tokenConverter.decryptToken(token);

        // validate token
        tokenConverter.validateTokenSignature(signedToken);

        // check whether the token should be encrypted and validate
        SecurityContextUtil.setSecurityContext(SignedJWT.parse(signedToken));

        // check whether the microservice wants the token signed and encrypted and add as a header
        if (jwtConfiguration.getType().equalsIgnoreCase("signed")) {
            RequestContext.getCurrentContext().addZuulRequestHeader("Authorization", jwtConfiguration.getHeader().getPrefix() + signedToken);
        }

        chain.doFilter(request, response);
    }
}
