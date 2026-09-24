package com.wiltech.insurly.email;

import com.wiltech.insurly.email.model.AdminNewQuoteEmailModel;
import com.wiltech.insurly.email.model.QuoteOptionView;
import com.wiltech.insurly.email.model.TopQuotesEmailModel;
import com.wiltech.insurly.quote.CoverageResource;
import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Composes and sends the two quote-related emails:
 *
 * <ul>
 *   <li>{@link #notifyAdminsOfNewQuote} — alert to the admin recipients with the
 *       proposer's details and the price. Wired into the quote-create flow.</li>
 *   <li>{@link #sendTopQuotesToUser} / {@link #sendTierQuotesToUser} — the ranked
 *       options for the person who asked. No Phase&nbsp;1 trigger yet — call it
 *       from wherever "email me my quotes" lands (or the insurer panel, docs/06).</li>
 * </ul>
 */
@Service
public class QuoteEmailService {

    private static final Logger log = LoggerFactory.getLogger(QuoteEmailService.class);

    private final EmailTemplateRenderer renderer;
    private final EmailSender sender;
    private final EmailProperties properties;
    private final EmailFormatter fmt;

    public QuoteEmailService(
            final EmailTemplateRenderer renderer,
            final EmailSender sender,
            final EmailProperties properties,
            final EmailFormatter fmt) {
        this.renderer = renderer;
        this.sender = sender;
        this.properties = properties;
        this.fmt = fmt;
    }

    // --- admin: new quote request -------------------------------------

    public void notifyAdminsOfNewQuote(final QuoteResource payload, final Quote saved) {
        final List<String> recipients = properties.getAdminRecipients();
        if (recipients.isEmpty()) {
            log.debug("No app.email.admin-recipients configured — skipping new-quote alert for {}", saved.getId());
            return;
        }

        final boolean guest = saved.getUserId() == null;
        final AdminNewQuoteEmailModel model = new AdminNewQuoteEmailModel(
                fmt.ref(saved.getId()),
                fmt.dateTime(saved.getCreatedAt()),
                fmt.date(saved.getExpiresAt()),
                guest ? "Guest" : "Account customer",
                payload.getDriver().getFirstName() + " " + payload.getDriver().getLastName(),
                fmt.ageYears(payload.getDriver().getDateOfBirth()),
                payload.getDriver().getYearsLicensed(),
                payload.getDriver().getLicenseStatus().getDescription(),
                payload.getVehicle().getYear() + " " + payload.getVehicle().getMake() + " " + payload.getVehicle().getModel(),
                payload.getVehicle().getPrimaryUse().getDescription(),
                payload.getHistory().getAccidentsLast5Years(),
                payload.getHistory().getViolationsLast5Years(),
                payload.getCoverage().getLiabilityLimit(),
                payload.getCoverage().isCollision(),
                payload.getCoverage().isComprehensive(),
                fmt.money(payload.getCoverage().getDeductible()),
                fmt.money(saved.getPremiumBasic()),
                fmt.money(saved.getPremiumStandard()),
                fmt.money(saved.getPremiumPremium()),
                guest
                        ? properties.getAppBaseUrl() + "/admin"
                        : properties.getAppBaseUrl() + "/admin/customers/" + saved.getUserId());

        final String html = renderer.render("admin-new-quote", Map.of("m", model));
        sender.send(EmailMessage.of(
                recipients,
                "New quote request — " + model.driverName() + " · " + model.premiumStandard() + "/yr",
                html));
    }

    // --- user: top quotes ------------------------------------------

    /** Generic: up to five ranked options (mark one {@code highlighted}). */
    public void sendTopQuotesToUser(
            final String toEmail,
            final String firstName,
            final List<QuoteOptionView> options,
            final Instant validUntil) {
        if (toEmail == null || toEmail.isBlank()) {
            log.debug("No recipient address — skipping top-quotes email");
            return;
        }
        final List<QuoteOptionView> top5 = options.stream().limit(5).toList();
        final TopQuotesEmailModel model = new TopQuotesEmailModel(
                firstName == null ? "" : firstName,
                top5,
                validUntil == null ? "" : fmt.date(validUntil),
                properties.getAppBaseUrl() + "/quote/car/review");

        final String html = renderer.render("user-top-quotes", Map.of("m", model));
        sender.send(EmailMessage.of(List.of(toEmail), "Your car insurance quotes", html));
    }

    /**
     * Convenience for the current 3-tier model: turns one priced
     * {@link QuoteResource} into Basic / Standard / Premium options.
     */
    public void sendTierQuotesToUser(final String toEmail, final String firstName, final QuoteResource quote) {
        final String cover = coverSummary(quote.getCoverage());
        final List<QuoteOptionView> options = List.of(
                tier(1, "Basic cover", quote.getPremiumBasic(), cover, false),
                tier(2, "Standard cover", quote.getPremiumStandard(), cover, true),
                tier(3, "Premium cover", quote.getPremiumPremium(), cover, false));
        sendTopQuotesToUser(toEmail, firstName, options, quote.getExpiresAt());
    }

    private QuoteOptionView tier(
            final int rank,
            final String name,
            final BigDecimal annual,
            final String cover,
            final boolean highlighted) {
        final String monthly = annual == null
                ? ""
                : fmt.money(annual.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP)) + "/mo";
        return new QuoteOptionView(rank, name, fmt.money(annual) + "/yr", monthly, cover, highlighted);
    }

    private String coverSummary(final CoverageResource c) {
        final String level = c.isComprehensive() ? "Comprehensive" : c.isCollision() ? "Collision" : "Liability only";
        return level + " · " + fmt.money(c.getDeductible()) + " excess · " + c.getLiabilityLimit() + " liability";
    }
}
