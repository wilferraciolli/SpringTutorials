package com.wiltech.insurly.account.phones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonRootName("phone")
@Value
@Builder
public class UserPhoneResource extends BaseDTO {

    @JsonProperty("id")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID id;

    @JsonProperty("label")
    @NotEmpty(message = "{userPhone.label.blank}")
    String label;

    @JsonProperty("number")
    @NotEmpty(message = "{userPhone.number.blank}")
    @Pattern(regexp = "^[+()\\-\\s\\d]{6,32}$", message = "{userPhone.number.invalid}")
    String number;

    @JsonProperty("primary")
    boolean primary;
}
