package com.wiltech.insurly.account.cars;

import com.wiltech.insurly.quote.PrimaryUse;
import jakarta.persistence.Column;
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
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** A car saved to an account. Owned by exactly one {@code app_user}. */
@Entity
@Table(name = "user_car")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCar implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    private String make;

    private String model;

    private int year;

    private String vin;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_use", nullable = false)
    private PrimaryUse primaryUse;

    private String nickname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void updateValues(
            final String make,
            final String model,
            final int year,
            final String vin,
            final PrimaryUse primaryUse,
            final String nickname,
            final Instant now) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.primaryUse = primaryUse;
        this.nickname = nickname;
        this.updatedAt = now;
    }
}
