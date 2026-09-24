package com.wiltech.insurly.admin.dashboard;

import com.wiltech.insurly.libraries.rest.BaseRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code /api/admin/dashboard} — headline numbers and the expiring / recent quote feeds. */
@RestController
@RequestMapping(value = "/admin/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminDashboardRestService extends BaseRestService {

    private final AdminDashboardAppService appService;

    @GetMapping("")
    public ResponseEntity<AdminDashboardResource> dashboard() {
        return buildResponseOk(getJsonRootName(AdminDashboardResource.class), appService.build());
    }
}
