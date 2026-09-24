package com.wiltech.insurly.libraries.rest;

/**
 * The type Payload data.
 */
class PayloadData {
    private final String rootName;
    private final Object data;

    public PayloadData(final String rootName, final Object data) {
        this.rootName = rootName;
        this.data = data;
    }

    public String getRootName() {
        return rootName;
    }

    public Object getData() {
        return data;
    }
}
