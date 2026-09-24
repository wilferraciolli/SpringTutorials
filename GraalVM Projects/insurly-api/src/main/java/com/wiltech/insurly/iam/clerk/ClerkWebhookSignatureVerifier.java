package com.wiltech.insurly.iam.clerk;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Verifies the {@code svix-id} / {@code svix-timestamp} / {@code svix-signature}
 * headers Clerk sends with every webhook request, per the Standard Webhooks
 * spec Svix implements: {@code HMAC-SHA256(secret, "{id}.{timestamp}.{body}")},
 * base64-encoded, compared against the {@code v1,<signature>} entries in the
 * header. See https://docs.svix.com/receiving/verifying-payloads/how-manual.
 */
@Component
public class ClerkWebhookSignatureVerifier {

    private static final String SECRET_PREFIX = "whsec_";
    private static final String SIGNATURE_VERSION_PREFIX = "v1,";
    private static final Duration TOLERANCE = Duration.ofMinutes(5);

    private final byte[] secretKey;

    public ClerkWebhookSignatureVerifier(@Value("${app.clerk.webhook-signing-secret}") final String signingSecret) {
        final String encoded = signingSecret.startsWith(SECRET_PREFIX)
                ? signingSecret.substring(SECRET_PREFIX.length())
                : signingSecret;
        this.secretKey = Base64.getDecoder().decode(encoded);
    }

    public boolean verify(
            final String svixId,
            final String svixTimestamp,
            final String svixSignature,
            final String rawBody) {
        if (svixId == null || svixTimestamp == null || svixSignature == null || !withinTolerance(svixTimestamp)) {
            return false;
        }

        final byte[] expected = hmacSha256(svixId + "." + svixTimestamp + "." + rawBody)
                .getBytes(StandardCharsets.UTF_8);

        return Arrays.stream(svixSignature.strip().split(" +"))
                .map(this::stripVersionPrefix)
                .anyMatch(candidate -> MessageDigest.isEqual(candidate.getBytes(StandardCharsets.UTF_8), expected));
    }

    private String stripVersionPrefix(final String signature) {
        return signature.startsWith(SIGNATURE_VERSION_PREFIX)
                ? signature.substring(SIGNATURE_VERSION_PREFIX.length())
                : signature;
    }

    private boolean withinTolerance(final String svixTimestamp) {
        try {
            final Instant timestamp = Instant.ofEpochSecond(Long.parseLong(svixTimestamp));
            final Instant now = Instant.now();
            return !timestamp.isBefore(now.minus(TOLERANCE)) && !timestamp.isAfter(now.plus(TOLERANCE));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String hmacSha256(final String content) {
        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to compute Clerk webhook signature", e);
        }
    }
}
