package com.wiltech.insurly.admin.profile;

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

@RestController()
@RequestMapping(value = "/admin/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminProfileRestService extends BaseRestService {

    private final AdminProfileAppService appService;
    private final AdminProfileMetaFabricator metaFabricator;

    @GetMapping("")
    public ResponseEntity<AdminProfileResource> getProfile() {
        final AdminProfileResource resource = appService.getProfile();
        Map<String, Metadata> meta = metaFabricator.createMeta();

        return buildResponseOk(getJsonRootName(AdminProfileResource.class), resource, meta);
    }

    @PutMapping("")
    public ResponseEntity<AdminProfileResource> update(@RequestBody @Valid final AdminProfileResource payload) {
        final AdminProfileResource updatedResource = appService.update(payload);
        Map<String, Metadata> meta = metaFabricator.createMeta();

        return buildResponseOk(getJsonRootName(AdminProfileResource.class), updatedResource, meta);
    }
}
