package com.wiltech.insurly.admin.providers;


import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProviderMetaFabricator {
    public Map<String, Metadata> createMetaForTemplate() {
        return buildBasicMeta();
    }

    public Map<String, Metadata> createMetaForSingleResource() {
        return buildBasicMeta();
    }

    public Map<String, Metadata> createMetaForCollectionResource() {
        return buildCollectionMeta();
    }

    private Map<String, Metadata> buildCollectionMeta() {
        Map<String, Metadata> metadata = new HashMap<>();

        metadata.put("id", Metadata.builder()
                .hidden(true)
                .readOnly(true)
                .build());

        metadata.put("name", Metadata.builder()
                .mandatory(true)
                .build());

        metadata.put("email", Metadata.builder()
                .mandatory(true)
                .build());

        metadata.put("providerTypeId", Metadata.builder()
                .mandatory(true)
                .values(generateProviderTypeEmbedded())
                .build());

        return metadata;
    }

    private Map<String, Metadata> buildBasicMeta() {
        Map<String, Metadata> metadata = new HashMap<>();

        metadata.put("id", Metadata.builder()
                .hidden(true)
                .readOnly(true)
                .build());

        metadata.put("name", Metadata.builder()
                .mandatory(true)
                .build());

                metadata.put("email", Metadata.builder()
                .mandatory(true)
                .build());

        metadata.put("userAddressTypeId", Metadata.builder()
                .mandatory(true)
                .values(generateProviderTypeEmbedded())
                .build());

        return metadata;
    }

    private List<MetadataEmnbedded> generateProviderTypeEmbedded() {
        return ProviderType.stream()
                .map(value -> new MetadataEmnbedded(value.name(), value.getDescription()))
                .collect(Collectors.toList());
    }
}
