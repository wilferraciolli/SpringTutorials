package com.wiltech.insurly.account.settings;

import com.wiltech.insurly.settings.SystemSettingsAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

/**
 * Reads and updates a user's personal display settings — the caller's own,
 * or (for an admin) another user's, per {@code userId} resolved by
 * {@link UserSettingsRestService}. The response is never empty: any value the
 * user hasn't saved (no row yet, or a null column) is filled in from the
 * platform's system settings, so a user who has never saved keeps following
 * whatever the admin picks.
 */
@Service
@RequiredArgsConstructor
public class UserSettingsAppService {

    private final UserSettingsRepository repository;
    private final UserSettingsAssembler assembler;
    private final SystemSettingsAppService systemSettings;
    private final Clock clock;

    @Transactional(readOnly = true)
    public UserSettingsResource getSettings(final UUID userId) {
        final UserSettings entity = repository.findById(userId)
                .orElseGet(() -> UserSettings.empty(userId, clock.instant()));
        return assembler.convertToDTO(entity, systemSettings.getSettings(false));
    }

    @Transactional
    public UserSettingsResource updateSettings(final UUID userId, @Valid final UserSettingsResource payload) {
        final UserSettings entity = repository.findById(userId)
                .orElseGet(() -> UserSettings.empty(userId, clock.instant()));

        entity.update(
                payload.getTimezone().trim(),
                payload.getLanguage(),
                payload.getCurrency(),
                clock.instant());

        return assembler.convertToDTO(repository.save(entity), systemSettings.getSettings(false));
    }
}
