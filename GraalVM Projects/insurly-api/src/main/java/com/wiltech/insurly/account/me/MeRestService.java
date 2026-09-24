package com.wiltech.insurly.account.me;

import com.wiltech.insurly.account.identity.CurrentUserService;
import com.wiltech.insurly.libraries.rest.BaseRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code /api/users/me} - the signed-in user's profile and the account-deletion entry point. */
@RestController
@RequestMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MeRestService extends BaseRestService {

    private final MeAppService appService;
    private final CurrentUserService currentUser;

    @GetMapping("")
    public ResponseEntity<MeResource> me() {
        final MeResource resource = appService.getProfile(currentUser.require());
        return buildResponseOk(getJsonRootName(MeResource.class), resource);
    }

    @PutMapping("")
    public ResponseEntity<MeResource> update(@RequestBody @Valid final MeResource payload) {
        final MeResource resource = appService.updateProfile(currentUser.require(), payload);
        return buildResponseOk(getJsonRootName(MeResource.class), resource);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteMe() {
        appService.deleteAccount(currentUser.require());
        return ResponseEntity.noContent().build();
    }
}
