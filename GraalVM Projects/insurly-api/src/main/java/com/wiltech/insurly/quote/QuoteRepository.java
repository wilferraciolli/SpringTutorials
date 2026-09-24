package com.wiltech.insurly.quote;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, UUID> {

    List<Quote> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Quote> findByIdAndUserId(UUID id, UUID userId);

    long countByStatus(QuoteStatus status);

    long countByCreatedAtAfter(Instant instant);

    List<Quote> findByExpiresAtBetweenOrderByExpiresAtAsc(Instant from, Instant to);

    List<Quote> findTop10ByOrderByCreatedAtDesc();

    long countByUserId(UUID userId);

    /** Priced quotes still inside their validity window — "open", not yet converted. */
    long countByStatusAndExpiresAtAfter(QuoteStatus status, Instant now);

    List<Quote> findTop50ByStatusAndExpiresAtAfterOrderByExpiresAtAsc(QuoteStatus status, Instant now);

    long countByStatusAndCreatedAtAfter(QuoteStatus status, Instant instant);

    /** For the dashboard trend/by-provider charts — everything created since the window start. */
    List<Quote> findByCreatedAtGreaterThanEqual(Instant since);
}