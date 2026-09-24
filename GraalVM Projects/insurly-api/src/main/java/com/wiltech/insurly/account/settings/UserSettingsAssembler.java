package com.wiltech.insurly.account.settings;

import com.wiltech.insurly.settings.SystemSettingsResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.requireNonNullElse;

@Service
@RequiredArgsConstructor
public class UserSettingsAssembler {

    private final UserSettingsLinkProvider linkProvider;

    /** Any value the user hasn't saved is filled in from {@code platform}. */
    public UserSettingsResource convertToDTO(final UserSettings entity, final SystemSettingsResource platform) {
        final UserSettingsResource resource = UserSettingsResource.builder()
                .timezone(requireNonNullElse(entity.getTimezone(), platform.getTimezone()))
                .language(requireNonNullElse(entity.getLanguage(), platform.getLanguage()))
                .currency(requireNonNullElse(entity.getCurrency(), platform.getCurrency()))
                .updatedAt(entity.getUpdatedAt())
                .build();

        resource.addLinks(List.of(
                linkProvider.generateSelfLink(entity.getUserId()),
                linkProvider.generateUpdateLink(entity.getUserId())));

        return resource;
    }
}
