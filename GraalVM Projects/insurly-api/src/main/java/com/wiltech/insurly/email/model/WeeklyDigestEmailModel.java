package com.wiltech.insurly.email.model;

import java.util.List;

/**
 * View model for {@code email/admin-weekly-digest.html} — the Monday summary,
 * leading with quotes that are priced but not yet converted ("open").
 */
public record WeeklyDigestEmailModel(
        String weekRange,
        long newQuotes,
        long completedQuotes,
        long openQuotes,
        long expiringThisWeek,
        List<DigestQuoteRow> openRows,
        List<DigestQuoteRow> expiringRows,
        String consoleUrl) {
}
