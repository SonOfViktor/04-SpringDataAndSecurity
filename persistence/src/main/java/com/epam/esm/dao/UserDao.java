package com.epam.esm.dao;

import com.epam.esm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * The interface provided methods to control data in database table related with users.
 */
public interface UserDao extends JpaRepository<User, Integer> {

    /**
     * Find user with specified email in database
     *
     * @param email the user's email
     * @return optional with user if exist in database or Optional.empty() otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user with email exists in database
     *
     * @param email the email of user
     * @return true if user with specified email exists in database
     */
    boolean existsByEmail(String email);
}
