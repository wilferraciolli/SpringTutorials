package com.wiltech.insurly.quote.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wiltech.insurly.quote.PrimaryUse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CarInsuranceRulesEngineTest {

    private final CarInsuranceRulesEngine engine = new CarInsuranceRulesEngine();

    @Test
    void pricesLowRiskProfileWithExperienceDiscountAndTierSpread() {
        CarRiskProfile profile = new CarRiskProfile(40, 15, 0, 0, 6, PrimaryUse.PLEASURE);

        CarQuotePricing pricing = engine.priceCarQuote(profile);

        // 800.00 base * 0.90 experience discount, all other factors neutral.
        assertEquals(new BigDecimal("720.00"), pricing.standard());
        assertEquals(new BigDecimal("576.00"), pricing.basic());
        assertEquals(new BigDecimal("972.00"), pricing.premium());
    }

    @Test
    void youngDriverWithIncidentsPaysMoreThanCleanProfile() {
        CarRiskProfile clean = new CarRiskProfile(40, 15, 0, 0, 6, PrimaryUse.PLEASURE);
        CarRiskProfile risky = new CarRiskProfile(19, 1, 2, 1, 1, PrimaryUse.BUSINESS);

        assertTrue(engine.priceCarQuote(risky).standard()
                .compareTo(engine.priceCarQuote(clean).standard()) > 0);
    }

    @Test
    void sameInputsProduceSameOutput() {
        CarRiskProfile profile = new CarRiskProfile(30, 8, 1, 0, 4, PrimaryUse.COMMUTE);

        assertEquals(engine.priceCarQuote(profile), engine.priceCarQuote(profile));
    }
}
