package com.wiltech.insurly.admin.profile;


import com.wiltech.insurly.libraries.rest.Metadata;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminProfileMetaFabricator {
    public Map<String, Metadata> createMeta() {
        Map<String, Metadata> metadata = new HashMap<>();

        metadata.put("id", Metadata.builder()
                .hidden(true)
                .readOnly(true)
                .build());

        metadata.put("firstName", Metadata.builder()
                .mandatory(true)
                .build());

        metadata.put("firstName", Metadata.builder()
                .mandatory(true)
                .build());

        metadata.put("email", Metadata.builder()
                .mandatory(true)
                .build());

        return metadata;
    }
}
