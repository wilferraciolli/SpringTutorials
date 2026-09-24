package com.wiltech.insurly.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class DriverResource {
    @JsonProperty("firstName")
    @NotEmpty(message = "{quote.driver.firstName.blank}")
    String firstName;

    @JsonProperty("lastName")
    @NotEmpty(message = "{quote.driver.lastName.blank}")
    String lastName;

    @JsonProperty("dateOfBirth")
    @NotNull
    @Past
    LocalDate dateOfBirth;

    @JsonProperty("yearsLicensed")
    @NotNull
    @PositiveOrZero
    Integer yearsLicensed;

    @JsonProperty("licenseStatus")
    @NotNull
    LicenseStatus licenseStatus;
}
