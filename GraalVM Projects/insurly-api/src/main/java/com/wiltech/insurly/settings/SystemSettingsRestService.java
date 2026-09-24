package com.wiltech.insurly.settings;

import com.wiltech.insurly.libraries.rest.BaseRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code /api/system-settings} — public read of the platform's timezone,
 * language and currency. The frontend calls this on load to format amounts and
 * dates consistently; no authentication required.
 */
@RestController
@RequestMapping(value = "/system-settings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SystemSettingsRestService extends BaseRestService {

    private final SystemSettingsAppService appService;

    @GetMapping("")
    public ResponseEntity<SystemSettingsResource> get() {
        return buildResponseOk(
                getJsonRootName(SystemSettingsResource.class),
                appService.getSettings(false));
    }
}
