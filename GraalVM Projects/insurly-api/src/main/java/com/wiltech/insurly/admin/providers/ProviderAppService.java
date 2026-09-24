package com.wiltech.insurly.admin.providers;

import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class ProviderAppService {
    private ProviderRepository repository;
    private ProviderAssembler assembler;

    @Transactional(readOnly = true)
    public ProviderResource createTemplate() {
        return ProviderResource.builder()
                .providerTypeId(ProviderType.CAR_INSURANCE)
                .build();
    }

    @Transactional
    public ProviderResource create(@Valid ProviderResource payload) {
        Provider resource = assembler.convertToEntity(payload);
        repository.save(resource);

        return assembler.convertToDTO(resource);
    }

    @Transactional(readOnly = true)
    public List<ProviderResource> findAll() {
        return findAll(null);
    }

    /** @param query optional case-insensitive "name contains" filter; null/blank returns all. */
    @Transactional(readOnly = true)
    public List<ProviderResource> findAll(final String query) {
        final String needle = query == null ? "" : query.trim().toLowerCase();
        return repository.findAll().stream()
                .filter(provider -> needle.isEmpty()
                        || (provider.getName() != null && provider.getName().toLowerCase().contains(needle)))
                .map(assembler::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProviderResource findById(final UUID id) {
        final Provider entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user entity for given id"));

        return assembler.convertToDTO(entity);
    }

    @Transactional
    public ProviderResource update(UUID id, @Valid ProviderResource payload) {
        final Provider entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find entity for given id"));

        entity.updateValues(
                payload.getName(),
                payload.getEmail(),
                payload.getPhoneNumber(),
                payload.getWebsite(),
                payload.getProviderTypeId()
        );
        repository.save(entity);

        return assembler.convertToDTO(entity);
    }

    @Transactional
    public void deleteById(UUID id) {
        final Provider entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user entity for given id"));

        repository.delete(entity);
    }
}
