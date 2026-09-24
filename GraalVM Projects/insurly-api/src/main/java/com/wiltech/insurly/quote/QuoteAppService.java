package com.wiltech.insurly.quote;

import com.wiltech.insurly.admin.providers.Provider;
import com.wiltech.insurly.admin.providers.ProviderRepository;
import com.wiltech.insurly.email.QuoteEmailService;
import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import com.wiltech.insurly.quote.rules.CarQuotePricing;
import com.wiltech.insurly.quote.rules.PricingEngine;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuoteAppService {

    private static final Logger log = LoggerFactory.getLogger(QuoteAppService.class);
    private static final Duration QUOTE_VALIDITY = Duration.ofDays(30);

    private QuoteRepository repository;
    private QuoteAssembler assembler;
    private PricingEngine pricingEngine;
    private Clock clock;
    private QuoteEmailService quoteEmailService;
    private ProviderRepository providerRepository;

    @Transactional(readOnly = true)
    public QuoteResource createTemplate() {
        return QuoteResource.builder()
                .id(null)
                .status(QuoteStatus.DRAFT)
                .driver(DriverResource.builder()
                        .firstName("")
                        .lastName("")
                        .dateOfBirth(null)
                        .yearsLicensed(0)
                        .licenseStatus(LicenseStatus.VALID)
                        .build())
                .vehicle(VehicleResource.builder()
                        .make("")
                        .model("")
                        .year(null)
                        .primaryUse(PrimaryUse.COMMUTE)
                        .build())
                .history(DrivingHistoryResource.builder()
                        .accidentsLast5Years(0)
                        .violationsLast5Years(0)
                        .build())
                .coverage(CoverageResource.builder()
                        .liabilityLimit("")
                        .collision(false)
                        .comprehensive(false)
                        .deductible(BigDecimal.ZERO)
                        .build())
                .build();
    }

    @Transactional
    public QuoteResource create(@Valid QuoteResource payload) {
        return priceAndSave(payload, null);
    }

    /** Price and persist a quote already attached to an account (admin-on-behalf). */
    @Transactional
    public QuoteResource createForUser(@Valid QuoteResource payload, UUID userId) {
        return priceAndSave(payload, userId);
    }

    private QuoteResource priceAndSave(QuoteResource payload, UUID userId) {
        CarQuotePricing pricing = priceFrom(payload);
        Instant now = clock.instant();

        Quote entity = Quote.builder()
                .status(QuoteStatus.COMPLETE)
                .guestToken(UUID.randomUUID())
                .userId(userId)
                .providerId(assignProvider(payload))
                .premiumBasic(pricing.basic())
                .premiumStandard(pricing.standard())
                .premiumPremium(pricing.premium())
                .createdAt(now)
                .expiresAt(now.plus(QUOTE_VALIDITY))
                .build();
        repository.save(entity);

        notifyAdmins(payload, entity);

        return assembler.convertToDTO(entity);
    }

    /** Fire-and-forget: an email failure must never fail the quote. */
    private void notifyAdmins(QuoteResource payload, Quote entity) {
        try {
            quoteEmailService.notifyAdminsOfNewQuote(payload, entity);
        } catch (RuntimeException ex) {
            log.warn("Admin new-quote email failed for quote {}", entity.getId(), ex);
        }
    }

    @Transactional(readOnly = true)
    public List<QuoteResource> findAll() {
        return repository.findAll().stream()
                .map(assembler::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuoteResource findById(final UUID id) {
        final Quote entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find quote entity for given id"));

        return assembler.convertToDTO(entity);
    }

    @Transactional
    public QuoteResource update(UUID id, @Valid QuoteResource payload) {
        final Quote entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find quote entity for given id"));

        CarQuotePricing pricing = priceFrom(payload);
        entity.updateValues(
                QuoteStatus.COMPLETE,
                pricing.basic(),
                pricing.standard(),
                pricing.premium(),
                clock.instant().plus(QUOTE_VALIDITY));
        repository.save(entity);

        return assembler.convertToDTO(entity);
    }

    @Transactional
    public void deleteById(UUID id) {
        final Quote entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find quote entity for given id"));

        repository.delete(entity);
    }

    private CarQuotePricing priceFrom(QuoteResource payload) {
        return pricingEngine.priceCarQuote(assembler.convertToRiskProfile(payload, LocalDate.now(clock)));
    }

    /**
     * Deterministically routes a quote to one of the mock providers, keyed on the
     * vehicle make/model — same vehicle always lands on the same provider, no RNG.
     */
    private UUID assignProvider(QuoteResource payload) {
        final List<Provider> providers = providerRepository.findAllByOrderByIdAsc();
        if (providers.isEmpty()) {
            return null;
        }
        final String key = payload.getVehicle().getMake() + payload.getVehicle().getModel();
        return providers.get(Math.floorMod(key.hashCode(), providers.size())).getId();
    }
}
