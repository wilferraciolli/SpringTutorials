package com.wiltech.insurly.account.cars;

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
 * {@code /api/users/{userId}/cars} — a user's saved cars. {@code userId} is
 * either the literal {@code "me"} or another user's id; {@link UserAccessService}
 * allows the request through only when the caller is that user or an admin.
 */
@RestController
@RequestMapping(value = "/users/{userId}/cars", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserCarRestService extends BaseRestService {

    private final UserCarAppService appService;
    private final UserCarMetaFabricator metaFabricator;
    private final UserAccessService userAccess;

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/template")
    public ResponseEntity<UserCarResource> template(@PathVariable final String userId) {
        final UserCarResource resource = appService.createTemplate();
        return buildResponseOk(getJsonRootName(UserCarResource.class), resource, metaFabricator.forTemplate());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("")
    public ResponseEntity<UserCarResource> findAll(@PathVariable final String userId) {
        final List<UserCarResource> resources = appService.findForUser(userAccess.resolve(userId));
        return buildResponseOk(getJsonRootName(UserCarResource.class), resources, metaFabricator.forCollection());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/{id}")
    public ResponseEntity<UserCarResource> findById(@PathVariable final String userId, @PathVariable final UUID id) {
        final UserCarResource resource = appService.findForUser(userAccess.resolve(userId), id);
        return buildResponseOk(getJsonRootName(UserCarResource.class), resource, metaFabricator.forSingle());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PostMapping("")
    public ResponseEntity<UserCarResource> create(
            @PathVariable final String userId, @RequestBody @Valid final UserCarResource payload) {
        final UserCarResource created = appService.create(userAccess.resolve(userId), payload);
        final Map<String, Metadata> metadata = metaFabricator.forSingle();
        return buildResponseCreated(getJsonRootName(UserCarResource.class), created, metadata, null);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PutMapping("/{id}")
    public ResponseEntity<UserCarResource> update(
            @PathVariable final String userId,
            @PathVariable final UUID id,
            @RequestBody @Valid final UserCarResource payload) {
        final UserCarResource updated = appService.update(userAccess.resolve(userId), id, payload);
        return buildResponseOk(getJsonRootName(UserCarResource.class), updated, metaFabricator.forSingle());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final String userId, @PathVariable final UUID id) {
        appService.delete(userAccess.resolve(userId), id);
        return ResponseEntity.noContent().build();
    }
}
