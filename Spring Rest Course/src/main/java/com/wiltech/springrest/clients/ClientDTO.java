package com.wiltech.springrest.clients;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "{Client.name.Blank}")
    @Size(min = 2, max = 60, message = "{Client.name.Size}")
    private String name;

    @ValidateUniqueEmail(message = "{Client.email.NonUnique}")
    @NotNull(message = "{Client.email.Blank}")
    @Email(message = "{Client.email.Invalid}")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "{Client.telephone.size}")
    @Size(max = 20, message = "{Client.telephone.Size}")
    private String telephone;
}
