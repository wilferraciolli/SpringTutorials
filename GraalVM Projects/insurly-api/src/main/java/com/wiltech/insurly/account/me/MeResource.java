package com.wiltech.insurly.account.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * The signed-in user's own profile. {@code email} and {@code roles} are
 * provider-owned (read-only here); {@code displayName} / {@code firstName} /
 * {@code lastName} / {@code dateOfBirth} are user-editable via {@code PUT /api/me}.
 * Links point at the account sub-collections.
 */
@JsonRootName("me")
@Value
@Builder
public class MeResource extends BaseDTO {

    @JsonProperty("id")
    UUID id;

    @JsonProperty("email")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String email;

    @JsonProperty("displayName")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String displayName;

    @JsonProperty("firstName")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String firstName;

    @JsonProperty("lastName")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String lastName;

    @JsonProperty("dateOfBirth")
    LocalDate dateOfBirth;

    @JsonProperty("roles")
    Set<String> roles;
}
