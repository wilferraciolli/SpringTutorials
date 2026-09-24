package com.wiltech.insurly.quote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * A priced car-insurance quote. Phase 1 persists only the quote row and its three
 * tier premiums; the driver/vehicle/history/coverage wizard input is not stored
 * yet (see docs/03-database-schema.md).
 */
@Entity
@Table(name = "quote")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Quote implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private QuoteStatus status;

    @NotNull
    private UUID guestToken;

    private UUID userId;

    /** The mock provider this quote was routed to (see admin.providers.Provider). */
    private UUID providerId;

    @NotNull
    private BigDecimal premiumBasic;

    @NotNull
    private BigDecimal premiumStandard;

    @NotNull
    private BigDecimal premiumPremium;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant expiresAt;

    public void updateValues(
            QuoteStatus status,
            BigDecimal premiumBasic,
            BigDecimal premiumStandard,
            BigDecimal premiumPremium,
            Instant expiresAt) {
        this.status = status;
        this.premiumBasic = premiumBasic;
        this.premiumStandard = premiumStandard;
        this.premiumPremium = premiumPremium;
        this.expiresAt = expiresAt;
    }

    /** Attach a previously anonymous guest quote to an account (see the account/quotes claim flow). */
    public void attachTo(UUID userId) {
        this.userId = userId;
    }
}
