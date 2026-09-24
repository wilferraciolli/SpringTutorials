package com.wiltech.insurly.iam.clerk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

class ClerkWebhookSignatureVerifierTest {

    private static final String SECRET = "whsec_" + encode("the-real-signing-secret-bytes!!!");
    private static final String WRONG_SECRET = "whsec_" + encode("a-completely-different-secret!!");
    private static final String SVIX_ID = "msg_p5jXN8AQM9LWM0D4loKWxJek";
    private static final String PAYLOAD = "{\"test\": 2432232314}";

    private final ClerkWebhookSignatureVerifier verifier = new ClerkWebhookSignatureVerifier(SECRET);

    @Test
    void acceptsACorrectlySignedRequest() {
        final String timestamp = String.valueOf(Instant.now().getEpochSecond());
        final String signature = "v1," + sign(SECRET, SVIX_ID, timestamp, PAYLOAD);

        assertTrue(verifier.verify(SVIX_ID, timestamp, signature, PAYLOAD));
    }

    @Test
    void acceptsWhenTheMatchingSignatureIsOneOfSeveralSpaceSeparatedEntries() {
        final String timestamp = String.valueOf(Instant.now().getEpochSecond());
        final String signature = "v1,not-the-right-one v1," + sign(SECRET, SVIX_ID, timestamp, PAYLOAD);

        assertTrue(verifier.verify(SVIX_ID, timestamp, signature, PAYLOAD));
    }

    @Test
    void rejectsATamperedPayload() {
        final String timestamp = String.valueOf(Instant.now().getEpochSecond());
        final String signature = "v1," + sign(SECRET, SVIX_ID, timestamp, PAYLOAD);

        assertFalse(verifier.verify(SVIX_ID, timestamp, signature, "{\"test\": 1}"));
    }

    @Test
    void rejectsASignatureFromTheWrongSecret() {
        final String timestamp = String.valueOf(Instant.now().getEpochSecond());
        final String signature = "v1," + sign(WRONG_SECRET, SVIX_ID, timestamp, PAYLOAD);

        assertFalse(verifier.verify(SVIX_ID, timestamp, signature, PAYLOAD));
    }

    @Test
    void rejectsAStaleTimestamp() {
        final String timestamp = String.valueOf(Instant.now().minusSeconds(600).getEpochSecond());
        final String signature = "v1," + sign(SECRET, SVIX_ID, timestamp, PAYLOAD);

        assertFalse(verifier.verify(SVIX_ID, timestamp, signature, PAYLOAD));
    }

    @Test
    void rejectsMissingHeaders() {
        final String timestamp = String.valueOf(Instant.now().getEpochSecond());

        assertFalse(verifier.verify(null, timestamp, "v1,abc", PAYLOAD));
        assertFalse(verifier.verify(SVIX_ID, timestamp, null, PAYLOAD));
    }

    private static String encode(final String raw) {
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private static String sign(final String secret, final String svixId, final String timestamp, final String payload) {
        try {
            final String encodedSecret = secret.startsWith("whsec_") ? secret.substring("whsec_".length()) : secret;
            final byte[] key = Base64.getDecoder().decode(encodedSecret);
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            final String content = svixId + "." + timestamp + "." + payload;
            return Base64.getEncoder().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
