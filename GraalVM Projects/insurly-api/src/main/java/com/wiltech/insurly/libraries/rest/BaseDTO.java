package com.wiltech.insurly.libraries.rest;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO implements Transferable {
    @JsonProperty("links")
    private final Map<String, LinkDetails> links = new HashMap<>();

    /**
     * Add a single link to the resource links.
     * @param link The link to add
     */
    public void addLink(final Link link) {
        links.put(link.getRel().value(), new LinkDetails(link.getHref()));
    }

    /**
     * Adds a collection of links to the resource links.
     * @param linksToAdd The links to add
     */
    public void addLinks(final List<Link> linksToAdd) {

        linksToAdd.stream()
                .filter(Objects::nonNull)
                .forEach(this::addLink);
    }

    public Map<String, LinkDetails> getLinks() {
        return links;
    }
}
