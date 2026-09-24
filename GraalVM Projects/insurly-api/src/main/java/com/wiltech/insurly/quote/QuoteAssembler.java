package com.wiltech.insurly.quote;

import com.wiltech.insurly.quote.rules.CarRiskProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class QuoteAssembler {

    public QuoteResource convertToDTO(Quote entity) {
        return QuoteResource.builder()
                .id(entity.getId())
                .guestToken(entity.getGuestToken())
                .status(entity.getStatus())
                .premiumBasic(entity.getPremiumBasic())
                .premiumStandard(entity.getPremiumStandard())
                .premiumPremium(entity.getPremiumPremium())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    /**
     * Normalises the wizard input into the plain risk inputs the pricing engine
     * needs (ages/counts, not dates). {@code today} is passed in so the engine
     * chain stays a pure function of its arguments.
     */
    public CarRiskProfile convertToRiskProfile(QuoteResource payload, LocalDate today) {
        int driverAge = Period.between(payload.getDriver().getDateOfBirth(), today).getYears();
        int vehicleAgeYears = Math.max(today.getYear() - payload.getVehicle().getYear(), 0);

        return new CarRiskProfile(
                driverAge,
                payload.getDriver().getYearsLicensed(),
                payload.getHistory().getAccidentsLast5Years(),
                payload.getHistory().getViolationsLast5Years(),
                vehicleAgeYears,
                payload.getVehicle().getPrimaryUse());
    }
}
