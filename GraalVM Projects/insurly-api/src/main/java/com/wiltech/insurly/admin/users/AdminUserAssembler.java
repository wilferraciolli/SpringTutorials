package com.wiltech.insurly.admin.users;

import com.wiltech.insurly.account.identity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserAssembler {

    private final AdminUserLinkProvider linkProvider;

    /** List row — no per-user counts. */
    public AdminUserResource toSummary(final AppUser user) {
        return withLinks(user, base(user).build());
    }

    /** Single-resource — with sub-collection counts. */
    public AdminUserResource toDetail(final AppUser user, final Counts counts) {
        return withLinks(user, base(user)
                .carCount(counts.cars())
                .addressCount(counts.addresses())
                .phoneCount(counts.phones())
                .licenseCount(counts.licenses())
                .quoteCount(counts.quotes())
                .build());
    }

    private AdminUserResource withLinks(final AppUser user, final AdminUserResource resource) {
        resource.addLinks(linkProvider.forUser(user.getId()));
        return resource;
    }

    private AdminUserResource.AdminUserResourceBuilder base(final AppUser user) {
        return AdminUserResource.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .dateOfBirth(user.getDateOfBirth())
                .ownership(user.getOwnership())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt());
    }

    public record Counts(int cars, int addresses, int phones, int licenses, int quotes) {
    }
}
