package com.wiltech.authserver.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wiltech.authserver.config.Glee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {

    public enum Role {USER, ADMIN, USER_MANAGER}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Email
    private String email;

    @JsonIgnore
    @ToString.Exclude
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    private Double minGleePerDay;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private Collection<Glee> glee;
}
