package com.wiltech.insurly.quote.rules;

import com.wiltech.insurly.quote.PrimaryUse;

/**
 * Plain risk inputs the pricing engine needs — already normalised away from HTTP
 * DTOs (ages/counts, not dates). The engine is a pure function of this record.
 */
public record CarRiskProfile(
        int driverAge,
        int yearsLicensed,
        int accidentsLast5Years,
        int violationsLast5Years,
        int vehicleAgeYears,
        PrimaryUse primaryUse) {
}
