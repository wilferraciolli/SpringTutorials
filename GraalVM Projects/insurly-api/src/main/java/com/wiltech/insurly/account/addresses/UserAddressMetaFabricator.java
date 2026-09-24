package com.wiltech.insurly.account.addresses;

import com.wiltech.insurly.libraries.rest.Metadata;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserAddressMetaFabricator {

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
        meta.put("label", Metadata.builder().mandatory(true).build());
        meta.put("line1", Metadata.builder().mandatory(true).build());
        meta.put("city", Metadata.builder().mandatory(true).build());
        meta.put("postalCode", Metadata.builder().mandatory(true).build());
        meta.put("countryCode", Metadata.builder().mandatory(true).build());
        return meta;
    }
}
