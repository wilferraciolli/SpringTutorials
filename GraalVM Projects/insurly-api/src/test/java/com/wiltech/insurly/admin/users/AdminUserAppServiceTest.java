package com.wiltech.insurly.admin.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wiltech.insurly.account.addresses.UserAddressRepository;
import com.wiltech.insurly.account.cars.UserCarRepository;
import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.account.identity.UserOwnership;
import com.wiltech.insurly.account.licenses.UserLicenseRepository;
import com.wiltech.insurly.account.phones.UserPhoneRepository;
import com.wiltech.insurly.admin.access.AdminAccessService;
import com.wiltech.insurly.admin.users.UserSearchCriteria.UserFilter;
import com.wiltech.insurly.admin.users.UserSearchCriteria.UserSort;
import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteRepository;
import com.wiltech.insurly.quote.QuoteStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminUserAppServiceTest {

    private AppUserRepository users;
    private AdminUserAssembler assembler;
    private AdminAccessService adminAccess;
    private UserCarRepository cars;
    private UserAddressRepository addresses;
    private UserPhoneRepository phones;
    private UserLicenseRepository licenses;
    private QuoteRepository quotes;
    private Clock clock;

    private AdminUserAppService service;

    @BeforeEach
    void setUp() {
        users = mock(AppUserRepository.class);
        assembler = mock(AdminUserAssembler.class);
        adminAccess = mock(AdminAccessService.class);
        cars = mock(UserCarRepository.class);
        addresses = mock(UserAddressRepository.class);
        phones = mock(UserPhoneRepository.class);
        licenses = mock(UserLicenseRepository.class);
        quotes = mock(QuoteRepository.class);
        clock = Clock.fixed(Instant.parse("2026-09-06T00:00:00Z"), ZoneOffset.UTC);

        when(quotes.findAll()).thenReturn(List.of());
        when(cars.findAll()).thenReturn(List.of());

        // Return a dummy resource with matching email for inspection
        when(assembler.toSummary(any())).thenAnswer(invocation -> {
            final AppUser user = invocation.getArgument(0);
            return AdminUserResource.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .displayName(user.getDisplayName())
                    .build();
        });

        service = new AdminUserAppService(
                users, assembler, adminAccess, clock, cars, addresses, phones, licenses, quotes);
    }

    @Test
    void searchSortByNameHandlesUsersWithNullOrBlankFields() {
        final AppUser userWithNulls = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email(null)
                .firstName(null)
                .lastName(null)
                .displayName(null)
                .build();

        final AppUser userWithBlanks = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email("   ")
                .firstName(" ")
                .lastName("")
                .displayName("  ")
                .build();

        final AppUser userAlice = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .build();

        final AppUser userBob = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email("bob@example.com")
                .displayName("Bob The Builder")
                .build();

        when(users.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(userBob, userWithNulls, userAlice, userWithBlanks));

        final UserSearchCriteria criteria = new UserSearchCriteria(null, null, UserFilter.ALL, UserSort.NAME);
        final List<AdminUserResource> result = service.search(criteria);

        assertThat(result).hasSize(4);
        // Empty/blank sort names are placed first (or equal), followed by "Alice Smith", then "Bob The Builder"
        assertThat(result.get(2).getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get(3).getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void searchSortByNameFollowsPrecedenceAndIsCaseInsensitive() {
        final AppUser u1 = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email("zebra@example.com")
                .firstName("charlie")
                .lastName("Brown")
                .build();

        final AppUser u2 = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email("beta@example.com")
                .displayName("Beta User")
                .build();

        final AppUser u3 = AppUser.builder()
                .id(UUID.randomUUID())
                .ownership(UserOwnership.MANAGED)
                .email("alpha@example.com")
                .build();

        when(users.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(u1, u2, u3));

        final UserSearchCriteria criteria = new UserSearchCriteria(null, null, UserFilter.ALL, UserSort.NAME);
        final List<AdminUserResource> result = service.search(criteria);

        assertThat(result).extracting(AdminUserResource::getEmail)
                .containsExactly("alpha@example.com", "beta@example.com", "zebra@example.com");
    }

    @Test
    void searchSortByRenewalSoonestOrdersByQuoteExpiry() {
        final UUID u1Id = UUID.randomUUID();
        final UUID u2Id = UUID.randomUUID();

        final AppUser u1 = AppUser.builder()
                .id(u1Id)
                .ownership(UserOwnership.MANAGED)
                .email("u1@example.com")
                .build();

        final AppUser u2 = AppUser.builder()
                .id(u2Id)
                .ownership(UserOwnership.MANAGED)
                .email("u2@example.com")
                .build();

        final Instant now = Instant.parse("2026-09-06T00:00:00Z");

        final Quote q1 = Quote.builder()
                .id(UUID.randomUUID())
                .userId(u1Id)
                .status(QuoteStatus.COMPLETE)
                .guestToken(UUID.randomUUID())
                .premiumBasic(BigDecimal.TEN)
                .premiumStandard(BigDecimal.TEN)
                .premiumPremium(BigDecimal.TEN)
                .createdAt(now.minusSeconds(3600))
                .expiresAt(now.plusSeconds(86400 * 10)) // expires in 10 days
                .build();

        final Quote q2 = Quote.builder()
                .id(UUID.randomUUID())
                .userId(u2Id)
                .status(QuoteStatus.COMPLETE)
                .guestToken(UUID.randomUUID())
                .premiumBasic(BigDecimal.TEN)
                .premiumStandard(BigDecimal.TEN)
                .premiumPremium(BigDecimal.TEN)
                .createdAt(now.minusSeconds(3600))
                .expiresAt(now.plusSeconds(86400 * 2)) // expires in 2 days
                .build();

        when(quotes.findAll()).thenReturn(List.of(q1, q2));
        when(users.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(u1, u2));

        final UserSearchCriteria criteria = new UserSearchCriteria(null, null, UserFilter.ALL, UserSort.RENEWAL_SOONEST);
        final List<AdminUserResource> result = service.search(criteria);

        assertThat(result).extracting(AdminUserResource::getEmail)
                .containsExactly("u2@example.com", "u1@example.com");
    }
}
