package com.wiltech.insurly.account.cars;

import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import com.wiltech.insurly.quote.PrimaryUse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserCarMetaFabricator {

    public Map<String, Metadata> forTemplate() {
        return baseMeta();
    }

    public Map<String, Metadata> forSingle() {
        return baseMeta();
    }

    public Map<String, Metadata> forCollection() {
        return baseMeta();
    }

    private Map<String, Metadata> baseMeta() {
        final Map<String, Metadata> meta = new HashMap<>();
        meta.put("id", Metadata.builder().hidden(true).readOnly(true).build());
        meta.put("make", Metadata.builder().mandatory(true).build());
        meta.put("model", Metadata.builder().mandatory(true).build());
        meta.put("year", Metadata.builder().mandatory(true).build());
        meta.put("primaryUse", Metadata.builder()
                .mandatory(true)
                .values(primaryUseValues())
                .build());
        return meta;
    }

    private List<MetadataEmnbedded> primaryUseValues() {
        return PrimaryUse.stream()
                .map(value -> new MetadataEmnbedded(value.name(), value.getDescription()))
                .toList();
    }
}
