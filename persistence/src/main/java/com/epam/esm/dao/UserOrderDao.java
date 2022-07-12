package com.epam.esm.dao;

import com.epam.esm.entity.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The interface provided methods to control data in database table related with user orders.
 */
public interface UserOrderDao extends JpaRepository<UserOrder, Integer> {
    String SELECT_ORDERS_BY_PAYMENT_ID_HQL = """
            select o from UserOrder o
            join o.payment p
            where p.paymentId = :id
            """;

    /**
     * Read orders made by user with specified id
     *
     * @param id the id of a user
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return the page with orders mady by user with specified id
     */
    @Query(SELECT_ORDERS_BY_PAYMENT_ID_HQL)
    Page<UserOrder> findAllByPaymentId(int id, Pageable pageable);
}
