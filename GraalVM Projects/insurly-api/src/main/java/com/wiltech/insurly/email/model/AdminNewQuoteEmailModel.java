package com.wiltech.insurly.email.model;

/**
 * View model for {@code email/admin-new-quote.html} — the alert an admin gets
 * when someone requests a quote, with the proposer's details and the price.
 */
public record AdminNewQuoteEmailModel(
        String quoteRef,
        String createdAt,
        String expiresAt,
        String customerType,
        String driverName,
        int driverAge,
        int yearsLicensed,
        String licenseStatus,
        String vehicle,
        String primaryUse,
        int accidents,
        int violations,
        String liabilityLimit,
        boolean collision,
        boolean comprehensive,
        String deductible,
        String premiumBasic,
        String premiumStandard,
        String premiumPremium,
        String consoleUrl) {
}
