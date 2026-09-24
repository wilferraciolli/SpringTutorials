package com.wiltech.insurly.account.licenses;

import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import com.wiltech.insurly.quote.LicenseStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserLicenseMetaFabricator {

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
        meta.put("licenseNumber", Metadata.builder().mandatory(true).build());
        meta.put("status", Metadata.builder()
                .mandatory(true)
                .values(statusValues())
                .build());
        return meta;
    }

    private List<MetadataEmnbedded> statusValues() {
        return LicenseStatus.stream()
                .map(value -> new MetadataEmnbedded(value.name(), value.getDescription()))
                .toList();
    }
}
