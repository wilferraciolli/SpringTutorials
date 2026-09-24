package com.wiltech.insurly.account.me;

import com.wiltech.insurly.account.identity.AppUser;
import com.wiltech.insurly.account.identity.AppUserRepository;
import com.wiltech.insurly.account.identity.CurrentUser;
import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeAppService {

    private static final Logger log = LoggerFactory.getLogger(MeAppService.class);

    private final AppUserRepository users;
    private final MeAssembler assembler;
    private final Clock clock;

    @Transactional(readOnly = true)
    public MeResource getProfile(final CurrentUser user) {
        return assembler.toDto(load(user), user.roles());
    }

    @Transactional
    public MeResource updateProfile(final CurrentUser user, @Valid final MeResource payload) {
        final AppUser entity = load(user);
        entity.editProfile(
                blankToNull(payload.getDisplayName()),
                blankToNull(payload.getFirstName()),
                blankToNull(payload.getLastName()),
                payload.getDateOfBirth(),
                clock.instant());
        return assembler.toDto(users.save(entity), user.roles());
    }

    /**
     * Deletes the local account. FK cascades remove cars/addresses/phones/
     * licences; quotes have their {@code user_id} set null (they revert to
     * guest quotes).
     *
     * <p>TODO: also delete the user at the identity provider (Clerk / Keycloak
     * admin API) - see docs/05-security-and-accounts.md.
     */
    @Transactional
    public void deleteAccount(final CurrentUser user) {
        users.deleteById(user.id());
        log.info("Deleted local account {}. Provider-side deletion still required.", user.id());
    }

    private AppUser load(final CurrentUser user) {
        return users.findById(user.id())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private static String blankToNull(final String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
