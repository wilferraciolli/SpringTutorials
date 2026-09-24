package com.wiltech.insurly.quote;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/quotes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class QuoteRestService extends BaseRestService {

    private final QuoteAppService appService;
    private final QuoteMetaFabricator metaFabricator;

    @GetMapping("/template")
    public ResponseEntity<QuoteResource> template() {
        final QuoteResource resource = appService.createTemplate();

        return buildResponseOk(getJsonRootName(QuoteResource.class), resource, metaFabricator.createMetaForTemplate());
    }

    @PostMapping("/car")
    public ResponseEntity<QuoteResource> create(@RequestBody @Valid final QuoteResource payload) {
        final QuoteResource createdResource = this.appService.create(payload);
        Map<String, Metadata> metadata = metaFabricator.createMetaForSingleResource();

        return buildResponseCreated(
                getJsonRootName(QuoteResource.class),
                createdResource,
                metadata,
                null);
    }

    @GetMapping("")
    public ResponseEntity<QuoteResource> findAll() {
        final List<QuoteResource> resources = appService.findAll();
        Map<String, Metadata> metadata = metaFabricator.createMetaForCollectionResource();

        return buildResponseOk(getJsonRootName(QuoteResource.class), resources, metadata);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResource> findById(@PathVariable("id") final UUID id) {
        final QuoteResource resource = this.appService.findById(id);
        Map<String, Metadata> metadata = metaFabricator.createMetaForSingleResource();

        return buildResponseOk(getJsonRootName(QuoteResource.class), resource, metadata);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteResource> update(
            @PathVariable("id") final UUID id,
            @RequestBody @Valid final QuoteResource payload) {
        final QuoteResource updatedResource = this.appService.update(id, payload);
        Map<String, Metadata> metadata = metaFabricator.createMetaForSingleResource();

        return buildResponseOk(getJsonRootName(QuoteResource.class), updatedResource, metadata);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final UUID id) {
        this.appService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
