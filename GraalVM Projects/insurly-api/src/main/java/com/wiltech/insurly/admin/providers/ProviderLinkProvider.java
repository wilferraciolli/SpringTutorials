package com.wiltech.insurly.admin.providers;


import com.wiltech.insurly.libraries.rest.LinkBuilder;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderLinkProvider {
    private final LinkBuilder linkBuilder;

    public Link generateSelfLink(final UUID id) {
        if (Objects.nonNull(id)) {
            return linkBuilder.buildSelfLink(
                    ProviderRestService.class,
                    "findById",
                    Map.of("id", id));
        }

        return null;
    }

    public Link generateUpdateLink(final UUID id) {
        if (Objects.nonNull(id)) {
            return linkBuilder.buildLink(
                    ProviderRestService.class,
                    "update",
                    "updateProvider",
                    Map.of("id", id));
        }

        return null;
    }

    public Link generateDeleteLink(final UUID id) {
        if (Objects.nonNull(id)) {
            return linkBuilder.buildLink(
                    ProviderRestService.class,
                    "deleteById",
                    "deleteProvider",
                    Map.of("id", id));
        }

        return null;
    }
}
