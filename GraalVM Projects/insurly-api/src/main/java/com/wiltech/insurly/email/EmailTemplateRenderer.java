package com.wiltech.insurly.email;

import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

/**
 * Renders an HTML email body from a Thymeleaf template under
 * {@code src/main/resources/templates/email/} and a model map.
 *
 * <p>Depends only on {@link ITemplateEngine}; Spring Boot's Thymeleaf
 * auto-configuration provides the concrete engine, and a plain
 * {@code TemplateEngine} can be injected in tests.
 */
@Component
public class EmailTemplateRenderer {

    private final ITemplateEngine templateEngine;

    public EmailTemplateRenderer(final ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * @param templateName file name without the {@code email/} prefix or
     *                     {@code .html} suffix, e.g. {@code "admin-new-quote"}
     */
    public String render(final String templateName, final Map<String, Object> model) {
        final Context context = new Context(Locale.ENGLISH, model);
        return templateEngine.process("email/" + templateName, context);
    }
}
