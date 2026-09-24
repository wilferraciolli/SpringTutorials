package com.wiltech.insurly.account.phones;

import com.wiltech.insurly.account.identity.UserAccessService;
import com.wiltech.insurly.libraries.rest.BaseRestService;
import com.wiltech.insurly.libraries.rest.Metadata;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code /api/users/{userId}/phones} — a user's saved phone numbers.
 * {@code userId} is either the literal {@code "me"} or another user's id;
 * {@link UserAccessService} allows the request through only when the caller
 * is that user or an admin.
 */
@RestController
@RequestMapping(value = "/users/{userId}/phones", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserPhoneRestService extends BaseRestService {

    private final UserPhoneAppService appService;
    private final UserPhoneMetaFabricator metaFabricator;
    private final UserAccessService userAccess;

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/template")
    public ResponseEntity<UserPhoneResource> template(@PathVariable final String userId) {
        return buildResponseOk(getJsonRootName(UserPhoneResource.class),
                appService.createTemplate(), metaFabricator.forTemplate());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("")
    public ResponseEntity<UserPhoneResource> findAll(@PathVariable final String userId) {
        final List<UserPhoneResource> resources = appService.findForUser(userAccess.resolve(userId));
        return buildResponseOk(getJsonRootName(UserPhoneResource.class), resources, metaFabricator.forCollection());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/{id}")
    public ResponseEntity<UserPhoneResource> findById(@PathVariable final String userId, @PathVariable final UUID id) {
        return buildResponseOk(getJsonRootName(UserPhoneResource.class),
                appService.findForUser(userAccess.resolve(userId), id), metaFabricator.forSingle());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PostMapping("")
    public ResponseEntity<UserPhoneResource> create(
            @PathVariable final String userId, @RequestBody @Valid final UserPhoneResource payload) {
        final UserPhoneResource created = appService.create(userAccess.resolve(userId), payload);
        final Map<String, Metadata> metadata = metaFabricator.forSingle();
        return buildResponseCreated(getJsonRootName(UserPhoneResource.class), created, metadata, null);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PutMapping("/{id}")
    public ResponseEntity<UserPhoneResource> update(
            @PathVariable final String userId,
            @PathVariable final UUID id,
            @RequestBody @Valid final UserPhoneResource payload) {
        return buildResponseOk(getJsonRootName(UserPhoneResource.class),
                appService.update(userAccess.resolve(userId), id, payload), metaFabricator.forSingle());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final String userId, @PathVariable final UUID id) {
        appService.delete(userAccess.resolve(userId), id);
        return ResponseEntity.noContent().build();
    }
}
