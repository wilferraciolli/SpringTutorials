package com.wiltech.insurly.account.cars;

import java.util.UUID;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCarAssembler {

    private final UserCarLinkProvider linkProvider;
    private final Clock clock;

    public UserCar toEntity(final UUID ownerId, final UserCarResource payload) {
        return UserCar.builder()
                .userId(ownerId)
                .make(payload.getMake())
                .model(payload.getModel())
                .year(payload.getYear())
                .vin(payload.getVin())
                .primaryUse(payload.getPrimaryUse())
                .nickname(payload.getNickname())
                .createdAt(clock.instant())
                .updatedAt(clock.instant())
                .build();
    }

    public UserCarResource toDto(final UserCar entity) {
        final UserCarResource resource = UserCarResource.builder()
                .id(entity.getId())
                .make(entity.getMake())
                .model(entity.getModel())
                .year(entity.getYear())
                .vin(entity.getVin())
                .primaryUse(entity.getPrimaryUse())
                .nickname(entity.getNickname())
                .build();

        final List<Link> links = new ArrayList<>();
        links.add(linkProvider.generateSelfLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateUpdateLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateDeleteLink(entity.getUserId(), entity.getId()));
        resource.addLinks(links);

        return resource;
    }
}
