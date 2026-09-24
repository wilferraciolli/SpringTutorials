package com.wiltech.insurly.libraries.rest;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * The type Response serializer.
 * This Serializer is to be used to produce responses with the root name of a DTO either being a collection or a single resource.
 */
public class ResponseSerializer extends ValueSerializer<PayloadData> {
    @Override
    public void serialize(final PayloadData value, final JsonGenerator jsonGenerator, final SerializationContext provider)
            throws JacksonException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writePOJOProperty(workOutRootName(value), workOutData(value));
        jsonGenerator.writeEndObject();
    }

    private Object workOutData(final PayloadData value) {
        return value.getData();
    }

    private String workOutRootName(final PayloadData value) {
        return value.getRootName();
    }
}
