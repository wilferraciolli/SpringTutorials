package com.wiltech.insurly.account.quotes;

import com.wiltech.insurly.quote.Quote;
import com.wiltech.insurly.quote.QuoteAssembler;
import com.wiltech.insurly.quote.QuoteResource;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Reuses the quote package's own DTO mapping, then adds the account-scoped
 * {@code self} link ({@code /api/users/{userId}/quotes/{id}}).
 */
@Service
@RequiredArgsConstructor
public class UserQuoteAssembler {

    private final QuoteAssembler quoteAssembler;
    private final UserQuoteLinkProvider linkProvider;

    public QuoteResource toDto(final Quote entity) {
        final QuoteResource resource = quoteAssembler.convertToDTO(entity);
        resource.addLinks(List.of(linkProvider.generateSelfLink(entity.getUserId(), entity.getId())));
        return resource;
    }
}
