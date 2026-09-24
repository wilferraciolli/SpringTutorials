package com.wiltech.insurly.account.quotes;

import com.wiltech.insurly.exceptions.DomainException;
import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteRepository;
import com.wiltech.insurly.quote.QuoteResource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read-only view of the signed-in user's quotes, plus the guest-quote claim flow. */
@Service
@RequiredArgsConstructor
public class UserQuoteAppService {

    private final QuoteRepository quoteRepository;
    private final UserQuoteAssembler assembler;

    @Transactional(readOnly = true)
    public List<QuoteResource> findForUser(final UUID ownerId) {
        return quoteRepository.findByUserIdOrderByCreatedAtDesc(ownerId).stream()
                .map(assembler::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuoteResource findForUser(final UUID ownerId, final UUID id) {
        final Quote quote = quoteRepository.findByIdAndUserId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote %s not found".formatted(id)));
        return assembler.toDto(quote);
    }

    /**
     * Attach a quote generated as a guest to this account. Wrong {@code guestToken}
     * &rarr; 404 (so the endpoint can't be used to probe for quote ids). Idempotent
     * when the quote is already mine.
     */
    @Transactional
    public QuoteResource claim(final UUID ownerId, @Valid final ClaimQuoteRequest request) {
        final Quote quote = quoteRepository.findById(request.getQuoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found"));

        if (!quote.getGuestToken().equals(request.getGuestToken())) {
            throw new ResourceNotFoundException("Quote not found");
        }
        if (quote.getUserId() != null && !quote.getUserId().equals(ownerId)) {
            throw new DomainException("This quote is already attached to another account");
        }
        if (quote.getUserId() == null) {
            quote.attachTo(ownerId);
            quoteRepository.save(quote);
        }
        return assembler.toDto(quote);
    }
}
