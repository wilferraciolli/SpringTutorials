package com.wiltech.insurly.account.phones;

import com.wiltech.insurly.libraries.rest.LinkBuilder;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPhoneLinkProvider {

    private final LinkBuilder linkBuilder;

    public Link generateSelfLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildSelfLink(UserPhoneRestService.class, "findById", pathParams(ownerId, id));
    }

    public Link generateUpdateLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildLink(UserPhoneRestService.class, "update", "updatePhone", pathParams(ownerId, id));
    }

    public Link generateDeleteLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildLink(UserPhoneRestService.class, "deleteById", "deletePhone", pathParams(ownerId, id));
    }

    private static Map<String, Object> pathParams(final UUID ownerId, final UUID id) {
        return Map.of("userId", ownerId, "id", id);
    }
}
