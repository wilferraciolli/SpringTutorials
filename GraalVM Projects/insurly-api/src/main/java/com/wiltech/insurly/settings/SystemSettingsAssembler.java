package com.wiltech.insurly.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemSettingsAssembler {
    private final SystemSettingsLinkProvider linkProvider;

    public SystemSettingsResource convertToDTO(final SystemSettings entity, final boolean includeAdminLinks) {
        final SystemSettingsResource resource = SystemSettingsResource.builder()
                .timezone(entity.getTimezone())
                .language(entity.getLanguage())
                .currency(entity.getCurrency())
                .updatedAt(entity.getUpdatedAt())
                .build();

        final List<Link> links = new ArrayList<>();
        links.add(linkProvider.generateSelfLink());
        if (includeAdminLinks) {
            links.add(linkProvider.generateUpdateLink());
        }
        resource.addLinks(links);

        return resource;
    }
}
