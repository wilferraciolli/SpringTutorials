package com.wiltech.insurly.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeRuntimeHints.class)
public class AppConfig {

    /** Injected wherever "now" is needed, so time-dependent logic stays testable. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
