package com.wiltech.insurly.account.licenses;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * {@link LicenseNumberConverter} is built by Hibernate, not Spring, so it reads
 * its key from {@code System.getenv}/{@code System.getProperty}. In a deployed
 * container {@code LICENSE_ENC_KEY} is a real env var and this bridge is a
 * no-op; for local {@code mvn spring-boot:run} the key lives in
 * {@code insurly-api/.env} as a Spring property, so copy it to a system
 * property before the first entity load.
 */
@Component
public class LicenseEncryptionKeyBridge {

    @Value("${LICENSE_ENC_KEY:}")
    private String key;

    @PostConstruct
    void publish() {
        if (key != null && !key.isBlank() && System.getProperty("LICENSE_ENC_KEY") == null) {
            System.setProperty("LICENSE_ENC_KEY", key);
        }
    }
}
