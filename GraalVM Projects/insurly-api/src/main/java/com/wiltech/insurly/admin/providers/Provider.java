package com.wiltech.insurly.admin.providers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "provider")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Provider implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotEmpty
    String name;

    @NotEmpty
    String email;

    String phoneNumber;

    String website;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    public void updateValues(
            String name,
            String email,
            String phoneNumber,
            String website,
            ProviderType providerTypeId) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.providerType = providerTypeId;
    }
}
