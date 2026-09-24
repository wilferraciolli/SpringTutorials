package com.wiltech.insurly.account.settings;

import com.wiltech.insurly.account.identity.UserAccessService;
import com.wiltech.insurly.libraries.rest.BaseRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code /api/users/{userId}/settings} — a user's personal timezone /
 * language / currency. {@code userId} is either the literal {@code "me"} or
 * another user's id; {@link UserAccessService} allows the request through
 * only when the caller is that user or an admin. Reached by following the
 * {@code settings} link on {@code GET /api/users/me}; always fully populated,
 * with anything the user hasn't saved filled in from the platform settings.
 */
@RestController
@RequestMapping(value = "/users/{userId}/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserSettingsRestService extends BaseRestService {

    private final UserSettingsAppService appService;
    private final UserAccessService userAccess;

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("")
    public ResponseEntity<UserSettingsResource> get(@PathVariable final String userId) {
        final UserSettingsResource resource = appService.getSettings(userAccess.resolve(userId));
        return buildResponseOk(getJsonRootName(UserSettingsResource.class), resource);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PutMapping("")
    public ResponseEntity<UserSettingsResource> update(
            @PathVariable final String userId,
            @RequestBody @Valid final UserSettingsResource payload) {
        final UserSettingsResource resource = appService.updateSettings(userAccess.resolve(userId), payload);
        return buildResponseOk(getJsonRootName(UserSettingsResource.class), resource);
    }
}
