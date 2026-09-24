package com.wiltech.insurly.quote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import tools.jackson.databind.ObjectMapper;

/**
 * Proves the immutable {@code @Value}/{@code @Builder} resource still round-trips
 * through Spring's configured Jackson 3 mapper without {@code @Jacksonized}
 * (which emits Jackson 2 packages Spring Boot 4 no longer ships).
 */
@JsonTest
class QuoteResourceJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deserializesNestedWizardInput() {
        String json = """
                {
                  "driver": {
                    "firstName": "Ada", "lastName": "Lovelace",
                    "dateOfBirth": "1990-05-01", "yearsLicensed": 10, "licenseStatus": "VALID"
                  },
                  "vehicle": { "make": "Toyota", "model": "Corolla", "year": 2020, "primaryUse": "COMMUTE" },
                  "history": { "accidentsLast5Years": 0, "violationsLast5Years": 1 },
                  "coverage": {
                    "liabilityLimit": "50/100/50", "collision": true,
                    "comprehensive": false, "deductible": 500.00
                  }
                }
                """;

        QuoteResource resource = objectMapper.readValue(json, QuoteResource.class);

        assertEquals("Ada", resource.getDriver().getFirstName());
        assertEquals(LocalDate.of(1990, 5, 1), resource.getDriver().getDateOfBirth());
        assertEquals(LicenseStatus.VALID, resource.getDriver().getLicenseStatus());
        assertEquals(PrimaryUse.COMMUTE, resource.getVehicle().getPrimaryUse());
        assertEquals(2020, resource.getVehicle().getYear());
        assertEquals(1, resource.getHistory().getViolationsLast5Years());
        assertTrue(resource.getCoverage().isCollision());
        assertEquals(0, new BigDecimal("500.00").compareTo(resource.getCoverage().getDeductible()));
    }

    @Test
    void serializesComputedFields() {
        QuoteResource resource = QuoteResource.builder()
                .status(QuoteStatus.COMPLETE)
                .premiumStandard(new BigDecimal("720.00"))
                .build();

        String json = objectMapper.writeValueAsString(resource);

        assertTrue(json.contains("\"status\":\"COMPLETE\""), json);
        assertTrue(json.contains("\"premiumStandard\":720.00"), json);
    }
}
