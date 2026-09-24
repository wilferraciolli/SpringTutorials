package com.wiltech.insurly.account.addresses;

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
public class UserAddressAppService {

    private final UserAddressRepository repository;
    private final UserAddressAssembler assembler;
    private final Clock clock;

    @Transactional(readOnly = true)
    public UserAddressResource createTemplate() {
        return UserAddressResource.builder().countryCode("GB").build();
    }

    @Transactional(readOnly = true)
    public List<UserAddressResource> findForUser(final UUID ownerId) {
        return repository.findByUserIdOrderByCreatedAtDesc(ownerId).stream()
                .map(assembler::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAddressResource findForUser(final UUID ownerId, final UUID id) {
        return assembler.toDto(load(ownerId, id));
    }

    @Transactional
    public UserAddressResource create(final UUID ownerId, @Valid final UserAddressResource payload) {
        if (payload.isPrimary()) {
            demoteOtherPrimaries(ownerId, null);
        }
        return assembler.toDto(repository.save(assembler.toEntity(ownerId, payload)));
    }

    @Transactional
    public UserAddressResource update(final UUID ownerId, final UUID id, @Valid final UserAddressResource payload) {
        final UserAddress address = load(ownerId, id);
        if (payload.isPrimary()) {
            demoteOtherPrimaries(ownerId, id);
        }
        address.updateValues(
                payload.getLabel(),
                payload.getLine1(),
                payload.getLine2(),
                payload.getCity(),
                payload.getRegion(),
                payload.getPostalCode(),
                payload.getCountryCode() == null ? null : payload.getCountryCode().toUpperCase(),
                payload.isPrimary(),
                clock.instant());
        return assembler.toDto(repository.save(address));
    }

    @Transactional
    public void delete(final UUID ownerId, final UUID id) {
        repository.delete(load(ownerId, id));
    }

    /** At most one address per user is primary. */
    private void demoteOtherPrimaries(final UUID ownerId, final UUID keepId) {
        repository.findByUserIdAndPrimaryTrue(ownerId).stream()
                .filter(existing -> !existing.getId().equals(keepId))
                .forEach(existing -> {
                    existing.clearPrimary(clock.instant());
                    repository.save(existing);
                });
    }

    private UserAddress load(final UUID ownerId, final UUID id) {
        return repository.findByIdAndUserId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address %s not found".formatted(id)));
    }
}
