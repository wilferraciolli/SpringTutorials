package com.wiltech.users;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByUsername(String username);
}
