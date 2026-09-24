package com.wiltech.insurly.account.licenses;

import java.util.UUID;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLicenseAssembler {

    private final UserLicenseLinkProvider linkProvider;
    private final Clock clock;

    public UserLicense toEntity(final UUID ownerId, final UserLicenseResource payload) {
        return UserLicense.builder()
                .userId(ownerId)
                .licenseNumber(payload.getLicenseNumber().trim())
                .status(payload.getStatus())
                .issuingRegion(payload.getIssuingRegion())
                .issuedOn(payload.getIssuedOn())
                .expiresOn(payload.getExpiresOn())
                .yearsHeld(payload.getYearsHeld())
                .createdAt(clock.instant())
                .updatedAt(clock.instant())
                .build();
    }

    /** @param maskNumber true for list / mutation responses; false only on the single-resource GET */
    public UserLicenseResource toDto(final UserLicense entity, final boolean maskNumber) {
        final UserLicenseResource resource = UserLicenseResource.builder()
                .id(entity.getId())
                .licenseNumber(maskNumber ? mask(entity.getLicenseNumber()) : entity.getLicenseNumber())
                .status(entity.getStatus())
                .issuingRegion(entity.getIssuingRegion())
                .issuedOn(entity.getIssuedOn())
                .expiresOn(entity.getExpiresOn())
                .yearsHeld(entity.getYearsHeld())
                .build();

        final List<Link> links = new ArrayList<>();
        links.add(linkProvider.generateSelfLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateUpdateLink(entity.getUserId(), entity.getId()));
        links.add(linkProvider.generateDeleteLink(entity.getUserId(), entity.getId()));
        resource.addLinks(links);

        return resource;
    }

    private static String mask(final String number) {
        if (number == null || number.length() <= 4) {
            return "••••";
        }
        return "••••• " + number.substring(number.length() - 4);
    }
}
