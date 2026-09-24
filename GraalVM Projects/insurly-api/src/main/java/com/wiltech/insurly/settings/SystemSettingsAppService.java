package com.wiltech.insurly.settings;

import com.wiltech.insurly.admin.access.AdminAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * Reads and updates the single global {@link SystemSettings} row. Reads are
 * public (the frontend formats amounts / dates with these); the update is
 * gated on the ADMIN role.
 */
@Service
@RequiredArgsConstructor
public class SystemSettingsAppService {

    private final SystemSettingsRepository repository;
    private final SystemSettingsAssembler assembler;
    private final AdminAccessService adminAccess;
    private final Clock clock;

    @Transactional(readOnly = true)
    public SystemSettingsResource getSettings(final boolean includeAdminLinks) {
        final SystemSettings entity = repository.findSingleton().orElseGet(this::transientDefault);
        return assembler.convertToDTO(entity, includeAdminLinks);
    }

    /** The configured display currency, for formatting amounts outside the REST layer (e.g. emails). */
    @Transactional(readOnly = true)
    public SupportedCurrency currentCurrency() {
        return repository.findSingleton()
                .map(SystemSettings::getCurrency)
                .orElse(SystemSettingsDefaults.CURRENCY);
    }

    @Transactional
    public SystemSettingsResource updateSettings(@Valid final SystemSettingsResource payload) {
        adminAccess.requireAdmin();

        final SystemSettings entity = repository.findSingleton().orElseGet(this::transientDefault);
        entity.update(payload.getTimezone(), payload.getLanguage(), payload.getCurrency(), clock.instant());

        return assembler.convertToDTO(repository.save(entity), true);
    }

    /**
     * Fallback used only if the Flyway seed row is somehow absent, so a read
     * never 500s. Not persisted here — an {@code updateSettings} call saves it.
     */
    private SystemSettings transientDefault() {
        return SystemSettings.builder()
                .timezone(SystemSettingsDefaults.TIMEZONE)
                .language(SystemSettingsDefaults.LANGUAGE)
                .currency(SystemSettingsDefaults.CURRENCY)
                .updatedAt(clock.instant())
                .build();
    }
}
