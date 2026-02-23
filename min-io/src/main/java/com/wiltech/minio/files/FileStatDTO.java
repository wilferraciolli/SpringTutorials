package com.wiltech.minio.files;

import java.time.Instant;
import java.util.Map;

public record FileStatDTO(
        String contentType,
        long size,
        Instant lastModifiedDateTime,
        Map<String, String> meta
) {
}
