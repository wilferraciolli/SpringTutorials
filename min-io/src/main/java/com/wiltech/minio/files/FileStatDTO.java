package com.wiltech.minio.files;

import java.time.ZonedDateTime;
import java.util.Map;

public record FileStatDTO(
        String contentType,
        long size,
        ZonedDateTime lastModifiedDateTime,
        Map<String, String> meta
) {
}
