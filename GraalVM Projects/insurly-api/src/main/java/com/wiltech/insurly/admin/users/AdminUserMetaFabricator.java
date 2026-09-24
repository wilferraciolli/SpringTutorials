package com.wiltech.insurly.admin.users;

import com.wiltech.insurly.account.identity.UserOwnership;
import com.wiltech.insurly.account.identity.UserRole;
import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminUserMetaFabricator {

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
        meta.put("email", Metadata.builder().mandatory(true).build());
        meta.put("ownership", Metadata.builder()
                .readOnly(true)
                .values(ownershipValues())
                .build());
        meta.put("roles", Metadata.builder()
                .values(roleValues())
                .build());
        meta.put("createdAt", Metadata.builder().readOnly(true).build());
        return meta;
    }

    private List<MetadataEmnbedded> ownershipValues() {
        return UserOwnership.stream()
                .map(value -> new MetadataEmnbedded(value.name(), value.getDescription()))
                .toList();
    }

    private List<MetadataEmnbedded> roleValues() {
        return UserRole.stream()
                .map(value -> new MetadataEmnbedded(value.name(), value.getDescription()))
                .toList();
    }
}
