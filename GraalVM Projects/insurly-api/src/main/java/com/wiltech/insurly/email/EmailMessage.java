package com.wiltech.insurly.email;

import java.util.List;

/**
 * A ready-to-send email: recipients plus a rendered subject and body. Built by
 * the notification services, handed to an {@link EmailSender}.
 *
 * @param to        recipient addresses (at least one)
 * @param subject   plain-text subject line
 * @param htmlBody  the rendered HTML body
 * @param textBody  a plain-text alternative (may be {@code null})
 */
public record EmailMessage(List<String> to, String subject, String htmlBody, String textBody) {

    public EmailMessage {
        to = to == null ? List.of() : List.copyOf(to);
    }

    public static EmailMessage of(final List<String> to, final String subject, final String htmlBody) {
        return new EmailMessage(to, subject, htmlBody, null);
    }
}
