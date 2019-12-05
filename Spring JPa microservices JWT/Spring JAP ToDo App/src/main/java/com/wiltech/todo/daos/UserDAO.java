/*
 * (c) Midland Software Limited 2019
 * Name     : UserDAO.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.daos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wiltech.todo.entities.User;

/**
 * The interface User dao.
 */
@Repository
public interface UserDAO extends JpaRepository<User, Long> {

    /**
     * Find user by email optional.
     * @param email the email
     * @return the optional
     */
    Optional<User> findUserByEmail(final String email);

//    @Query(value ="SELECT * FROM users WHERE email =:email", nativeQuery = true)
//    Optional<User> findByEmail(String email);


}
