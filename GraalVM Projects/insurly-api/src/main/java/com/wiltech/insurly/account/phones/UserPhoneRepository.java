package com.wiltech.insurly.account.phones;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhoneRepository extends JpaRepository<UserPhone, UUID> {

    List<UserPhone> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserPhone> findByIdAndUserId(UUID id, UUID userId);

    List<UserPhone> findByUserIdAndPrimaryTrue(UUID userId);

    long countByUserId(UUID userId);
}