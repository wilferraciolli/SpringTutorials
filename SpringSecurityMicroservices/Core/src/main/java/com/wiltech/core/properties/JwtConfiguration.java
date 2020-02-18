package com.wiltech.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Jwt configuration. Class used to hold configuration for the token.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt.config")
@Getter
@Setter
@ToString
public class JwtConfiguration {

    private String loginUrl = "/login/**";

    @NestedConfigurationProperty
    private Header header = new Header();

    private int expiration = 3600;
    private String privateKey = "0dT73Ht1DiGUVPZOxXsEi3p7DqcGrBAF";
    private String type = "encrypted";

    @Getter
    @Setter
    public static class Header {

        private String name = "Authorization";
        private String prefix = "Bearer" + " ";
    }
}
