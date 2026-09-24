package com.wiltech.insurly.libraries.rest;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataEmnbedded {
    @JsonProperty("id")
    private String id;

    @JsonProperty("value")
    private String value;

    public MetadataEmnbedded() {
    }

    public MetadataEmnbedded(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
