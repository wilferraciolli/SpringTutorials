package com.wiltech.insurly.admin.dashboard;

import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.account.identity.UserOwnership;
import com.wiltech.insurly.admin.access.AdminAccessService;
import com.wiltech.insurly.admin.dashboard.AdminDashboardResource.DailyQuoteCount;
import com.wiltech.insurly.admin.dashboard.AdminDashboardResource.ProviderQuoteCount;
import com.wiltech.insurly.admin.dashboard.AdminDashboardResource.QuoteRow;
import com.wiltech.insurly.admin.providers.Provider;
import com.wiltech.insurly.admin.providers.ProviderRepository;
import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteRepository;
import com.wiltech.insurly.quote.QuoteStatus;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardAppService {

    private static final Duration EXPIRY_WINDOW = Duration.ofDays(30);
    private static final Duration TREND_WINDOW = Duration.ofDays(30);
    private static final String UNASSIGNED_PROVIDER = "Unassigned";

    private final QuoteRepository quotes;
    private final AppUserRepository users;
    private final ProviderRepository providers;
    private final AdminAccessService adminAccess;
    private final Clock clock;

    @Transactional(readOnly = true)
    public AdminDashboardResource build() {
        adminAccess.requireAdmin();

        final Instant now = clock.instant();
        final List<Quote> expiring =
                quotes.findByExpiresAtBetweenOrderByExpiresAtAsc(now, now.plus(EXPIRY_WINDOW));
        final List<Quote> recent = quotes.findTop10ByOrderByCreatedAtDesc();
        final List<Quote> trendWindow = quotes.findByCreatedAtGreaterThanEqual(now.minus(TREND_WINDOW));

        final Map<UUID, CustomerInfo> customers = resolveCustomers(expiring, recent, trendWindow);
        final Map<UUID, String> providerNames = resolveProviderNames(expiring, recent, trendWindow);

        return AdminDashboardResource.builder()
                .quoteTotal(quotes.count())
                .quotesComplete(quotes.countByStatus(QuoteStatus.COMPLETE))
                .quotesDraft(quotes.countByStatus(QuoteStatus.DRAFT))
                .quotesLast7Days(quotes.countByCreatedAtAfter(now.minus(Duration.ofDays(7))))
                .quotesLast30Days(quotes.countByCreatedAtAfter(now.minus(Duration.ofDays(30))))
                .userTotal(users.count())
                .usersManaged(users.countByOwnership(UserOwnership.MANAGED))
                .usersSelfService(users.countByOwnership(UserOwnership.SELF_SERVICE))
                .usersLast30Days(users.countByCreatedAtAfter(now.minus(Duration.ofDays(30))))
                .expiringSoon(expiring.stream().map(q -> row(q, customers, providerNames)).toList())
                .recentQuotes(recent.stream().map(q -> row(q, customers, providerNames)).toList())
                .quotesTrend(dailyTrend(trendWindow, now))
                .quotesByProvider(byProvider(trendWindow, providerNames))
                .build();
    }

    /** One entry per day for the last 30 days (oldest first), zero-filled — no gaps for the chart. */
    private List<DailyQuoteCount> dailyTrend(final List<Quote> quotesInWindow, final Instant now) {
        final LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);
        final Map<LocalDate, Long> counts = new LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            counts.put(today.minusDays(i), 0L);
        }
        for (final Quote q : quotesInWindow) {
            final LocalDate day = LocalDate.ofInstant(q.getCreatedAt(), ZoneOffset.UTC);
            counts.computeIfPresent(day, (d, count) -> count + 1);
        }
        return counts.entrySet().stream()
                .map(e -> new DailyQuoteCount(e.getKey(), e.getValue()))
                .toList();
    }

    private List<ProviderQuoteCount> byProvider(
            final List<Quote> quotesInWindow, final Map<UUID, String> providerNames) {
        final Map<String, Long> counts = new LinkedHashMap<>();
        for (final Quote q : quotesInWindow) {
            final String name = providerNames.getOrDefault(q.getProviderId(), UNASSIGNED_PROVIDER);
            counts.merge(name, 1L, Long::sum);
        }
        return counts.entrySet().stream()
                .map(e -> new ProviderQuoteCount(e.getKey(), e.getValue()))
                .sorted((a, b) -> Long.compare(b.count(), a.count()))
                .toList();
    }

    @SafeVarargs
    private Map<UUID, CustomerInfo> resolveCustomers(final List<Quote>... quoteLists) {
        final Set<UUID> ids = new LinkedHashSet<>();
        for (final List<Quote> list : quoteLists) {
            list.forEach(q -> {
                if (q.getUserId() != null) {
                    ids.add(q.getUserId());
                }
            });
        }
        if (ids.isEmpty()) {
            return Map.of();
        }
        return users.findAllById(ids).stream()
                .collect(Collectors.toMap(AppUser::getId, AdminDashboardAppService::customerInfoOf, (x, y) -> x));
    }

    @SafeVarargs
    private Map<UUID, String> resolveProviderNames(final List<Quote>... quoteLists) {
        final Set<UUID> ids = new LinkedHashSet<>();
        for (final List<Quote> list : quoteLists) {
            list.forEach(q -> {
                if (q.getProviderId() != null) {
                    ids.add(q.getProviderId());
                }
            });
        }
        if (ids.isEmpty()) {
            return Map.of();
        }
        return providers.findAllById(ids).stream()
                .collect(Collectors.toMap(Provider::getId, Provider::getName, (x, y) -> x));
    }

    private static CustomerInfo customerInfoOf(final AppUser user) {
        final String full = StringUtils.trimToEmpty(
                StringUtils.trimToEmpty(user.getFirstName()) + " " + StringUtils.trimToEmpty(user.getLastName()));
        final String name = StringUtils.isNotBlank(full)
                ? full
                : StringUtils.firstNonBlank(user.getDisplayName(), user.getEmail(), "Unknown");
        return new CustomerInfo(name, user.getEmail());
    }

    private static QuoteRow row(
            final Quote q, final Map<UUID, CustomerInfo> customers, final Map<UUID, String> providerNames) {
        final CustomerInfo customer = q.getUserId() == null ? null : customers.get(q.getUserId());
        final String name = q.getUserId() == null ? "Guest" : StringUtils.defaultIfBlank(
                customer == null ? null : customer.name(), "Unknown");
        final String email = customer == null ? null : customer.email();
        final String providerName = providerNames.getOrDefault(q.getProviderId(), UNASSIGNED_PROVIDER);
        return new QuoteRow(
                q.getId(),
                q.getUserId(),
                name,
                email,
                providerName,
                Objects.toString(q.getStatus(), null),
                q.getPremiumStandard(),
                q.getCreatedAt(),
                q.getExpiresAt());
    }

    private record CustomerInfo(String name, String email) {
    }
}
