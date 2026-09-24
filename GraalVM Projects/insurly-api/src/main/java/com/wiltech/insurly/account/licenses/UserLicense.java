package com.wiltech.insurly.account.licenses;

import com.wiltech.insurly.quote.LicenseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** A driving licence saved to an account. The number is stored encrypted. */
@Entity
@Table(name = "user_license")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLicense implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Convert(converter = LicenseNumberConverter.class)
    @Column(name = "license_number", nullable = false, columnDefinition = "bytea")
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseStatus status;

    @Column(name = "issuing_region")
    private String issuingRegion;

    @Column(name = "issued_on")
    private LocalDate issuedOn;

    @Column(name = "expires_on")
    private LocalDate expiresOn;

    @Column(name = "years_held")
    private Integer yearsHeld;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void updateValues(
            final String licenseNumber,
            final LicenseStatus status,
            final String issuingRegion,
            final LocalDate issuedOn,
            final LocalDate expiresOn,
            final Integer yearsHeld,
            final Instant now) {
        this.licenseNumber = licenseNumber;
        this.status = status;
        this.issuingRegion = issuingRegion;
        this.issuedOn = issuedOn;
        this.expiresOn = expiresOn;
        this.yearsHeld = yearsHeld;
        this.updatedAt = now;
    }
}
