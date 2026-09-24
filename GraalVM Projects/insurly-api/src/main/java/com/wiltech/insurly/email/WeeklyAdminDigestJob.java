package com.wiltech.insurly.email;

import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.email.model.DigestQuoteRow;
import com.wiltech.insurly.email.model.WeeklyDigestEmailModel;
import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteRepository;
import com.wiltech.insurly.quote.QuoteStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Monday-morning email to the admin recipients. Leads with <b>open quotes</b> —
 * priced and still valid but not converted to a policy — since that is the work
 * to chase. Also lists what's expiring in the next 7 days and the week's volume.
 *
 * <p>Cron and zone come from {@code app.email.weekly-digest.*}; the whole job is
 * switched off with {@code app.email.weekly-digest.enabled=false}.
 *
 * <p>Note: today every quote is saved {@code COMPLETE} (there is no bind/convert
 * step yet), so "open" is effectively "every live quote". When a conversion
 * concept exists, exclude converted quotes from {@link #openRows}.
 */
@Component
@ConditionalOnProperty(prefix = "app.email.weekly-digest", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WeeklyAdminDigestJob {

    private static final Logger log = LoggerFactory.getLogger(WeeklyAdminDigestJob.class);
    private static final int MAX_ROWS = 25;

    private final QuoteRepository quotes;
    private final AppUserRepository users;
    private final EmailTemplateRenderer renderer;
    private final EmailSender sender;
    private final EmailProperties properties;
    private final EmailFormatter fmt;
    private final Clock clock;

    public WeeklyAdminDigestJob(
            final QuoteRepository quotes,
            final AppUserRepository users,
            final EmailTemplateRenderer renderer,
            final EmailSender sender,
            final EmailProperties properties,
            final EmailFormatter fmt,
            final Clock clock) {
        this.quotes = quotes;
        this.users = users;
        this.renderer = renderer;
        this.sender = sender;
        this.properties = properties;
        this.fmt = fmt;
        this.clock = clock;
    }

    @Scheduled(cron = "${app.email.weekly-digest.cron}", zone = "${app.email.weekly-digest.zone}")
    public void sendWeeklyDigest() {
        try {
            run();
        } catch (final RuntimeException ex) {
            log.error("Weekly admin digest failed", ex);
        }
    }

    /** The digest build + send, split out so it can be triggered/tested directly. */
    @Transactional(readOnly = true)
    public void run() {
        final List<String> recipients = properties.getAdminRecipients();
        if (recipients.isEmpty()) {
            log.info("Weekly digest: no app.email.admin-recipients configured — nothing sent");
            return;
        }

        final Instant now = clock.instant();
        final Instant weekAgo = now.minus(Duration.ofDays(7));
        final Instant in7Days = now.plus(Duration.ofDays(7));

        final List<Quote> open = quotes.findTop50ByStatusAndExpiresAtAfterOrderByExpiresAtAsc(QuoteStatus.COMPLETE, now);
        final List<Quote> expiring = quotes.findByExpiresAtBetweenOrderByExpiresAtAsc(now, in7Days);

        final Map<UUID, String> names = resolveNames(open, expiring);

        final WeeklyDigestEmailModel model = new WeeklyDigestEmailModel(
                fmt.dayMonth(weekAgo) + " – " + fmt.date(now),
                quotes.countByCreatedAtAfter(weekAgo),
                quotes.countByStatusAndCreatedAtAfter(QuoteStatus.COMPLETE, weekAgo),
                quotes.countByStatusAndExpiresAtAfter(QuoteStatus.COMPLETE, now),
                expiring.size(),
                open.stream().limit(MAX_ROWS).map(q -> row(q, names, now)).toList(),
                expiring.stream().limit(MAX_ROWS).map(q -> row(q, names, now)).toList(),
                properties.getAppBaseUrl() + "/admin");

        final String html = renderer.render("admin-weekly-digest", Map.of("m", model));
        sender.send(EmailMessage.of(
                recipients,
                "insurly · Monday digest — " + model.openQuotes() + " open quote(s)",
                html));
    }

    private DigestQuoteRow row(final Quote q, final Map<UUID, String> names, final Instant now) {
        final long ageDays = Math.max(0, Duration.between(q.getCreatedAt(), now).toDays());
        final long toExpiry = Math.max(0, Duration.between(now, q.getExpiresAt()).toDays());
        final String who = q.getUserId() == null ? "Guest" : names.getOrDefault(q.getUserId(), "Account customer");
        return new DigestQuoteRow(fmt.ref(q.getId()), who, fmt.dayMonth(q.getCreatedAt()), ageDays, toExpiry,
                fmt.money(q.getPremiumStandard()));
    }

    private Map<UUID, String> resolveNames(final List<Quote> a, final List<Quote> b) {
        final Set<UUID> ids = Stream.concat(a.stream(), b.stream())
                .map(Quote::getUserId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (ids.isEmpty()) {
            return Map.of();
        }
        return users.findAllById(ids).stream()
                .collect(Collectors.toMap(AppUser::getId, WeeklyAdminDigestJob::displayNameOf, (x, y) -> x));
    }

    private static String displayNameOf(final AppUser user) {
        final String full = StringUtils.trimToEmpty(
                StringUtils.trimToEmpty(user.getFirstName()) + " " + StringUtils.trimToEmpty(user.getLastName()));
        if (StringUtils.isNotBlank(full)) {
            return full;
        }
        return StringUtils.firstNonBlank(user.getDisplayName(), user.getEmail(), "Account customer");
    }
}
