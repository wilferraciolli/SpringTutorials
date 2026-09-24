package com.wiltech.insurly.account.phones;

import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPhoneAppService {

    private final UserPhoneRepository repository;
    private final UserPhoneAssembler assembler;
    private final Clock clock;

    @Transactional(readOnly = true)
    public UserPhoneResource createTemplate() {
        return UserPhoneResource.builder().label("Mobile").build();
    }

    @Transactional(readOnly = true)
    public List<UserPhoneResource> findForUser(final UUID ownerId) {
        return repository.findByUserIdOrderByCreatedAtDesc(ownerId).stream()
                .map(assembler::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserPhoneResource findForUser(final UUID ownerId, final UUID id) {
        return assembler.toDto(load(ownerId, id));
    }

    @Transactional
    public UserPhoneResource create(final UUID ownerId, @Valid final UserPhoneResource payload) {
        if (payload.isPrimary()) {
            demoteOtherPrimaries(ownerId, null);
        }
        return assembler.toDto(repository.save(assembler.toEntity(ownerId, payload)));
    }

    @Transactional
    public UserPhoneResource update(final UUID ownerId, final UUID id, @Valid final UserPhoneResource payload) {
        final UserPhone phone = load(ownerId, id);
        if (payload.isPrimary()) {
            demoteOtherPrimaries(ownerId, id);
        }
        phone.updateValues(payload.getLabel(), payload.getNumber().trim(), payload.isPrimary(), clock.instant());
        return assembler.toDto(repository.save(phone));
    }

    @Transactional
    public void delete(final UUID ownerId, final UUID id) {
        repository.delete(load(ownerId, id));
    }

    private void demoteOtherPrimaries(final UUID ownerId, final UUID keepId) {
        repository.findByUserIdAndPrimaryTrue(ownerId).stream()
                .filter(existing -> !existing.getId().equals(keepId))
                .forEach(existing -> {
                    existing.clearPrimary(clock.instant());
                    repository.save(existing);
                });
    }

    private UserPhone load(final UUID ownerId, final UUID id) {
        return repository.findByIdAndUserId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone %s not found".formatted(id)));
    }
}
