package com.wiltech.insurly.admin.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Placeholder admin profile: no persistence yet, {@link #profile} is a
 * single dummy user held in memory for the app's lifetime. Its only real
 * purpose right now is to be the UI's single hardcoded entry point — every
 * other admin area (e.g. providers) is reached by following a link this
 * profile hands back, not a URL the UI already knows.
 */
@Service
@RequiredArgsConstructor
public class AdminProfileAppService {
    private final AdminProfileAssembler assembler;

    private final AdminProfile profile = AdminProfile.builder()
            .id(UUID.randomUUID())
            .firstName("Jon")
            .lastName("Doe")
            .email("jon.doe@insurly.example.com")
            .build();

    public AdminProfileResource getProfile() {
        return assembler.convertToDTO(profile);
    }

    public AdminProfileResource update(@Valid final AdminProfileResource payload) {
        profile.updateValues(payload.getFirstName(), payload.getLastName(), payload.getEmail());

        return assembler.convertToDTO(profile);
    }
}
