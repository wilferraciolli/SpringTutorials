package com.wiltech.insurly.iam.clerk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code POST /api/iam/webhooks/clerk} — receives Clerk's account-lifecycle
 * webhooks (configured in the Clerk Dashboard under Webhooks). Every request is
 * verified via {@link ClerkWebhookSignatureVerifier} before the body is trusted;
 * there is no other auth on this route (Clerk doesn't send a bearer token here),
 * so it's deliberately outside {@code /api/admin/**} and {@code /api/users/**}.
 */
@RestController
@RequestMapping("/iam/webhooks/clerk")
@RequiredArgsConstructor
public class ClerkWebhookRestService {

    private static final Logger log = LoggerFactory.getLogger(ClerkWebhookRestService.class);

    // Not a Spring-managed bean: this app doesn't otherwise expose one (no
    // spring-boot-starter-json), and the webhook payload is small ad hoc JSON
    // that doesn't need the shared HTTP-response converter configuration.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ClerkWebhookSignatureVerifier signatureVerifier;
    private final ClerkWebhookAppService appService;

    @PostMapping
    public ResponseEntity<Void> handle(
            @RequestHeader("svix-id") final String svixId,
            @RequestHeader("svix-timestamp") final String svixTimestamp,
            @RequestHeader("svix-signature") final String svixSignature,
            @RequestBody final String rawBody) throws JsonProcessingException {

        if (!signatureVerifier.verify(svixId, svixTimestamp, svixSignature, rawBody)) {
            log.warn("Rejected a Clerk webhook request with an invalid signature");
            return ResponseEntity.badRequest().build();
        }

        appService.handle(OBJECT_MAPPER.readValue(rawBody, ClerkWebhookEvent.class));
        return ResponseEntity.ok().build();
    }
}
