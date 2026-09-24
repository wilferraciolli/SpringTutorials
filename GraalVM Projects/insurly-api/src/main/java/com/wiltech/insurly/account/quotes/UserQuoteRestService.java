package com.wiltech.insurly.account.quotes;

import com.wiltech.insurly.account.identity.UserAccessService;
import com.wiltech.insurly.libraries.rest.BaseRestService;
import com.wiltech.insurly.quote.QuoteAppService;
import com.wiltech.insurly.quote.QuoteResource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code /api/users/{userId}/quotes} — a user's quotes (read-only), the
 * guest-quote claim flow, a prefill helper for the wizard, and generating a
 * quote on the user's behalf. {@code userId} is either the literal
 * {@code "me"} or another user's id; {@link UserAccessService} allows the
 * request through only when the caller is that user or an admin.
 */
@RestController
@RequestMapping(value = "/users/{userId}/quotes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserQuoteRestService extends BaseRestService {

    private final UserQuoteAppService userQuoteAppService;
    private final QuotePrefillAppService prefillAppService;
    private final QuoteAppService quoteAppService;
    private final UserAccessService userAccess;

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("")
    public ResponseEntity<QuoteResource> findAll(@PathVariable final String userId) {
        final List<QuoteResource> resources = userQuoteAppService.findForUser(userAccess.resolve(userId));
        return buildResponseOk(getJsonRootName(QuoteResource.class), resources);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/template")
    public ResponseEntity<QuoteResource> template(@PathVariable final String userId) {
        return buildResponseOk(getJsonRootName(QuoteResource.class), quoteAppService.createTemplate());
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/prefill")
    public ResponseEntity<QuoteResource> prefill(@PathVariable final String userId) {
        return buildResponseOk(getJsonRootName(QuoteResource.class), prefillAppService.forUser(userAccess.resolve(userId)));
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @GetMapping("/{id}")
    public ResponseEntity<QuoteResource> findById(@PathVariable final String userId, @PathVariable final UUID id) {
        final QuoteResource resource = userQuoteAppService.findForUser(userAccess.resolve(userId), id);
        return buildResponseOk(getJsonRootName(QuoteResource.class), resource);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PostMapping("")
    public ResponseEntity<QuoteResource> create(
            @PathVariable final String userId, @RequestBody @Valid final QuoteResource payload) {
        final QuoteResource created = quoteAppService.createForUser(payload, userAccess.resolve(userId));
        return buildResponseCreated(getJsonRootName(QuoteResource.class), created, null, null);
    }

    @PreAuthorize("@userAccess.isSelfOrAdmin(#userId)")
    @PostMapping("/claim")
    public ResponseEntity<QuoteResource> claim(
            @PathVariable final String userId, @RequestBody @Valid final ClaimQuoteRequest request) {
        final QuoteResource resource = userQuoteAppService.claim(userAccess.resolve(userId), request);
        return buildResponseOk(getJsonRootName(QuoteResource.class), resource);
    }
}
