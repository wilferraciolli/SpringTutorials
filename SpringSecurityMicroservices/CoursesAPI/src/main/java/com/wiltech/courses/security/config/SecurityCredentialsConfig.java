package com.wiltech.courses.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wiltech.core.properties.JwtConfiguration;
import com.wiltech.security.config.SecurityTokenConfig;
import com.wiltech.security.filter.JwtTokenAuthorizationFilter;
import com.wiltech.security.token.converter.TokenConverter;

/**
 * The type Security credentials config. Class used to configure what is necessary for security to run.
 */
@EnableWebSecurity
public class SecurityCredentialsConfig extends SecurityTokenConfig {

    private final TokenConverter tokenConverter;

    public SecurityCredentialsConfig(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {

        super(jwtConfiguration);
        this.tokenConverter = tokenConverter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
			.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);

        // pass local config to super class
        super.configure(http);
    }

}
