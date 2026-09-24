package com.wiltech.insurly.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Wires the email seam and turns on {@code @Scheduled} (used by
 * {@link WeeklyAdminDigestJob}).
 */
@Configuration
@EnableScheduling
public class EmailConfig {

    /**
     * Real transport. Active only when {@code app.email.enabled=true}, in which
     * case Spring Boot's mail auto-configuration has also built the
     * {@link JavaMailSender} from {@code spring.mail.*}. Declared before the
     * logging fallback so its {@link ConditionalOnMissingBean} backs off.
     */
    @Bean
    @ConditionalOnProperty(prefix = "app.email", name = "enabled", havingValue = "true")
    public EmailSender javaMailEmailSender(
            final JavaMailSender mailSender, final EmailProperties properties) {
        return new JavaMailEmailSender(mailSender, properties);
    }

    /**
     * Fallback sender that only logs, used whenever {@code app.email.enabled} is
     * not {@code true}. Any other {@link EmailSender} bean also takes over with
     * no other change.
     */
    @Bean
    @ConditionalOnMissingBean(EmailSender.class)
    public EmailSender loggingEmailSender(final EmailProperties properties) {
        return new LoggingEmailSender(properties.isEnabled());
    }
}
