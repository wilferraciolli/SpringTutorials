package com.wiltech.insurly.account.phones;

import com.wiltech.insurly.libraries.rest.Metadata;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserPhoneMetaFabricator {

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
        meta.put("number", Metadata.builder().mandatory(true).build());
        return meta;
    }
}
