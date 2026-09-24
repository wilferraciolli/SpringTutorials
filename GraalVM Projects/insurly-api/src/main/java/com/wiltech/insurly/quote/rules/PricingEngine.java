package com.wiltech.insurly.quote.rules;

/**
 * Contract for turning a risk profile into tiered premiums. The MVP implementation
 * is a mock deterministic formula; a real rate provider can replace it later
 * without the RestService or ApplicationService layers changing.
 */
public interface PricingEngine {

    CarQuotePricing priceCarQuote(CarRiskProfile riskProfile);
}
