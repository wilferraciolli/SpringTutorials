package com.wiltech.insurly.account.licenses;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLicenseRepository extends JpaRepository<UserLicense, UUID> {

    List<UserLicense> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserLicense> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);
}