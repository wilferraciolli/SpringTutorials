package com.wiltech.insurly.account.settings;

import com.wiltech.insurly.libraries.rest.LinkBuilder;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsLinkProvider {

    private final LinkBuilder linkBuilder;

    public Link generateSelfLink(final UUID userId) {
        return linkBuilder.buildSelfLink(UserSettingsRestService.class, "get", pathParams(userId));
    }

    /** Always present — a user may always change their own overrides. */
    public Link generateUpdateLink(final UUID userId) {
        return linkBuilder.buildLink(UserSettingsRestService.class, "update", "updateUserSettings", pathParams(userId));
    }

    private static Map<String, Object> pathParams(final UUID userId) {
        return Map.of("userId", userId);
    }
}
