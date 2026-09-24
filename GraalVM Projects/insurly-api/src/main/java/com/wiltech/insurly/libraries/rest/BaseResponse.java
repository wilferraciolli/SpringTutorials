package com.wiltech.insurly.libraries.rest;



import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonSerialize;


/**
 * The type Base response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    @JsonProperty("_data")
    @JsonSerialize(using = ResponseSerializer.class)
    private PayloadData data;

    @JsonProperty("_metadata")
    private Map<String, Metadata> metadata = new HashMap<>();

    @JsonProperty("_metaLinks")
    private Map<String, LinkDetails> metaLinks = new HashMap<>();

    @JsonProperty("_messages")
    private Object messages;

    /**
     * Adds single metadata.
     */
    public void addMetaData() {
        metadata.put("personId", new Metadata(true, null, true, Arrays.asList(new MetadataEmnbedded("123", "WilIAm"))));
    }

    /**
     * Add meta data.
     * @param fieldName the field name
     * @param meta the meta
     */
    public void addMetaData(final String fieldName, final Metadata meta) {
        metadata.put(fieldName, meta);
    }

    /**
     * Adds single link to the meta links.
     * @param link the link
     */
    public void addMetaLink(final Link link) {
        this.metaLinks.put(link.getRel().value(), new LinkDetails(link.getHref()));
    }

    /**
     * Add a collection of links to meta links.
     * @param linksToAdd the links to add
     */
    public void addMetaLinks(final List<Link> linksToAdd) {

        linksToAdd.forEach(this::addMetaLink);
    }

    public BaseResponse() {
    }

    public PayloadData getData() {
        return data;
    }

    public void setData(final String rootName, final Object data) {
        this.data = new PayloadData(rootName, data);
    }

    public Map<String, Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(final Map<String, Metadata> metadata) {
        this.metadata = metadata;
    }

    public Map<String, LinkDetails> getMetaLinks() {
        return metaLinks;
    }

    public void setMetaLinks(final Map<String, LinkDetails> metaLinks) {
        this.metaLinks = metaLinks;
    }

    public Object getMessages() {
        return messages;
    }

    public void setMessages(final Object messages) {
        this.messages = messages;
    }

}

