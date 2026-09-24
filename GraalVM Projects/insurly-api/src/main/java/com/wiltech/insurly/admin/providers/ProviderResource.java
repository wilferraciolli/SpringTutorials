package com.wiltech.insurly.admin.providers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonRootName("provider")
@Value
@Builder
public class ProviderResource extends BaseDTO {
    @JsonProperty("id")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID id;

    @JsonProperty("name")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    @NotEmpty(message = "{provider.name.blank}")
    String name;

    @JsonProperty("email")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    @NotEmpty(message = "{provider.email.blank}")
    String email;

    @JsonProperty("phoneNumber")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String phoneNumber;

    @JsonProperty("website")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String website;

    @JsonProperty("providerTypeId")
    @NotNull
    ProviderType providerTypeId;
}
