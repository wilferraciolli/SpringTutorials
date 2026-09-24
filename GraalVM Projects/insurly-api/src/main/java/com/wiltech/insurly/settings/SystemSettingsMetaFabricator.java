package com.wiltech.insurly.settings;

import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the {@code _metadata} block for the admin settings screen: the
 * selectable timezone / language / currency options, so the UI dropdowns are
 * driven by the API rather than a hand-maintained copy.
 */
@Service
public class SystemSettingsMetaFabricator {

    /**
     * A curated shortlist for the admin timezone dropdown. Any valid IANA zone
     * is still accepted on save — this is just the convenient set.
     */
    public static final List<String> TIMEZONE_CHOICES = List.of(
            "UTC",
            "Europe/London", "Europe/Dublin", "Europe/Lisbon", "Europe/Paris",
            "Europe/Madrid", "Europe/Berlin", "Europe/Rome", "Europe/Amsterdam",
            "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles",
            "America/Sao_Paulo", "America/Bahia", "America/Manaus", "America/Toronto",
            "America/Mexico_City", "America/Bogota", "America/Buenos_Aires",
            "Africa/Johannesburg", "Asia/Dubai", "Asia/Kolkata", "Asia/Singapore",
            "Asia/Tokyo", "Australia/Sydney", "Pacific/Auckland");

    public Map<String, Metadata> createMeta() {
        final Map<String, Metadata> metadata = new HashMap<>();

        metadata.put("timezone", Metadata.builder()
                .mandatory(true)
                .values(TIMEZONE_CHOICES.stream()
                        .map(zone -> new MetadataEmnbedded(zone, zone.replace('_', ' ')))
                        .toList())
                .build());

        metadata.put("language", Metadata.builder()
                .mandatory(true)
                .values(SupportedLanguage.stream()
                        .map(language -> new MetadataEmnbedded(language.name(), language.getDisplayName()))
                        .toList())
                .build());

        metadata.put("currency", Metadata.builder()
                .mandatory(true)
                .values(SupportedCurrency.stream()
                        .map(currency -> new MetadataEmnbedded(
                                currency.name(),
                                currency.getDisplayName() + " (" + currency.getSymbol() + ")"))
                        .toList())
                .build());

        metadata.put("updatedAt", Metadata.builder()
                .readOnly(true)
                .build());

        return metadata;
    }
}
