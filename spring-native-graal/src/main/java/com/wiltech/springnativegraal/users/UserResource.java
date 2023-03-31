package com.wiltech.springnativegraal.users;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class UserResource {

    private Long id;

    //    @NotNull
//    @Size(max = 80, message = "First name cannot have more than {max} characters")
    private String firstName;

    //    @NotNull
//    @Size(max = 80, message = "Last name cannot have more than {max} characters")
    private String lastName;

    //    @NotNull(message = "User name cannot be null.")
//    @ValidateUniqueUsername(message = "{Users.username.NonUnique}")
    private String username;


    //    @NotNull(message = "Password cannot be null.")
    private String password;

    private LocalDate dateOfBirth;

    //    @NotNull(message = "Active cannot be null.")
    private Boolean active;

    //    @NotEmpty(message = "At least 1 role must be added to the user.")
    private List<String> roleIds;
}
