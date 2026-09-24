package com.wiltech.insurly.account.phones;

import java.util.UUID;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPhoneAssembler {

    private final UserPhoneLinkProvider linkProvider;
    private final Clock clock;

    public UserPhone toEntity(final UUID ownerId, final UserPhoneResource payload) {
        return UserPhone.builder()
                .userId(ownerId)
                .label(payload.getLabel())
                .number(payload.getNumber().trim())
                .primary(payload.isPrimary())
                .createdAt(clock.instant())
                .updatedAt(clock.instant())
                .build();
    }

    public UserPhoneResource toDto(final UserPhone entity) {
        final UserPhoneResource resource = UserPhoneResource.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .number(entity.getNumber())
                .primary(entity.isPrimary())
                .build();

        final List<Link> links = new ArrayList<>();
        links.add(linkProvider.generateSelfLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateUpdateLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateDeleteLink(entity.getUserId(), entity.getId()));
        resource.addLinks(links);

        return resource;
    }
}
