package com.wiltech.insurly.admin.dashboard;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@JsonRootName("dashboard")
@Value
@Builder
public class AdminDashboardResource extends BaseDTO {

    long quoteTotal;
    long quotesComplete;
    long quotesDraft;
    long quotesLast7Days;
    long quotesLast30Days;

    long userTotal;
    long usersManaged;
    long usersSelfService;
    long usersLast30Days;

    List<QuoteRow> expiringSoon;
    List<QuoteRow> recentQuotes;

    /** Quotes created per day over the last 30 days, zero-filled — feeds the trend chart. */
    List<DailyQuoteCount> quotesTrend;

    /** Quotes over the last 30 days grouped by provider — feeds the by-provider chart. */
    List<ProviderQuoteCount> quotesByProvider;

    /** A quote line on the dashboard, with its customer and provider resolved. */
    public record QuoteRow(
            UUID quoteId,
            UUID userId,
            String userName,
            String userEmail,
            String providerName,
            String status,
            BigDecimal premiumStandard,
            Instant createdAt,
            Instant expiresAt) {
    }

    public record DailyQuoteCount(LocalDate date, long count) {
    }

    public record ProviderQuoteCount(String providerName, long count) {
    }
}
