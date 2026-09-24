package com.wiltech.insurly.admin.profile;

import com.wiltech.insurly.admin.dashboard.AdminDashboardRestService;
import com.wiltech.insurly.admin.providers.ProviderRestService;
import com.wiltech.insurly.admin.users.AdminUserRestService;
import com.wiltech.insurly.libraries.rest.LinkBuilder;
import com.wiltech.insurly.settings.AdminSystemSettingsRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The admin profile is the hub of the whole admin area: the UI reaches it by
 * following the {@code adminProfile} link on {@code GET /api/users/me} (emitted
 * for admins only), and every admin screen — dashboard, customers, providers,
 * settings — is then reached by following a link from here, never a URL the UI
 * knows up front. Template links ({@code createUser}, {@code createProvider})
 * point at the {@code /template} endpoints.
 */
@Service
@RequiredArgsConstructor
public class AdminProfileAssembler {
    private final LinkBuilder linkBuilder;

    public AdminProfileResource convertToDTO(final AdminProfile entity) {
        final AdminProfileResource resource = AdminProfileResource.builder()
                .userId(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .build();

        final List<Link> linksToAdd = new ArrayList<>();
        linksToAdd.add(linkBuilder.buildSelfLink(AdminProfileRestService.class, "getProfile"));
        linksToAdd.add(linkBuilder.buildLink(AdminProfileRestService.class, "update", "updateProfile"));

        // Every admin area hangs off this resource. Reaching /admin/profile
        // already requires the ADMIN role (AdminGuardInterceptor), and the
        // adminProfile link that leads here is admin-only, so an unconditional
        // add is correct until a finer-grained permission model exists.
        linksToAdd.add(linkBuilder.buildLink(AdminDashboardRestService.class, "dashboard", "dashboard"));
        linksToAdd.add(linkBuilder.buildLink(AdminUserRestService.class, "findAll", "users"));
        linksToAdd.add(linkBuilder.buildLink(AdminUserRestService.class, "template", "createUser"));
        linksToAdd.add(linkBuilder.buildLink(ProviderRestService.class, "findAll", "providers"));
        linksToAdd.add(linkBuilder.buildLink(ProviderRestService.class, "template", "createProvider"));
        linksToAdd.add(linkBuilder.buildLink(AdminSystemSettingsRestService.class, "get", "settings"));
        linksToAdd.add(linkBuilder.buildLink(AdminSystemSettingsRestService.class, "update", "updateSettings"));

        resource.addLinks(linksToAdd);

        return resource;
    }
}
