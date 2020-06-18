package com.wiltech.springrest.clients;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByName(String name);

    List<Client> findByNameContaining(String name);

    Optional<Client> findByEmail(String email);
}
