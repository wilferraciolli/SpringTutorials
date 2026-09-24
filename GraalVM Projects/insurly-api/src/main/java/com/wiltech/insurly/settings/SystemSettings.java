package com.wiltech.insurly.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * The single global row of platform-wide display preferences: timezone,
 * language and currency. Exactly one row exists (seeded by Flyway in
 * {@code V6__system_settings.sql}); the application always reads and updates
 * that one row rather than inserting more.
 */
@Entity
@Table(name = "system_settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SystemSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private SupportedLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
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
}
