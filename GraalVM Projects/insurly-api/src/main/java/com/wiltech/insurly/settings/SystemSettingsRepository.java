package com.wiltech.insurly.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, UUID> {

    /** The platform keeps a single settings row; this returns it if present. */
    default Optional<SystemSettings> findSingleton() {
        return findAll().stream().findFirst();
    }
}
