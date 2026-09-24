package com.wiltech.insurly.admin.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonRootName("adminProfile")
@Value
@Builder
public class AdminProfileResource extends BaseDTO {
    @JsonProperty("userId")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID userId;

    @JsonProperty("firstName")
    @NotEmpty(message = "{adminProfile.firstName.blank}")
    String firstName;

    @JsonProperty("lastName")
    @NotEmpty(message = "{adminProfile.lastName.blank}")
    String lastName;

    @JsonProperty("email")
    @NotEmpty(message = "{adminProfile.email.blank}")
    String email;
}
