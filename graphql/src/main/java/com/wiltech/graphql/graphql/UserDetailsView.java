package com.wiltech.graphql.graphql;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "user_details_v")
@Immutable // Tells JPA this is a view/read-only
public class UserDetailsView {
    @Id
    private Long id;
    private String username;
    private String email;
    private String roleNames;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleNames() {
        return roleNames;
    }
}