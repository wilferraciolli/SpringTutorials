package com.wiltech.insurly.account.addresses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonRootName("address")
@Value
@Builder
public class UserAddressResource extends BaseDTO {

    @JsonProperty("id")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID id;

    @JsonProperty("label")
    @NotEmpty(message = "{userAddress.label.blank}")
    String label;

    @JsonProperty("line1")
    @NotEmpty(message = "{userAddress.line1.blank}")
    String line1;

    @JsonProperty("line2")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String line2;

    @JsonProperty("city")
    @NotEmpty(message = "{userAddress.city.blank}")
    String city;

    @JsonProperty("region")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String region;

    @JsonProperty("postalCode")
    @NotEmpty(message = "{userAddress.postalCode.blank}")
    String postalCode;

    @JsonProperty("countryCode")
    @NotEmpty(message = "{userAddress.countryCode.blank}")
    @Size(min = 2, max = 2, message = "{userAddress.countryCode.size}")
    String countryCode;

    @JsonProperty("primary")
    boolean primary;
}
