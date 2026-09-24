package com.wiltech.insurly.account.identity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByAuthSubject(String authSubject);

    List<AppUser> findAllByOrderByCreatedAtDesc();

    long countByOwnership(UserOwnership ownership);

    long countByCreatedAtAfter(Instant instant);
}
