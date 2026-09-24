package com.wiltech.insurly.iam.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The subset of a Clerk webhook payload this app cares about. Clerk sends many
 * more fields per event type — everything else is ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClerkWebhookEvent(String type, Data data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(String id) {
    }
}
