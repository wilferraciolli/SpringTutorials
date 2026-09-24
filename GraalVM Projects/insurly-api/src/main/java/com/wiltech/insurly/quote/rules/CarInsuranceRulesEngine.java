package com.wiltech.insurly.quote.rules;

import com.wiltech.insurly.quote.PrimaryUse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * Mock Phase 1 pricing: a base premium scaled by deterministic risk multipliers.
 * No external calls, no randomness — same inputs always produce the same output.
 */
@Component
public class CarInsuranceRulesEngine implements PricingEngine {

    private static final BigDecimal BASE_PREMIUM = new BigDecimal("800.00");
    private static final BigDecimal BASIC_TIER_FACTOR = new BigDecimal("0.80");
    private static final BigDecimal PREMIUM_TIER_FACTOR = new BigDecimal("1.35");

    @Override
    public CarQuotePricing priceCarQuote(CarRiskProfile profile) {
        double multiplier = ageMultiplier(profile.driverAge())
                * experienceMultiplier(profile.yearsLicensed())
                * (1.0 + 0.25 * profile.accidentsLast5Years())
                * (1.0 + 0.15 * profile.violationsLast5Years())
                * vehicleAgeMultiplier(profile.vehicleAgeYears())
                * useMultiplier(profile.primaryUse());

        BigDecimal standard = BASE_PREMIUM
                .multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_UP);

        return new CarQuotePricing(
                standard.multiply(BASIC_TIER_FACTOR).setScale(2, RoundingMode.HALF_UP),
                standard,
                standard.multiply(PREMIUM_TIER_FACTOR).setScale(2, RoundingMode.HALF_UP));
    }

    private static double ageMultiplier(int driverAge) {
        if (driverAge < 21) {
            return 1.80;
        }
        if (driverAge < 25) {
            return 1.50;
        }
        if (driverAge >= 70) {
            return 1.30;
        }
        return 1.00;
    }

    private static double experienceMultiplier(int yearsLicensed) {
        if (yearsLicensed >= 10) {
            return 0.90;
        }
        if (yearsLicensed >= 5) {
            return 0.95;
        }
        if (yearsLicensed < 2) {
            return 1.15;
        }
        return 1.00;
    }

    private static double vehicleAgeMultiplier(int vehicleAgeYears) {
        if (vehicleAgeYears <= 2) {
            return 1.15;
        }
        if (vehicleAgeYears > 12) {
            return 1.10;
        }
        return 1.00;
    }

    private static double useMultiplier(PrimaryUse primaryUse) {
        return switch (primaryUse) {
            case BUSINESS -> 1.20;
            case COMMUTE -> 1.10;
            case PLEASURE -> 1.00;
        };
    }
}
