package com.wiltech.insurly.account.addresses;

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
 * {@code /api/users/{userId}/addresses} — a user's saved addresses.
 * {@code userId} is either the literal {@code "me"} or another user's id;
 * {@link UserAccessService} allows the request through only when the caller
 * is that user or an admin.
 */
@RestController
@RequestMapping(value = "/users/{userId}/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserAddressRestService extends BaseRestService {

    private final UserAddressAppService appService;
    private final UserAddressMetaFabricator metaFabricator;
    private final UserAccessService userAccess;

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/template")
    public ResponseEntity<UserAddressResource> template(@PathVariable final String userId) {
        return buildResponseOk(getJsonRootName(UserAddressResource.class),
                appService.createTemplate(), metaFabricator.forTemplate());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("")
    public ResponseEntity<UserAddressResource> findAll(@PathVariable final String userId) {
        final List<UserAddressResource> resources = appService.findForUser(userAccess.resolve(userId));
        return buildResponseOk(getJsonRootName(UserAddressResource.class), resources, metaFabricator.forCollection());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/{id}")
    public ResponseEntity<UserAddressResource> findById(@PathVariable final String userId, @PathVariable final UUID id) {
        return buildResponseOk(getJsonRootName(UserAddressResource.class),
                appService.findForUser(userAccess.resolve(userId), id), metaFabricator.forSingle());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PostMapping("")
    public ResponseEntity<UserAddressResource> create(
            @PathVariable final String userId, @RequestBody @Valid final UserAddressResource payload) {
        final UserAddressResource created = appService.create(userAccess.resolve(userId), payload);
        final Map<String, Metadata> metadata = metaFabricator.forSingle();
        return buildResponseCreated(getJsonRootName(UserAddressResource.class), created, metadata, null);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PutMapping("/{id}")
    public ResponseEntity<UserAddressResource> update(
            @PathVariable final String userId,
            @PathVariable final UUID id,
            @RequestBody @Valid final UserAddressResource payload) {
        return buildResponseOk(getJsonRootName(UserAddressResource.class),
                appService.update(userAccess.resolve(userId), id, payload), metaFabricator.forSingle());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final String userId, @PathVariable final UUID id) {
        appService.delete(userAccess.resolve(userId), id);
        return ResponseEntity.noContent().build();
    }
}
