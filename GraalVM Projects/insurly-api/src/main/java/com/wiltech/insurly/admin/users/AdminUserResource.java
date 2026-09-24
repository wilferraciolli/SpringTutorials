package com.wiltech.insurly.admin.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.account.identity.UserOwnership;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * A customer as seen by the admin. {@code ownership} and {@code createdAt} are
 * read-only; {@code counts} are populated only on the single-resource GET.
 */
@JsonRootName("user")
@Value
@Builder
public class AdminUserResource extends BaseDTO {

    @JsonProperty("id")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID id;

    @JsonProperty("email")
    @NotEmpty(message = "{adminUser.email.blank}")
    @Email(message = "{adminUser.email.invalid}")
    String email;

    @JsonProperty("firstName")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String firstName;

    @JsonProperty("lastName")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String lastName;

    @JsonProperty("displayName")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String displayName;

    @JsonProperty("dateOfBirth")
    LocalDate dateOfBirth;

    @JsonProperty("ownership")
    UserOwnership ownership;

    /** App-owned roles (ADMIN, PROVIDER_ADMIN, SALES_ADMIN, ...) — admin-editable here. */
    @JsonProperty("roles")
    Set<String> roles;

    @JsonProperty("createdAt")
    Instant createdAt;

    @JsonProperty("carCount")
    Integer carCount;

    @JsonProperty("addressCount")
    Integer addressCount;

    @JsonProperty("phoneCount")
    Integer phoneCount;

    @JsonProperty("licenseCount")
    Integer licenseCount;

    @JsonProperty("quoteCount")
    Integer quoteCount;
}
