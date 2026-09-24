package com.wiltech.insurly.settings;

import com.wiltech.insurly.libraries.rest.BaseRestService;
import com.wiltech.insurly.libraries.rest.Metadata;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * {@code /api/admin/system-settings} — read and update the single global
 * settings row. Gated on the ADMIN role by {@code AdminGuardInterceptor}; the
 * response also carries the selectable timezone / language / currency lists in
 * {@code _metadata}.
 */
@RestController
@RequestMapping(value = "/admin/system-settings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminSystemSettingsRestService extends BaseRestService {

    private final SystemSettingsAppService appService;
    private final SystemSettingsMetaFabricator metaFabricator;

    @GetMapping("")
    public ResponseEntity<SystemSettingsResource> get() {
        final Map<String, Metadata> metadata = metaFabricator.createMeta();

        return buildResponseOk(
                getJsonRootName(SystemSettingsResource.class),
                appService.getSettings(true),
                metadata);
    }

    @PutMapping("")
    public ResponseEntity<SystemSettingsResource> update(
            @RequestBody @Valid final SystemSettingsResource payload) {
        final Map<String, Metadata> metadata = metaFabricator.createMeta();

        return buildResponseOk(
                getJsonRootName(SystemSettingsResource.class),
                appService.updateSettings(payload),
                metadata);
    }
}
