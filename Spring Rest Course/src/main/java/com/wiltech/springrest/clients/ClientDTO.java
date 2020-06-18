package com.wiltech.springrest.clients;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.wiltech.springrest.validation.ValidateUniqueEmail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientDTO {

    private Long id;

    @NotBlank
    @Size(max = 60)
    private String name;

    @ValidateUniqueEmail(allowBlanks = false, message = "Email is already taken")
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String telephone;
}
