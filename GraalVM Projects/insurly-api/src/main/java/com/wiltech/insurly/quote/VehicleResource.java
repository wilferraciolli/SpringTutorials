package com.wiltech.insurly.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VehicleResource {
    @JsonProperty("make")
    @NotEmpty(message = "{quote.vehicle.make.blank}")
    String make;

    @JsonProperty("model")
    @NotEmpty(message = "{quote.vehicle.model.blank}")
    String model;

    @JsonProperty("year")
    @NotNull
    @Min(1900)
    Integer year;

    @JsonProperty("primaryUse")
    @NotNull
    PrimaryUse primaryUse;
}
