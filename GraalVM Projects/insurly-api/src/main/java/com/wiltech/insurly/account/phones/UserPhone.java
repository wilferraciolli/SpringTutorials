package com.wiltech.insurly.account.phones;

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

/** A phone number saved to an account. */
@Entity
@Table(name = "user_phone")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserPhone implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    private String label;

    @Column(name = "phone_number")
    private String number;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void updateValues(
            final String label,
            final String number,
            final boolean primary,
            final Instant now) {
        this.label = label;
        this.number = number;
        this.primary = primary;
        this.updatedAt = now;
    }

    public void clearPrimary(final Instant now) {
        this.primary = false;
        this.updatedAt = now;
    }
}
