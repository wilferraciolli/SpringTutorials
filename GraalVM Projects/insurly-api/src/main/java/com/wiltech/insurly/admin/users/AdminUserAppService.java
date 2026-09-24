package com.wiltech.insurly.admin.users;

import com.wiltech.insurly.account.addresses.UserAddressRepository;
import com.wiltech.insurly.account.cars.UserCar;
import com.wiltech.insurly.account.cars.UserCarRepository;
import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.account.identity.UserOwnership;
import com.wiltech.insurly.account.licenses.UserLicenseRepository;
import com.wiltech.insurly.account.phones.UserPhoneRepository;
import com.wiltech.insurly.admin.access.AdminAccessService;
import com.wiltech.insurly.admin.users.AdminUserAssembler.Counts;
import com.wiltech.insurly.admin.users.UserSearchCriteria.UserFilter;
import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteRepository;
import com.wiltech.insurly.quote.QuoteStatus;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserAppService {

    private static final Duration RENEWAL_WINDOW = Duration.ofDays(30);

    /** Rough "expensive car" proxy — matched case-insensitively against {@code user_car.make}. */
    private static final Set<String> PREMIUM_MAKES = Set.of(
            "audi", "bmw", "mercedes", "mercedes-benz", "porsche", "tesla", "jaguar",
            "land rover", "range rover", "lexus", "volvo", "maserati", "bentley",
            "aston martin", "ferrari", "lamborghini", "alfa romeo", "genesis", "polestar");

    private final AppUserRepository users;
    private final AdminUserAssembler assembler;
    private final AdminAccessService adminAccess;
    private final Clock clock;

    private final UserCarRepository cars;
    private final UserAddressRepository addresses;
    private final UserPhoneRepository phones;
    private final UserLicenseRepository licenses;
    private final QuoteRepository quotes;

    @Transactional(readOnly = true)
    public AdminUserResource createTemplate() {
        adminAccess.requireAdmin();
        return AdminUserResource.builder().ownership(UserOwnership.MANAGED).roles(Set.of()).build();
    }

    /**
     * The customer list with optional search / filter / sort. Data volumes here
     * are small (an admin tool), so filtering runs in memory over two batch
     * loads rather than a bespoke query per criterion.
     */
    @Transactional(readOnly = true)
    public List<AdminUserResource> search(final UserSearchCriteria criteria) {
        adminAccess.requireAdmin();

        final List<AppUser> all = users.findAllByOrderByCreatedAtDesc();
        final Instant now = clock.instant();

        final Map<UUID, List<Quote>> quotesByUser = quotes.findAll().stream()
                .filter(q -> q.getUserId() != null)
                .collect(Collectors.groupingBy(Quote::getUserId));
        final Map<UUID, List<UserCar>> carsByUser = cars.findAll().stream()
                .collect(Collectors.groupingBy(UserCar::getUserId));

        Stream<AppUser> stream = all.stream();

        final String q = StringUtils.trimToNull(criteria.query());
        if (q != null) {
            final String needle = q.toLowerCase();
            stream = stream.filter(u -> matchesText(u, needle));
        }
        if (criteria.ownership() != null) {
            stream = stream.filter(u -> u.getOwnership() == criteria.ownership());
        }
        stream = stream.filter(u -> matchesFilter(u, criteria.filter(), quotesByUser, carsByUser, now));

        final List<AppUser> filtered = stream.toList();
        return sort(filtered, criteria.sort(), quotesByUser, now).stream()
                .map(assembler::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserResource get(final UUID id) {
        final AppUser user = adminAccess.requireViewable(id);
        return assembler.toDetail(user, countsFor(id));
    }

    @Transactional
    public AdminUserResource create(@Valid final AdminUserResource payload) {
        adminAccess.requireAdmin();
        final AppUser user = AppUser.builder()
                .ownership(UserOwnership.MANAGED)
                .email(payload.getEmail().trim())
                .firstName(blankToNull(payload.getFirstName()))
                .lastName(blankToNull(payload.getLastName()))
                .displayName(blankToNull(payload.getDisplayName()))
                .dateOfBirth(payload.getDateOfBirth())
                .createdAt(clock.instant())
                .updatedAt(clock.instant())
                .build();
        if (payload.getRoles() != null) {
            user.replaceRoles(payload.getRoles());
        }
        return assembler.toDetail(users.save(user), new Counts(0, 0, 0, 0, 0));
    }

    /**
     * Admin edit of any user — email/name/dob and roles. {@code ownership} no
     * longer gates writes: admin may manage self-service (Clerk-registered)
     * users just as freely as MANAGED ones.
     */
    @Transactional
    public AdminUserResource update(final UUID id, @Valid final AdminUserResource payload) {
        final AppUser user = adminAccess.requireViewable(id);
        user.adminEdit(
                payload.getEmail().trim(),
                blankToNull(payload.getDisplayName()),
                blankToNull(payload.getFirstName()),
                blankToNull(payload.getLastName()),
                payload.getDateOfBirth(),
                clock.instant());
        if (payload.getRoles() != null) {
            user.replaceRoles(payload.getRoles());
        }
        return assembler.toDetail(users.save(user), countsFor(id));
    }

    @Transactional
    public void delete(final UUID id) {
        adminAccess.requireViewable(id);
        users.deleteById(id);
    }

    // --- search helpers ------------------------------------------------

    private static boolean matchesText(final AppUser u, final String needle) {
        return Stream.of(u.getFirstName(), u.getLastName(), u.getDisplayName(), u.getEmail(),
                        StringUtils.trimToEmpty(u.getFirstName()) + " " + StringUtils.trimToEmpty(u.getLastName()))
                .filter(StringUtils::isNotBlank)
                .anyMatch(field -> field.toLowerCase().contains(needle));
    }

    private boolean matchesFilter(
            final AppUser u,
            final UserFilter filter,
            final Map<UUID, List<Quote>> quotesByUser,
            final Map<UUID, List<UserCar>> carsByUser,
            final Instant now) {
        final List<Quote> userQuotes = quotesByUser.getOrDefault(u.getId(), List.of());
        return switch (filter) {
            case ALL -> true;
            case NO_QUOTES -> userQuotes.isEmpty();
            case HAS_OPEN_QUOTES -> userQuotes.stream().anyMatch(q -> isOpen(q, now));
            case RENEWAL_DUE -> userQuotes.stream().anyMatch(q -> isOpen(q, now)
                    && q.getExpiresAt().isBefore(now.plus(RENEWAL_WINDOW)));
            case PREMIUM_VEHICLE -> carsByUser.getOrDefault(u.getId(), List.of()).stream()
                    .anyMatch(c -> c.getMake() != null && PREMIUM_MAKES.contains(c.getMake().trim().toLowerCase()));
        };
    }

    private static boolean isOpen(final Quote q, final Instant now) {
        return q.getStatus() == QuoteStatus.COMPLETE && q.getExpiresAt().isAfter(now);
    }

    private List<AppUser> sort(
            final List<AppUser> filtered,
            final UserSearchCriteria.UserSort sort,
            final Map<UUID, List<Quote>> quotesByUser,
            final Instant now) {
        return switch (sort) {
            case RECENT -> filtered; // already newest-first from the repository
            case NAME -> filtered.stream()
                    .sorted(Comparator.comparing(AdminUserAppService::sortName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                    .toList();
            case RENEWAL_SOONEST -> filtered.stream()
                    .sorted(Comparator.comparing(u -> soonestExpiry(quotesByUser.getOrDefault(u.getId(), List.of()), now)))
                    .toList();
        };
    }

    private static String sortName(final AppUser u) {
        if (u == null) {
            return "";
        }
        final String full = (StringUtils.trimToEmpty(u.getFirstName()) + " " + StringUtils.trimToEmpty(u.getLastName())).trim();
        final String candidate = StringUtils.firstNonBlank(full, u.getDisplayName(), u.getEmail());
        return candidate != null ? candidate : "";
    }

    private static Instant soonestExpiry(final List<Quote> userQuotes, final Instant now) {
        return userQuotes.stream()
                .filter(q -> isOpen(q, now))
                .map(Quote::getExpiresAt)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(Instant.MAX);
    }

    private Counts countsFor(final UUID id) {
        return new Counts(
                (int) cars.countByUserId(id),
                (int) addresses.countByUserId(id),
                (int) phones.countByUserId(id),
                (int) licenses.countByUserId(id),
                (int) quotes.countByUserId(id));
    }

    private static String blankToNull(final String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
