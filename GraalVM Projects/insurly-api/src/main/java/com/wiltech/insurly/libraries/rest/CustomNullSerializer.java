package com.wiltech.insurly.libraries.rest;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Annotation to be added to fields that should be converted to empty strings if null.
 */
public class CustomNullSerializer extends ValueSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext provider) {
        jsonGenerator.writeString("");
    }
}
