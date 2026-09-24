package com.wiltech.insurly.libraries.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseRestService {
    private static final String SERVER_CONTEXT_PATH = "/api";

    /**
     * Build location header uri. It assumes that the endpoint wil always have an extra {id} param added
     * @param params  the parameters to be added
     * @return the uri
     */
    protected URI buildLocationHeader(final Object... params) {
        return MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}")
                .buildAndExpand(params)
                .toUri();
    }

    /**
     * Build self link link.
     * @return the link
     */
    private Link buildSelfLink() {
        final String uriString = removeBeforeApi(ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUriString()
        );

        return Link.of(uriString, IanaLinkRelations.SELF.value());
    }

    /**
     * Build response ok response entity.
     * @param rootName the root name
     * @param resource the resource
     * @return the response entity
     */
    public ResponseEntity buildResponseOk(final String rootName, final BaseDTO resource) {
        final BaseResponse response = new BaseResponse();
        response.setData(rootName, resource);
        response.setMessages("Template Message");
        response.addMetaLink(buildSelfLink());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Build response ok response entity.
     * @param rootName the root name
     * @param resource the resource
     * @param metadata the metadata
     * @return the response entity
     */
    public ResponseEntity buildResponseOk(final String rootName, final BaseDTO resource, final Map<String, Metadata> metadata) {
        final BaseResponse response = new BaseResponse();
        response.setData(rootName, resource);
        response.setMetadata(metadata);
        response.addMetaLink(buildSelfLink());

        return ResponseEntity.ok().body(response);
    }

    /**
     *  Build response created response entity.
     * @param rootName the root name
     * @param createdResource the resource created
     * @param metadata the metadata
     * @param locationHeader the location header
     * @return the response entity
     */
    public ResponseEntity buildResponseCreated(String rootName, BaseDTO createdResource, Map<String, Metadata> metadata, URI locationHeader) {
        final BaseResponse response = new BaseResponse();
        response.setData(rootName, createdResource);
        response.setMetadata(metadata);
        response.addMetaLink(buildSelfLink());

        return ResponseEntity
                .created(locationHeader)
                .body(response);
    }

    /**
     * Build response ok response entity. For a collection.
     * @param rootName the root name
     * @param resources the resources
     * @return the response entity
     */
    public ResponseEntity buildResponseOk(final String rootName, final List<? extends BaseDTO> resources) {
        final BaseResponse response = new BaseResponse();
        response.setData(rootName, resources);
        response.addMetaLink(buildSelfLink());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Build response ok response entity. For a collection.
     * @param rootName the root name
     * @param resources the resources
     * @param metadata the metadata
     * @return the response entity
     */
    public ResponseEntity buildResponseOk(final String rootName, final List<? extends BaseDTO> resources, final Map<String, Metadata> metadata) {
        final BaseResponse response = new BaseResponse();
        response.setData(rootName, resources);
        response.setMetadata(metadata);
        response.addMetaLink(buildSelfLink());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Build response ok response entity. For a collection.
     * @param rootName the root name
     * @param resources the resources
     * @param metadata the metadata
     * @param metaLinks the meta links
     * @return the response entity
     */
    public ResponseEntity buildResponseOk(final String rootName, final List<? extends BaseDTO> resources, final Map<String, Metadata> metadata,
                                          final List<Link> metaLinks) {
        // add the self link to the collection of links
        List<Link> metaLinksToAdd = generateMetaLinks(metaLinks);

        final BaseResponse response = new BaseResponse();
        response.setData(rootName, resources);
        response.setMetadata(metadata);
        response.addMetaLinks(metaLinksToAdd);

        return ResponseEntity.ok().body(response);
    }

    /**
     * Gets json root name.
     * @param clazz the clazz
     * @return the json root name
     */
    public String getJsonRootName(final Class<? extends BaseDTO> clazz) {
        if (clazz.isAnnotationPresent(JsonRootName.class)) {
            return clazz.getAnnotation(JsonRootName.class).value();
        } else {
            return clazz.getSimpleName();
        }
    }

    private List<Link> generateMetaLinks(final List<Link> metaLinks) {
        List<Link> metaLinksToAdd = new ArrayList<>();
        metaLinksToAdd.add(buildSelfLink());
        metaLinksToAdd.addAll(metaLinks);

        return metaLinksToAdd;
    }

    private static String removeBeforeApi(String originalString) {
        int indexOfApi = originalString.indexOf(SERVER_CONTEXT_PATH);

        if (indexOfApi != -1) {
            return originalString.substring(indexOfApi);
        } else {
            // "/api" not found in the string
            return originalString;
        }
    }
}
