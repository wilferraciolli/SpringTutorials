package com.wiltech.insurly.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import com.wiltech.insurly.validations.ValidZoneId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

@JsonRootName("systemSettings")
@Value
@Builder
public class SystemSettingsResource extends BaseDTO {

    @JsonProperty("timezone")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    @NotBlank(message = "{systemSettings.timezone.blank}")
    @ValidZoneId(message = "{systemSettings.timezone.invalid}")
    String timezone;

    @JsonProperty("language")
    @NotNull(message = "{systemSettings.language.blank}")
    SupportedLanguage language;

    @JsonProperty("currency")
    @NotNull(message = "{systemSettings.currency.blank}")
    SupportedCurrency currency;

    @JsonProperty("updatedAt")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    Instant updatedAt;
}
