package com.wiltech.insurly.iam.clerk;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Reacts to Clerk webhook events. Currently: assign the default role on sign-up. */
@Service
public class ClerkWebhookAppService {

    private static final Logger log = LoggerFactory.getLogger(ClerkWebhookAppService.class);
    private static final String USER_CREATED_EVENT = "user.created";

    private final ClerkBackendApiClient clerkBackendApiClient;
    private final String defaultRole;

    public ClerkWebhookAppService(
            final ClerkBackendApiClient clerkBackendApiClient,
            @Value("${app.clerk.default-role}") final String defaultRole) {
        this.clerkBackendApiClient = clerkBackendApiClient;
        this.defaultRole = defaultRole;
    }

    public void handle(final ClerkWebhookEvent event) {
        if (!USER_CREATED_EVENT.equals(event.type())) {
            return;
        }

        final String clerkUserId = event.data().id();
        log.info("Assigning default role '{}' to new Clerk user {}", defaultRole, clerkUserId);
        clerkBackendApiClient.mergePublicMetadata(clerkUserId, Map.of("roles", List.of(defaultRole)));
    }
}
