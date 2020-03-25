package com.wiltech.auth.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.wiltech.auth.service.model.User;

public interface UserDetailRepository extends JpaRepository<User,Integer> {


    Optional<User> findByUsername(String name);

}
