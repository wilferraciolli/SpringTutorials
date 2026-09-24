package com.wiltech.insurly.account.quotes;

import com.wiltech.insurly.account.cars.UserCarRepository;
import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.account.licenses.UserLicenseRepository;
import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import com.wiltech.insurly.quote.DriverResource;
import com.wiltech.insurly.quote.QuoteResource;
import com.wiltech.insurly.quote.VehicleResource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds a partly-filled car-quote payload from what an account already holds:
 * profile name + date of birth, the most recently added car, and the most
 * recent licence. Driving history and coverage aren't stored, so they're left
 * for the wizard / admin form to collect. Used by {@code GET /api/users/{userId}/quotes/prefill}
 * for both self-service and admin-on-behalf-of callers.
 */
@Service
@RequiredArgsConstructor
public class QuotePrefillAppService {

    private final AppUserRepository users;
    private final UserCarRepository cars;
    private final UserLicenseRepository licenses;

    @Transactional(readOnly = true)
    public QuoteResource forUser(final UUID userId) {
        final AppUser user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(userId)));

        return QuoteResource.builder()
                .driver(buildDriver(user))
                .vehicle(buildVehicle(userId))
                .build();
    }

    private DriverResource buildDriver(final AppUser user) {
        final DriverResource.DriverResourceBuilder driver = DriverResource.builder()
                .firstName(firstName(user))
                .lastName(lastName(user))
                .dateOfBirth(user.getDateOfBirth());

        latest(licenses.findByUserIdOrderByCreatedAtDesc(user.getId())).ifPresent(licence -> {
            driver.licenseStatus(licence.getStatus());
            driver.yearsLicensed(licence.getYearsHeld());
        });
        return driver.build();
    }

    private VehicleResource buildVehicle(final UUID userId) {
        return latest(cars.findByUserIdOrderByCreatedAtDesc(userId))
                .map(car -> VehicleResource.builder()
                        .make(car.getMake())
                        .model(car.getModel())
                        .year(car.getYear())
                        .primaryUse(car.getPrimaryUse())
                        .build())
                .orElse(null);
    }

    private String firstName(final AppUser user) {
        return StringUtils.isNotBlank(user.getFirstName())
                ? user.getFirstName()
                : splitDisplayName(user.getDisplayName(), 0);
    }

    private String lastName(final AppUser user) {
        return StringUtils.isNotBlank(user.getLastName())
                ? user.getLastName()
                : splitDisplayName(user.getDisplayName(), 1);
    }

    private static String splitDisplayName(final String displayName, final int part) {
        if (StringUtils.isBlank(displayName)) {
            return null;
        }
        final String[] tokens = displayName.trim().split("\\s+", 2);
        return part < tokens.length ? tokens[part] : null;
    }

    private static <T> Optional<T> latest(final List<T> ordered) {
        return ordered.isEmpty() ? Optional.empty() : Optional.of(ordered.get(0));
    }
}
