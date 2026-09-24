package com.wiltech.insurly.account.addresses;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId);

    List<UserAddress> findByUserIdAndPrimaryTrue(UUID userId);

    long countByUserId(UUID userId);
}