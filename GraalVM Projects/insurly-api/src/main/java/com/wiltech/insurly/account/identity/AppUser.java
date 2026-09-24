package com.wiltech.insurly.account.identity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Local mirror of the OIDC provider's user. Holds no credentials. Created
 * just-in-time on the first authenticated request for a given subject.
 */
@Entity
@Table(name = "app_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AppUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Null for MANAGED (admin-created) users — they have no login. */
    @Column(name = "auth_subject", updatable = false, unique = true)
    private String authSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership", nullable = false)
    private UserOwnership ownership;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name")
    private String displayName;

    /** User-editable profile fields (not sourced from the identity provider). */
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * App-owned roles (ADMIN, PROVIDER_ADMIN, SALES_ADMIN, ...) — see
     * {@code V7__user_roles.sql}. Source of truth once populated; bootstrapped
     * from the identity provider's roles claim on first sight by
     * {@code CurrentUserService}, admin-editable thereafter.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    public boolean hasRole(final String role) {
        return roles.contains(role.toUpperCase());
    }

    public void replaceRoles(final Set<String> newRoles) {
        roles.clear();
        newRoles.stream().filter(Objects::nonNull).map(String::toUpperCase).forEach(roles::add);
    }

    /** Keep the local mirror in step with what the provider now reports. */
    public void syncFromProvider(final String email, final String displayName, final Instant now) {
        this.email = email;
        this.displayName = displayName;
        this.updatedAt = now;
    }

    /** Apply the fields the user edits themselves via {@code PUT /api/me}. */
    public void editProfile(
            final String displayName,
            final String firstName,
            final String lastName,
            final LocalDate dateOfBirth,
            final Instant now) {
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.updatedAt = now;
    }

    /** Admin edit of any user (self-service or MANAGED) — the admin also owns the email. */
    public void adminEdit(
            final String email,
            final String displayName,
            final String firstName,
            final String lastName,
            final LocalDate dateOfBirth,
            final Instant now) {
        this.email = email;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.updatedAt = now;
    }
}
