package com.wiltech.insurly.libraries.rest;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Metadata {
    @JsonProperty("readOnly")
    private Boolean readOnly;

    @JsonProperty("hidden")
    private Boolean hidden;

    @JsonProperty("mandatory")
    private Boolean mandatory;

    @JsonProperty("values")
    private List<MetadataEmnbedded> values;

    public Metadata() {
    }

    public Metadata(final Boolean readOnly, final Boolean hidden, final Boolean mandatory,
                    final List<MetadataEmnbedded> values) {
        this.readOnly = readOnly;
        this.hidden = hidden;
        this.mandatory = mandatory;
        this.values = values;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(final Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(final Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(final Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public List<MetadataEmnbedded> getValues() {
        return values;
    }

    public void setValues(final List<MetadataEmnbedded> values) {
        this.values = values;
    }
}
