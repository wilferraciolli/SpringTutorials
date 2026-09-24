package com.wiltech.insurly.admin.providers;


import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderAssembler {
    private final ProviderLinkProvider linkProvider;

    public Provider convertToEntity(ProviderResource payload) {
        return Provider.builder()
                .name(payload.getName())
                .email(payload.getEmail())
                .phoneNumber(payload.getPhoneNumber())
                .website(payload.getWebsite())
                .providerType(payload.getProviderTypeId())
                .build();
    }

    public ProviderResource convertToDTO(Provider entity) {
        ProviderResource resource = ProviderResource.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .website(entity.getWebsite())
                .providerTypeId(entity.getProviderType())
                .build();

        List<Link> linksToAdd = new ArrayList<>();
        linksToAdd.add(linkProvider.generateSelfLink(resource.getId()));
        linksToAdd.add(linkProvider.generateUpdateLink(resource.getId()));
        linksToAdd.add(linkProvider.generateDeleteLink(resource.getId()));

        resource.addLinks(linksToAdd);

        return resource;
    }
}
