package com.wiltech.insurly.account.licenses;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Column-level encryption for licence numbers: AES-256-GCM, key from the
 * {@code LICENSE_ENC_KEY} env var (base64, 32 bytes). A random 12-byte IV is
 * generated per write and stored as {@code [iv][ciphertext+tag]}.
 *
 * <p>Instantiated by Hibernate (not Spring), so the key is read from the
 * environment directly. A well-known development key is used when the env var is
 * absent, and a warning is logged — production MUST set a real key.
 *
 * <p>See docs/05-security-and-accounts.md &sect;7.1.
 */
@Converter
public class LicenseNumberConverter implements AttributeConverter<String, byte[]> {

    private static final Logger log = LoggerFactory.getLogger(LicenseNumberConverter.class);

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;

    /** Dev fallback only — 32 bytes base64. Overridden by LICENSE_ENC_KEY in every real environment. */
    private static final String DEV_KEY_B64 = "aW5zdXJseS1kZXYtb25seS1saWNlbnNlLWtleS0zMmI=";

    private final SecretKeySpec key = resolveKey();
    private final SecureRandom random = new SecureRandom();

    @Override
    public byte[] convertToDatabaseColumn(final String plaintext) {
        if (plaintext == null) {
            return null;
        }
        try {
            final byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            final byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to encrypt licence number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(final byte[] stored) {
        if (stored == null) {
            return null;
        }
        try {
            final ByteBuffer buffer = ByteBuffer.wrap(stored);
            final byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            final byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to decrypt licence number", e);
        }
    }

    private static SecretKeySpec resolveKey() {
        String b64 = System.getenv("LICENSE_ENC_KEY");
        if (b64 == null || b64.isBlank()) {
            b64 = System.getProperty("LICENSE_ENC_KEY");
        }
        if (b64 == null || b64.isBlank()) {
            log.warn("LICENSE_ENC_KEY not set — using the built-in development key. "
                    + "Set a real 32-byte base64 key before deploying.");
            b64 = DEV_KEY_B64;
        }
        final byte[] keyBytes = Base64.getDecoder().decode(b64);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("LICENSE_ENC_KEY must decode to exactly 32 bytes (got " + keyBytes.length + ")");
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
