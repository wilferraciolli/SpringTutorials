package com.wiltech.insurly.email;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wiltech.insurly.email.model.AdminNewQuoteEmailModel;
import com.wiltech.insurly.email.model.DigestQuoteRow;
import com.wiltech.insurly.email.model.QuoteOptionView;
import com.wiltech.insurly.email.model.TopQuotesEmailModel;
import com.wiltech.insurly.email.model.WeeklyDigestEmailModel;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Renders each email template against a sample model with the same
 * {@code SpringTemplateEngine} (SpringEL) the app uses at runtime — just no
 * Spring context and no DB. Catches template syntax errors, broken fragment
 * references and bad model accessors.
 */
class EmailTemplateRenderTest {

    private final EmailTemplateRenderer renderer = new EmailTemplateRenderer(buildEngine());

    private static SpringTemplateEngine buildEngine() {
        final ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    @Test
    void rendersAdminNewQuote() {
        final AdminNewQuoteEmailModel model = new AdminNewQuoteEmailModel(
                "Q-8F3K2A1B", "1 Sep 2026, 14:20", "1 Oct 2026", "Guest",
                "Jordan Rivera", 34, 12, "Valid",
                "2019 Toyota Corolla", "Commute", 0, 1,
                "50/100/50", true, true, "£500",
                "£576", "£768", "£1014",
                "http://localhost:4200/admin");

        final String html = renderer.render("admin-new-quote", Map.of("m", model));

        assertAll(
                () -> assertTrue(html.contains("New quote request"), "heading"),
                () -> assertTrue(html.contains("Jordan Rivera"), "driver name"),
                () -> assertTrue(html.contains("2019 Toyota Corolla"), "vehicle"),
                () -> assertTrue(html.contains("£768"), "standard premium"),
                () -> assertTrue(html.contains("http://localhost:4200/admin"), "console link"),
                () -> assertTrue(html.contains("insurly"), "layout header"));
    }

    @Test
    void rendersUserTopQuotes() {
        final TopQuotesEmailModel model = new TopQuotesEmailModel(
                "Jordan",
                List.of(
                        new QuoteOptionView(1, "Basic cover", "£576/yr", "£48/mo", "Comprehensive · £500 excess", false),
                        new QuoteOptionView(2, "Standard cover", "£768/yr", "£64/mo", "Comprehensive · £500 excess", true),
                        new QuoteOptionView(3, "Premium cover", "£1014/yr", "£85/mo", "Comprehensive · £250 excess", false)),
                "1 Oct 2026",
                "http://localhost:4200/quote/car/review");

        final String html = renderer.render("user-top-quotes", Map.of("m", model));

        assertAll(
                () -> assertTrue(html.contains("Your car insurance quotes"), "heading"),
                () -> assertTrue(html.contains(">Jordan<"), "greeting name interpolated"),
                () -> assertTrue(html.contains("Standard cover"), "option name"),
                () -> assertTrue(html.contains("Best value"), "highlighted tag"),
                () -> assertTrue(html.contains("£768/yr"), "price"),
                () -> assertTrue(html.contains("1 Oct 2026"), "validity"));
    }

    @Test
    void rendersWeeklyDigestWithRows() {
        final WeeklyDigestEmailModel model = new WeeklyDigestEmailModel(
                "25 Aug – 1 Sep 2026", 18, 16, 31, 4,
                List.of(new DigestQuoteRow("Q-AAAA1111", "Jordan Rivera", "25 Aug", 3, 27, "£864")),
                List.of(new DigestQuoteRow("Q-BBBB2222", "Guest", "20 Aug", 12, 2, "£712")),
                "http://localhost:4200/admin");

        final String html = renderer.render("admin-weekly-digest", Map.of("m", model));

        assertAll(
                () -> assertTrue(html.contains("Monday digest"), "heading"),
                () -> assertTrue(html.contains("25 Aug – 1 Sep 2026"), "week range"),
                () -> assertTrue(html.contains("Q-AAAA1111"), "open row ref"),
                () -> assertTrue(html.contains("Q-BBBB2222"), "expiring row ref"),
                () -> assertTrue(html.contains("27d"), "days to expiry"),
                () -> assertTrue(html.contains("Open quotes"), "section title"));
    }

    @Test
    void weeklyDigestHandlesEmptyLists() {
        final WeeklyDigestEmailModel model = new WeeklyDigestEmailModel(
                "25 Aug – 1 Sep 2026", 0, 0, 0, 0, List.of(), List.of(), "http://localhost:4200/admin");

        final String html = renderer.render("admin-weekly-digest", Map.of("m", model));

        assertAll(
                () -> assertTrue(html.contains("Nothing open right now."), "empty open state"),
                () -> assertTrue(html.contains("Nothing expiring this week."), "empty expiring state"));
    }
}
