package com.wiltech.insurly.account.quotes;

import com.wiltech.insurly.libraries.rest.LinkBuilder;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQuoteLinkProvider {

    private final LinkBuilder linkBuilder;

    public Link generateSelfLink(final UUID ownerId, final UUID id) {
        return id == null ? null
                : linkBuilder.buildSelfLink(UserQuoteRestService.class, "findById", Map.of("userId", ownerId, "id", id));
    }
}
