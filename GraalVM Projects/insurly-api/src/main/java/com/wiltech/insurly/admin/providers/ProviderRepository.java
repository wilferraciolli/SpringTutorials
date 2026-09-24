package com.wiltech.insurly.admin.providers;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface  ProviderRepository extends JpaRepository<Provider, UUID> {

    /** Stable ordering for deterministic quote-to-provider assignment (see QuoteAppService). */
    List<Provider> findAllByOrderByIdAsc();
}
