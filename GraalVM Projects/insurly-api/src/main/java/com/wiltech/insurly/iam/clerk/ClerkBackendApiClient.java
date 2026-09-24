package com.wiltech.insurly.iam.clerk;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Thin client for the parts of Clerk's Backend API this app calls server-to-server
 * (not to be confused with the frontend's Clerk JS SDK). Auth is the Clerk
 * {@code secret key} — never exposed to the browser.
 */
@Component
public class ClerkBackendApiClient {

    private static final String USERS_METADATA_URL = "https://api.clerk.com/v1/users/{userId}/metadata";

    private final RestClient restClient;

    public ClerkBackendApiClient(@Value("${app.clerk.secret-key}") final String secretKey) {
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                .build();
    }

    /**
     * {@code PATCH /v1/users/{id}/metadata} — deep-merges the given fields into
     * the user's existing {@code public_metadata} rather than replacing it.
     */
    public void mergePublicMetadata(final String clerkUserId, final Map<String, Object> publicMetadata) {
        restClient.patch()
                .uri(USERS_METADATA_URL, clerkUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("public_metadata", publicMetadata))
                .retrieve()
                .toBodilessEntity();
    }
}
