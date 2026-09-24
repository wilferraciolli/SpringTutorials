package com.wiltech.insurly.quote;

import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class QuoteMetaFabricator {

    public Map<String, Metadata> createMetaForTemplate() {
        return buildBasicMeta();
    }

    public Map<String, Metadata> createMetaForSingleResource() {
        return buildBasicMeta();
    }

    public Map<String, Metadata> createMetaForCollectionResource() {
        return buildBasicMeta();
    }

    private Map<String, Metadata> buildBasicMeta() {
        Map<String, Metadata> metadata = new HashMap<>();

        // computed server-side — never accepted from the caller
        metadata.put("id", readOnlyHidden());
        metadata.put("guestToken", readOnlyHidden());
        metadata.put("status", readOnly());
        metadata.put("premiumBasic", readOnly());
        metadata.put("premiumStandard", readOnly());
        metadata.put("premiumPremium", readOnly());
        metadata.put("createdAt", readOnly());
        metadata.put("expiresAt", readOnly());

        // wizard input
        metadata.put("driver", mandatory());
        metadata.put("vehicle", mandatory());
        metadata.put("history", mandatory());
        metadata.put("coverage", mandatory());

        metadata.put("driver.licenseStatus", Metadata.builder()
                .mandatory(true)
                .values(embeddedValues(LicenseStatus.stream(), LicenseStatus::name, LicenseStatus::getDescription))
                .build());

        metadata.put("vehicle.primaryUse", Metadata.builder()
                .mandatory(true)
                .values(embeddedValues(PrimaryUse.stream(), PrimaryUse::name, PrimaryUse::getDescription))
                .build());

        return metadata;
    }

    private static Metadata readOnlyHidden() {
        return Metadata.builder().hidden(true).readOnly(true).build();
    }

    private static Metadata readOnly() {
        return Metadata.builder().readOnly(true).build();
    }

    private static Metadata mandatory() {
        return Metadata.builder().mandatory(true).build();
    }

    private static <E> List<MetadataEmnbedded> embeddedValues(
            java.util.stream.Stream<E> values,
            Function<E, String> id,
            Function<E, String> label) {
        return values
                .map(value -> new MetadataEmnbedded(id.apply(value), label.apply(value)))
                .toList();
    }
}
