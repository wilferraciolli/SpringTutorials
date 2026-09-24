package com.wiltech.insurly.account.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import com.wiltech.insurly.settings.SupportedCurrency;
import com.wiltech.insurly.settings.SupportedLanguage;
import com.wiltech.insurly.validations.ValidZoneId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

/**
 * A user's personal display settings. Always fully populated: values the user
 * hasn't saved come back filled in from {@code systemSettings} (see
 * {@link UserSettingsAssembler}), and an update must supply all three.
 */
@JsonRootName("userSettings")
@Value
@Builder
public class UserSettingsResource extends BaseDTO {

    @JsonProperty("timezone")
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
