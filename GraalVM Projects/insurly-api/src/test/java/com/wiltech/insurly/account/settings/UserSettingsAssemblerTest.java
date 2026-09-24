package com.wiltech.insurly.account.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wiltech.insurly.settings.SupportedCurrency;
import com.wiltech.insurly.settings.SupportedLanguage;
import com.wiltech.insurly.settings.SystemSettingsResource;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

class UserSettingsAssemblerTest {

    private static final Instant NOW = Instant.parse("2026-09-12T00:00:00Z");

    private static final SystemSettingsResource PLATFORM = SystemSettingsResource.builder()
            .timezone("America/Sao_Paulo")
            .language(SupportedLanguage.PT_BR)
            .currency(SupportedCurrency.BRL)
            .build();

    private UserSettingsAssembler assembler;

    @BeforeEach
    void setUp() {
        final UserSettingsLinkProvider links = mock(UserSettingsLinkProvider.class);
        when(links.generateSelfLink(any())).thenReturn(Link.of("/api/users/me/settings", "self"));
        when(links.generateUpdateLink(any())).thenReturn(Link.of("/api/users/me/settings", "updateUserSettings"));
        assembler = new UserSettingsAssembler(links);
    }

    @Test
    void userWhoNeverSavedGetsThePlatformValues() {
        final UserSettingsResource resource = assembler.convertToDTO(UserSettings.empty(UUID.randomUUID(), NOW), PLATFORM);

        assertThat(resource.getTimezone()).isEqualTo("America/Sao_Paulo");
        assertThat(resource.getLanguage()).isEqualTo(SupportedLanguage.PT_BR);
        assertThat(resource.getCurrency()).isEqualTo(SupportedCurrency.BRL);
    }

    @Test
    void savedValuesWinOverThePlatform() {
        final UserSettings saved = UserSettings.builder()
                .userId(UUID.randomUUID())
                .timezone("Europe/London")
                .language(SupportedLanguage.EN_GB)
                .currency(SupportedCurrency.GBP)
                .updatedAt(NOW)
                .build();

        final UserSettingsResource resource = assembler.convertToDTO(saved, PLATFORM);

        assertThat(resource.getTimezone()).isEqualTo("Europe/London");
        assertThat(resource.getLanguage()).isEqualTo(SupportedLanguage.EN_GB);
        assertThat(resource.getCurrency()).isEqualTo(SupportedCurrency.GBP);
    }

    @Test
    void eachUnsavedColumnIsFilledIndependently() {
        final UserSettings partial = UserSettings.builder()
                .userId(UUID.randomUUID())
                .language(SupportedLanguage.EL)
                .updatedAt(NOW)
                .build();

        final UserSettingsResource resource = assembler.convertToDTO(partial, PLATFORM);

        assertThat(resource.getLanguage()).isEqualTo(SupportedLanguage.EL);
        assertThat(resource.getTimezone()).isEqualTo("America/Sao_Paulo");
        assertThat(resource.getCurrency()).isEqualTo(SupportedCurrency.BRL);
    }
}
