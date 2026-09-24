package com.wiltech.insurly.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CoverageResource {
    @JsonProperty("liabilityLimit")
    @NotEmpty(message = "{quote.coverage.liabilityLimit.blank}")
    String liabilityLimit;

    @JsonProperty("collision")
    boolean collision;

    @JsonProperty("comprehensive")
    boolean comprehensive;

    @JsonProperty("deductible")
    @NotNull
    @DecimalMin("0.0")
    BigDecimal deductible;
}
