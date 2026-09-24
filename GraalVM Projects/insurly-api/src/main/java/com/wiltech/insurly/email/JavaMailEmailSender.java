package com.wiltech.insurly.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Real {@link EmailSender}: builds a {@link MimeMessage} from the composed
 * {@link EmailMessage} and sends it through the configured SMTP transport
 * ({@code spring.mail.*}, set via the {@code MAIL_*} env vars — provider-agnostic).
 *
 * <p>Registered by {@link EmailConfig} in place of {@link LoggingEmailSender}
 * when {@code app.email.enabled=true}. Failures are logged and swallowed: every
 * caller treats notification email as fire-and-forget, same contract as the
 * logging stub.
 */
public class JavaMailEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(JavaMailEmailSender.class);

    private final JavaMailSender mailSender;
    private final EmailProperties properties;

    public JavaMailEmailSender(final JavaMailSender mailSender, final EmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void send(final EmailMessage message) {
        if (message.to().isEmpty()) {
            log.warn("[email] not sending \"{}\" — no recipients", message.subject());
            return;
        }

        final boolean hasText = message.textBody() != null;
        try {
            final MimeMessage mime = mailSender.createMimeMessage();
            final MimeMessageHelper helper =
                    new MimeMessageHelper(mime, hasText, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(
                    properties.getFrom(), properties.getFromName(), StandardCharsets.UTF_8.name()));
            helper.setTo(message.to().toArray(String[]::new));
            helper.setSubject(message.subject());
            if (hasText) {
                helper.setText(message.textBody(), message.htmlBody());
            } else {
                helper.setText(message.htmlBody(), true);
            }

            mailSender.send(mime);
            log.info("[email] sent \"{}\" to {}", message.subject(), message.to());
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("[email] failed to send \"{}\" to {}: {}",
                    message.subject(), message.to(), e.getMessage(), e);
        }
    }
}
