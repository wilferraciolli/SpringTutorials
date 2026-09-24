package com.wiltech.insurly.email.model;

/** One row in the "your top quotes" email. */
public record QuoteOptionView(
        int rank,
        String name,
        String annualPremium,
        String monthlyPremium,
        String coverSummary,
        boolean highlighted) {
}
