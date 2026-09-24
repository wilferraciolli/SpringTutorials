package com.wiltech.insurly.account.addresses;

import java.util.UUID;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAddressAssembler {

    private final UserAddressLinkProvider linkProvider;
    private final Clock clock;

    public UserAddress toEntity(final UUID ownerId, final UserAddressResource payload) {
        return UserAddress.builder()
                .userId(ownerId)
                .label(payload.getLabel())
                .line1(payload.getLine1())
                .line2(payload.getLine2())
                .city(payload.getCity())
                .region(payload.getRegion())
                .postalCode(payload.getPostalCode())
                .countryCode(upper(payload.getCountryCode()))
                .primary(payload.isPrimary())
                .createdAt(clock.instant())
                .updatedAt(clock.instant())
                .build();
    }

    public UserAddressResource toDto(final UserAddress entity) {
        final UserAddressResource resource = UserAddressResource.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .line1(entity.getLine1())
                .line2(entity.getLine2())
                .city(entity.getCity())
                .region(entity.getRegion())
                .postalCode(entity.getPostalCode())
                .countryCode(entity.getCountryCode())
                .primary(entity.isPrimary())
                .build();

        final List<Link> links = new ArrayList<>();
        links.add(linkProvider.generateSelfLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateUpdateLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateDeleteLink(entity.getUserId(), entity.getId()));
        resource.addLinks(links);

        return resource;
    }

    private static String upper(final String value) {
        return value == null ? null : value.toUpperCase();
    }
}
