package com.wiltech.insurly.quote.rules;

import java.math.BigDecimal;

/** Annual premium for each coverage tier the results page shows. */
public record CarQuotePricing(BigDecimal basic, BigDecimal standard, BigDecimal premium) {
}
