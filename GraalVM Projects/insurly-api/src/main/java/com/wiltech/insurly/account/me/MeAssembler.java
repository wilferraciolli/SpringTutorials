package com.wiltech.insurly.account.me;

import com.wiltech.insurly.account.addresses.UserAddressRestService;
import com.wiltech.insurly.account.cars.UserCarRestService;
import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.licenses.UserLicenseRestService;
import com.wiltech.insurly.account.phones.UserPhoneRestService;
import com.wiltech.insurly.account.quotes.UserQuoteRestService;
import com.wiltech.insurly.account.settings.UserSettingsRestService;
import com.wiltech.insurly.admin.profile.AdminProfileRestService;
import com.wiltech.insurly.libraries.rest.LinkBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeAssembler {

    private final LinkBuilder linkBuilder;

    public MeResource toDto(final AppUser user, final Set<String> roles) {
        final MeResource resource = MeResource.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .roles(roles)
                .build();

        final Map<String, Object> path = Map.of("userId", "me");
        final List<Link> links = new ArrayList<>();
        links.add(linkBuilder.buildSelfLink(MeRestService.class, "me"));
        links.add(linkBuilder.buildLink(MeRestService.class, "update", "updateProfile"));
        links.add(linkBuilder.buildLink(UserCarRestService.class, "findAll", "cars", path));
        links.add(linkBuilder.buildLink(UserAddressRestService.class, "findAll", "addresses", path));
        links.add(linkBuilder.buildLink(UserPhoneRestService.class, "findAll", "phones", path));
        links.add(linkBuilder.buildLink(UserLicenseRestService.class, "findAll", "licenses", path));
        links.add(linkBuilder.buildLink(UserQuoteRestService.class, "findAll", "quotes", path));
        links.add(linkBuilder.buildLink(UserQuoteRestService.class, "prefill", "quotePrefill", path));
        links.add(linkBuilder.buildLink(UserSettingsRestService.class, "get", "settings", path));

        // The one link that opens the admin area. Only admins get it, so the
        // profile is the single entry point the UI hardcodes — everything past
        // here (the admin profile, then providers) is reached by following the
        // link chain, never a URL the UI knows up front.
        if (roles.contains("ADMIN")) {
            links.add(linkBuilder.buildLink(AdminProfileRestService.class, "getProfile", "adminProfile"));
        }

        resource.addLinks(links);

        return resource;
    }
}
