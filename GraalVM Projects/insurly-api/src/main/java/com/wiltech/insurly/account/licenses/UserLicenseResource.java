package com.wiltech.insurly.account.licenses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import com.wiltech.insurly.quote.LicenseStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * {@code licenseNumber} is write-full / read-masked: the collection and update
 * responses carry a masked value ({@code ••••• 1234}); the single-resource GET
 * returns it in full.
 */
@JsonRootName("license")
@Value
@Builder
public class UserLicenseResource extends BaseDTO {

    @JsonProperty("id")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID id;

    @JsonProperty("licenseNumber")
    @NotEmpty(message = "{userLicense.licenseNumber.blank}")
    String licenseNumber;

    @JsonProperty("status")
    @NotNull
    LicenseStatus status;

    @JsonProperty("issuingRegion")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String issuingRegion;

    @JsonProperty("issuedOn")
    LocalDate issuedOn;

    @JsonProperty("expiresOn")
    LocalDate expiresOn;

    @JsonProperty("yearsHeld")
    @PositiveOrZero
    Integer yearsHeld;
}
