package com.wiltech.insurly.admin.users;

import com.wiltech.insurly.account.addresses.UserAddressRestService;
import com.wiltech.insurly.account.cars.UserCarRestService;
import com.wiltech.insurly.account.licenses.UserLicenseRestService;
import com.wiltech.insurly.account.phones.UserPhoneRestService;
import com.wiltech.insurly.account.quotes.UserQuoteRestService;
import com.wiltech.insurly.libraries.rest.LinkBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

/**
 * Links from an {@code AdminUserResource} — the customer's core profile
 * lives at {@code /api/admin/users/{id}}, but the sub-resource collections
 * (cars/addresses/phones/licenses/quotes) are the merged, self-or-admin
 * {@code /api/users/{userId}/**} endpoints (see {@code UserAccessService}).
 * An admin viewing this resource always has permission to write those, so
 * every link is unconditional.
 */
@Service
@RequiredArgsConstructor
public class AdminUserLinkProvider {

    private final LinkBuilder linkBuilder;

    public List<Link> forUser(final UUID id) {
        final List<Link> links = new ArrayList<>();
        if (id == null) {
            return links;
        }
        final Map<String, Object> path = Map.of("userId", id);

        links.add(linkBuilder.buildSelfLink(AdminUserRestService.class, "findById", Map.of("id", id)));
        links.add(linkBuilder.buildLink(AdminUserRestService.class, "update", "updateUser", Map.of("id", id)));
        links.add(linkBuilder.buildLink(AdminUserRestService.class, "deleteById", "deleteUser", Map.of("id", id)));
        links.add(linkBuilder.buildLink(UserCarRestService.class, "findAll", "cars", path));
        links.add(linkBuilder.buildLink(UserAddressRestService.class, "findAll", "addresses", path));
        links.add(linkBuilder.buildLink(UserPhoneRestService.class, "findAll", "phones", path));
        links.add(linkBuilder.buildLink(UserLicenseRestService.class, "findAll", "licenses", path));
        links.add(linkBuilder.buildLink(UserQuoteRestService.class, "findAll", "quotes", path));
        links.add(linkBuilder.buildLink(UserQuoteRestService.class, "prefill", "quotePrefill", path));
        links.add(linkBuilder.buildLink(UserQuoteRestService.class, "create", "createQuote", path));
        return links;
    }
}
