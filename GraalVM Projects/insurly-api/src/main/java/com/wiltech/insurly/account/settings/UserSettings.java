package com.wiltech.insurly.account.settings;

import com.wiltech.insurly.settings.SupportedCurrency;
import com.wiltech.insurly.settings.SupportedLanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * A signed-in user's personal display preferences (see
 * {@code com.wiltech.insurly.settings.SystemSettings}). The columns stay
 * nullable for users who have never saved — the API fills those in from the
 * system settings on read, so the response is never empty. Saving always
 * writes all three. The primary key is the owning {@code app_user} id: one
 * row per user, at most.
 */
@Entity
@Table(name = "user_settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    /** IANA zone id, or null if the user has never saved (the system timezone applies). */
    @Column(name = "timezone")
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private SupportedLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private SupportedCurrency currency;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void update(final String timezone,
                       final SupportedLanguage language,
                       final SupportedCurrency currency,
                       final Instant now) {
        this.timezone = timezone;
        this.language = language;
        this.currency = currency;
        this.updatedAt = now;
    }

    static UserSettings empty(final UUID userId, final Instant now) {
        return UserSettings.builder()
                .userId(userId)
                .updatedAt(now)
                .build();
    }
}
