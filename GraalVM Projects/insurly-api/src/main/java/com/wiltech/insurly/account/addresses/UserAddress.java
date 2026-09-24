package com.wiltech.insurly.account.addresses;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** A postal address saved to an account. */
@Entity
@Table(name = "user_address")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    private String label;

    private String line1;

    private String line2;

    private String city;

    private String region;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void updateValues(
            final String label,
            final String line1,
            final String line2,
            final String city,
            final String region,
            final String postalCode,
            final String countryCode,
            final boolean primary,
            final Instant now) {
        this.label = label;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.primary = primary;
        this.updatedAt = now;
    }

    public void clearPrimary(final Instant now) {
        this.primary = false;
        this.updatedAt = now;
    }
}
