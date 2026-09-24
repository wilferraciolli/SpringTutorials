package com.wiltech.insurly.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DrivingHistoryResource {
    @JsonProperty("accidentsLast5Years")
    @NotNull
    @PositiveOrZero
    Integer accidentsLast5Years;

    @JsonProperty("violationsLast5Years")
    @NotNull
    @PositiveOrZero
    Integer violationsLast5Years;
}
