package com.wiltech.insurly.email.model;

/** One quote line in the Monday admin digest. */
public record DigestQuoteRow(
        String quoteRef,
        String customer,
        String createdAt,
        long ageDays,
        long daysToExpiry,
        String premiumStandard) {
}
