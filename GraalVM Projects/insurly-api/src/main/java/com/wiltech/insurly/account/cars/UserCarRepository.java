package com.wiltech.insurly.account.cars;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Ownership is a query concern: callers reach rows through
 * {@code *ByUserId(...)}, never a bare {@code findById} followed by a check.
 */
@Repository
public interface UserCarRepository extends JpaRepository<UserCar, UUID> {

    List<UserCar> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserCar> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);
}