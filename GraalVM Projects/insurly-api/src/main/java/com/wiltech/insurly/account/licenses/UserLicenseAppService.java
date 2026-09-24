package com.wiltech.insurly.account.licenses;

import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import com.wiltech.insurly.quote.LicenseStatus;
import jakarta.validation.Valid;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLicenseAppService {

    private final UserLicenseRepository repository;
    private final UserLicenseAssembler assembler;
    private final Clock clock;

    @Transactional(readOnly = true)
    public UserLicenseResource createTemplate() {
        return UserLicenseResource.builder().status(LicenseStatus.VALID).build();
    }

    @Transactional(readOnly = true)
    public List<UserLicenseResource> findForUser(final UUID ownerId) {
        return repository.findByUserIdOrderByCreatedAtDesc(ownerId).stream()
                .map(license -> assembler.toDto(license, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserLicenseResource findForUser(final UUID ownerId, final UUID id) {
        return assembler.toDto(load(ownerId, id), false);
    }

    @Transactional
    public UserLicenseResource create(final UUID ownerId, @Valid final UserLicenseResource payload) {
        return assembler.toDto(repository.save(assembler.toEntity(ownerId, payload)), true);
    }

    @Transactional
    public UserLicenseResource update(final UUID ownerId, final UUID id, @Valid final UserLicenseResource payload) {
        final UserLicense license = load(ownerId, id);
        license.updateValues(
                payload.getLicenseNumber().trim(),
                payload.getStatus(),
                payload.getIssuingRegion(),
                payload.getIssuedOn(),
                payload.getExpiresOn(),
                payload.getYearsHeld(),
                clock.instant());
        return assembler.toDto(repository.save(license), true);
    }

    @Transactional
    public void delete(final UUID ownerId, final UUID id) {
        repository.delete(load(ownerId, id));
    }

    private UserLicense load(final UUID ownerId, final UUID id) {
        return repository.findByIdAndUserId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Licence %s not found".formatted(id)));
    }
}
