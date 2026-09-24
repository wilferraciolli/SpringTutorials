package com.wiltech.insurly.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * One resource for both request and response. The wizard input (driver/vehicle/
 * history/coverage) is supplied by the caller; the remaining fields are computed
 * server-side and flagged read-only via {@code _metadata} (see QuoteMetaFabricator).
 */
@JsonRootName("quote")
@Value
@Builder
public class QuoteResource extends BaseDTO {
    @JsonProperty("id")
    UUID id;

    @JsonProperty("guestToken")
    UUID guestToken;

    @JsonProperty("status")
    QuoteStatus status;

    @JsonProperty("driver")
    @NotNull
    @Valid
    DriverResource driver;

    @JsonProperty("vehicle")
    @NotNull
    @Valid
    VehicleResource vehicle;

    @JsonProperty("history")
    @NotNull
    @Valid
    DrivingHistoryResource history;

    @JsonProperty("coverage")
    @NotNull
    @Valid
    CoverageResource coverage;

    @JsonProperty("premiumBasic")
    BigDecimal premiumBasic;

    @JsonProperty("premiumStandard")
    BigDecimal premiumStandard;

    @JsonProperty("premiumPremium")
    BigDecimal premiumPremium;

    @JsonProperty("createdAt")
    Instant createdAt;

    @JsonProperty("expiresAt")
    Instant expiresAt;
}
