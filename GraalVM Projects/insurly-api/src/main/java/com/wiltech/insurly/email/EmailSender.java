package com.wiltech.insurly.email;

/**
 * The seam between "an email has been composed" and "an email has been sent".
 *
 * <p>The only implementation today is {@link LoggingEmailSender}, which logs the
 * rendered message. When email goes live, add {@code spring-boot-starter-mail}
 * and register a {@code JavaMailSender}-backed {@code EmailSender} bean — it
 * replaces the logging one automatically (see {@code EmailConfig}). Callers
 * never change.
 */
public interface EmailSender {

    void send(EmailMessage message);
}
