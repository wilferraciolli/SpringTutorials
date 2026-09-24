package com.wiltech.insurly.admin.providers;

import com.wiltech.insurly.libraries.rest.BaseRestService;
import com.wiltech.insurly.libraries.rest.Metadata;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController()
@RequestMapping(value = "/admin/providers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProviderRestService extends BaseRestService {

    private final ProviderAppService appService;
    private final ProviderMetaFabricator metaFabricator;

    @GetMapping("/template")
    public ResponseEntity<ProviderResource> template() {
        final ProviderResource resource = appService.createTemplate();

        return buildResponseOk(getJsonRootName(ProviderResource.class), resource, metaFabricator.createMetaForTemplate());
    }

    @PostMapping("")
    public ResponseEntity<ProviderResource> create(@RequestBody @Valid final ProviderResource payload) {
        final ProviderResource createdResource = this.appService.create(payload);
        Map<String, Metadata> metadata = metaFabricator.createMetaForSingleResource();

        return buildResponseCreated(
                getJsonRootName(ProviderResource.class),
                createdResource,
                metadata,
                null
        );
    }

    /** @param q optional case-insensitive filter on provider name. */
    @GetMapping("")
    public ResponseEntity<ProviderResource> findAll(
            @RequestParam(value = "q", required = false) final String q) {
        final List<ProviderResource> resources = appService.findAll(q);
        Map<String, Metadata> metadata = metaFabricator.createMetaForCollectionResource();

        return buildResponseOk(getJsonRootName(ProviderResource.class), resources, metadata);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResource> findById(@PathVariable("id") final UUID id) {
        final ProviderResource resource = this.appService.findById(id);

        Map<String, Metadata> metadata = metaFabricator.createMetaForSingleResource();

        return buildResponseOk(getJsonRootName(ProviderResource.class), resource, metadata);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResource> update(
            @PathVariable("id") final UUID id,
            @RequestBody @Valid final ProviderResource payload) {
        final ProviderResource updatedResource = this.appService.update(id, payload);
        Map<String, Metadata> metadata = metaFabricator.createMetaForSingleResource();

        return buildResponseOk(getJsonRootName(ProviderResource.class), updatedResource, metadata);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(
            @PathVariable("id") final UUID id) {
        this.appService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
