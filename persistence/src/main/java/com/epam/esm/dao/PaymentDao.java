package com.epam.esm.dao;

import com.epam.esm.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The interface provided methods to control data in database table related with payments.
 */
public interface PaymentDao extends JpaRepository<Payment, Integer> {
    String SELECT_PAYMENT_BY_USER_ID_HQL = """
            select p from Payment p
            join p.user u
            where u.userId = :id
            """;

    /**
     * Read payments that made user with specified id
     *
     * @param id the id of a user
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return payments that user with specified id made
     */
    @Query(SELECT_PAYMENT_BY_USER_ID_HQL)
    Page<Payment> findByUserId(int id, Pageable pageable);
}
