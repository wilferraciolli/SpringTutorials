package com.wiltech.insurly.account.quotes;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

/** Body of {@code POST /api/users/{userId}/quotes/claim} — proves ownership of a guest quote. */
@Value
@Builder
public class ClaimQuoteRequest {

    @JsonProperty("quoteId")
    @NotNull
    UUID quoteId;

    @JsonProperty("guestToken")
    @NotNull
    UUID guestToken;
}
