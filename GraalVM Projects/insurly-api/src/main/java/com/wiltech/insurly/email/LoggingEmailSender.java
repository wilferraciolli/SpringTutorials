package com.wiltech.insurly.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link EmailSender}: logs the message instead of sending it, so the
 * templates and the notification wiring can be exercised end to end before an
 * SMTP transport exists. Registered by {@code EmailConfig} only when no other
 * {@code EmailSender} bean is present.
 */
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    private final boolean enabled;

    public LoggingEmailSender(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void send(final EmailMessage message) {
        if (message.to().isEmpty()) {
            log.warn("[email:stub] not sending \"{}\" — no recipients", message.subject());
            return;
        }
        log.info(
                "[email:stub] {} | to={} | subject=\"{}\"\n{}",
                enabled ? "WOULD SEND (no transport configured)" : "email disabled (app.email.enabled=false)",
                message.to(),
                message.subject(),
                message.htmlBody());
    }
}
