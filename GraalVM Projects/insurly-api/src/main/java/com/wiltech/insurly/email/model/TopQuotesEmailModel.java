package com.wiltech.insurly.email.model;

import java.util.List;

/**
 * View model for {@code email/user-top-quotes.html} — up to five ranked quote
 * options sent to the person who requested them.
 */
public record TopQuotesEmailModel(
        String firstName,
        List<QuoteOptionView> options,
        String validUntil,
        String reviewUrl) {
}
