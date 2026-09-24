package com.wiltech.insurly.email;

import com.wiltech.insurly.settings.SystemSettingsAppService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Shared, presentation-only helpers for the email view models. */
@Component
public class EmailFormatter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm");
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM");

    private final SystemSettingsAppService settings;
    private final EmailProperties properties;

    public EmailFormatter(final SystemSettingsAppService settings, final EmailProperties properties) {
        this.settings = settings;
        this.properties = properties;
    }

    /** The business display zone (reuses the digest zone). Falls back to UTC. */
    public ZoneId zone() {
        try {
            return ZoneId.of(properties.getWeeklyDigest().getZone());
        } catch (final RuntimeException ex) {
            return ZoneId.of("UTC");
        }
    }

    /** e.g. {@code "£864"} — whole units, current display currency. */
    public String money(final BigDecimal amount) {
        final String symbol = settings.currentCurrency().getSymbol();
        if (amount == null) {
            return symbol + "0";
        }
        return symbol + amount.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    /** Short, human quote reference from the id, e.g. {@code "Q-8F3K2A1B"}. */
    public String ref(final UUID id) {
        return "Q-" + id.toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    public String date(final Instant instant) {
        return DATE.format(instant.atZone(zone()));
    }

    public String dateTime(final Instant instant) {
        return DATE_TIME.format(instant.atZone(zone()));
    }

    public String dayMonth(final Instant instant) {
        return DAY_MONTH.format(instant.atZone(zone()));
    }

    public int ageYears(final LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now(zone())).getYears();
    }
}
