package com.wiltech.insurly.settings;

import com.wiltech.insurly.libraries.rest.LinkBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemSettingsLinkProvider {
    private final LinkBuilder linkBuilder;

    public Link generateSelfLink() {
        return linkBuilder.buildSelfLink(SystemSettingsRestService.class, "get");
    }

    /** Only handed back to admin callers — the PUT is gated on the ADMIN role. */
    public Link generateUpdateLink() {
        return linkBuilder.buildLink(
                AdminSystemSettingsRestService.class,
                "update",
                "updateSystemSettings");
    }
}
