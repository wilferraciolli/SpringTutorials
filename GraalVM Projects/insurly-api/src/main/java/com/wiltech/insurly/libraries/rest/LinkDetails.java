package com.wiltech.insurly.libraries.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkDetails {
    @JsonProperty("href")
    private String href;

    public LinkDetails() {
    }

    public LinkDetails(final String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }
}
