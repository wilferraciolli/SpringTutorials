package com.wiltech.insurly.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Binds the {@code app.email.*} block in application.yaml. */
@Component
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {

    /** Master switch for a real transport to honour. Rendering/logging happens regardless. */
    private boolean enabled = false;

    private String from = "no-reply@insurly.example";
    private String fromName = "insurly";

    /** Absolute base URL of the frontend, used to build links inside emails. */
    private String appBaseUrl = "http://localhost:4200";

    /** Recipients of the new-quote alert and the Monday digest. */
    private List<String> adminRecipients = new ArrayList<>();

    private final WeeklyDigest weeklyDigest = new WeeklyDigest();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(final String fromName) {
        this.fromName = fromName;
    }

    public String getAppBaseUrl() {
        return appBaseUrl;
    }

    public void setAppBaseUrl(final String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    public List<String> getAdminRecipients() {
        return adminRecipients;
    }

    public void setAdminRecipients(final List<String> adminRecipients) {
        this.adminRecipients = adminRecipients;
    }

    public WeeklyDigest getWeeklyDigest() {
        return weeklyDigest;
    }

    public static class WeeklyDigest {
        private boolean enabled = true;
        private String cron = "0 0 8 * * MON";
        private String zone = "Europe/London";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(final String cron) {
            this.cron = cron;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(final String zone) {
            this.zone = zone;
        }
    }
}
