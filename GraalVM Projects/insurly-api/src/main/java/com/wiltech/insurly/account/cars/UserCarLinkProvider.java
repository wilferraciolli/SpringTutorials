package com.wiltech.insurly.account.cars;

import com.wiltech.insurly.libraries.rest.LinkBuilder;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCarLinkProvider {

    private final LinkBuilder linkBuilder;

    public Link generateSelfLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildSelfLink(UserCarRestService.class, "findById", pathParams(ownerId, id));
    }

    public Link generateUpdateLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildLink(UserCarRestService.class, "update", "updateCar", pathParams(ownerId, id));
    }

    public Link generateDeleteLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildLink(UserCarRestService.class, "deleteById", "deleteCar", pathParams(ownerId, id));
    }

    private static Map<String, Object> pathParams(final UUID ownerId, final UUID id) {
        return Map.of("userId", ownerId, "id", id);
    }
}
