package com.wiltech.insurly.admin.users;

import com.wiltech.insurly.account.identity.UserOwnership;
import com.wiltech.insurly.admin.users.UserSearchCriteria.UserFilter;
import com.wiltech.insurly.admin.users.UserSearchCriteria.UserSort;
import com.wiltech.insurly.libraries.rest.BaseRestService;
import com.wiltech.insurly.libraries.rest.Metadata;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** {@code /api/admin/users} — the admin's customer list. Gated on the ADMIN role by AdminGuardInterceptor. */
@RestController
@RequestMapping(value = "/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminUserRestService extends BaseRestService {

    private final AdminUserAppService appService;
    private final AdminUserMetaFabricator metaFabricator;

    @GetMapping("/template")
    public ResponseEntity<AdminUserResource> template() {
        return buildResponseOk(getJsonRootName(AdminUserResource.class),
                appService.createTemplate(), metaFabricator.forTemplate());
    }

    /**
     * The customer list. Optional query params:
     * {@code q} (name/email search), {@code ownership} (MANAGED|SELF_SERVICE),
     * {@code filter} (ALL|RENEWAL_DUE|HAS_OPEN_QUOTES|NO_QUOTES|PREMIUM_VEHICLE),
     * {@code sort} (RECENT|NAME|RENEWAL_SOONEST).
     */
    @GetMapping("")
    public ResponseEntity<AdminUserResource> findAll(
            @RequestParam(value = "q", required = false) final String q,
            @RequestParam(value = "ownership", required = false) final UserOwnership ownership,
            @RequestParam(value = "filter", required = false) final UserFilter filter,
            @RequestParam(value = "sort", required = false) final UserSort sort) {
        final List<AdminUserResource> resources =
                appService.search(new UserSearchCriteria(q, ownership, filter, sort));
        return buildResponseOk(getJsonRootName(AdminUserResource.class), resources, metaFabricator.forCollection());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResource> findById(@PathVariable("id") final UUID id) {
        return buildResponseOk(getJsonRootName(AdminUserResource.class),
                appService.get(id), metaFabricator.forSingle());
    }

    @PostMapping("")
    public ResponseEntity<AdminUserResource> create(@RequestBody @Valid final AdminUserResource payload) {
        final AdminUserResource created = appService.create(payload);
        final Map<String, Metadata> metadata = metaFabricator.forSingle();
        return buildResponseCreated(getJsonRootName(AdminUserResource.class), created, metadata, null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResource> update(
            @PathVariable("id") final UUID id,
            @RequestBody @Valid final AdminUserResource payload) {
        return buildResponseOk(getJsonRootName(AdminUserResource.class),
                appService.update(id, payload), metaFabricator.forSingle());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") final UUID id) {
        appService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
