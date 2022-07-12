package com.epam.esm.service;

import com.epam.esm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * The interface provided methods to manage business logic related with users.
 */
public interface UserService {
    /**
     * Find specified page with users
     *
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return page with users
     */
    Page<User> findAllUser(Pageable pageable);

    /**
     * Find user with specified email in database
     *
     * @param email the email of a user
     * @return optional with user if exists or empty optional otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Create user in database
     * @param user the user that must be created
     * @return created user
     */
    User createUser(User user);
}
