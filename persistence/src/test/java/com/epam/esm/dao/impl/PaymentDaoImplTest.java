package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.PaymentDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Payment;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentDaoImplTest {
    public static final String PAYMENTS_TABLE = "module_4.payments";
    public static final String USER_ORDER_TABLE = "module_4.orders";
    private final PaymentDao paymentDao;
    private final GiftCertificateDao giftCertificateDao;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PaymentDaoImplTest(PaymentDao paymentDao, GiftCertificateDao giftCertificateDao,
                              JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.paymentDao = paymentDao;
        this.giftCertificateDao = giftCertificateDao;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Test
    void testPaymentTableRowQuantity() {
        int actual = JdbcTestUtils.countRowsInTable(jdbcTemplate, PAYMENTS_TABLE);
        int expected = 5;
        assertEquals(expected, actual);
    }

    @Test
    void testUserOrderTableRowQuantity() {
        int actual = JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_ORDER_TABLE);
        int expected = 9;
        assertEquals(expected, actual);
    }

    @Test
    void testCreatePayment() {
        User user = new User(1, "Ivan", "Pupkin");
        GiftCertificate giftCertificate =
                new GiftCertificate.GiftCertificateBuilder().setGiftCertificateId(4).createGiftCertificate();
        UserOrder order1 = new UserOrder.UserOrderBuilder()
                .setCost(new BigDecimal("20"))
                .setGiftCertificate(giftCertificate)
                .createUserOrder();
        UserOrder order2 = new UserOrder.UserOrderBuilder()
                .setCost(new BigDecimal("30"))
                .setGiftCertificate(giftCertificate)
                .createUserOrder();
        Payment payment = new Payment.PaymentBuilder()
                .setCreatedDate(LocalDateTime.now())
                .setUser(user)
                .setUserOrder(List.of(order1, order2))
                .createPayment();

        paymentDao.createPayment(payment);
        entityManager.flush();

        assertEquals(6, payment.getPaymentId());
        assertEquals(10, order1.getOrderId());
        assertEquals(11, order2.getOrderId());
        assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, PAYMENTS_TABLE));
        assertEquals(11, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_ORDER_TABLE));
    }

    @Test
    void testReadPayment() {
        GiftCertificate certificate = new GiftCertificate.GiftCertificateBuilder()
                .setGiftCertificateId(4)
                .setName("Evroopt")
                .setDescription("Buy two bananas")
                .setPrice(new BigDecimal("20.00"))
                .setDuration(10)
                .setCreateDate(LocalDateTime.of(2022, 4, 10, 13, 48, 14, 0))
                .setLastUpdateDate(LocalDateTime.of(2022, 4, 10, 13, 48, 14, 0))
                .createGiftCertificate();


        Payment expected = new Payment.PaymentBuilder()
                .setPaymentId(4)
                .setCreatedDate(LocalDateTime.of(2022, 5, 26, 22, 25, 17, 0))
                .setUser(new User(2, "Sanek", "Lupkin"))
                .createPayment();

        UserOrder order1 = new UserOrder.UserOrderBuilder()
                .setUserOrderId(6)
                .setCost(new BigDecimal("20.00"))
                .setGiftCertificate(certificate)
                .setPayment(expected)
                .createUserOrder();

        UserOrder order2 = new UserOrder.UserOrderBuilder()
                .setUserOrderId(7)
                .setCost(new BigDecimal("20.00"))
                .setGiftCertificate(certificate)
                .setPayment(expected)
                .createUserOrder();

        Payment actual = paymentDao.readPayment(4).get();

        assertEquals(expected, actual);
        assertEquals(List.of(order1, order2), actual.getOrders());
    }

    @Test
    void testNonExistOrder() {
        assertTrue(paymentDao.readPayment(50).isEmpty());
    }

    @Test
    void testReadUserOrderByPaymentId() {
        List<UserOrder> expected = createUserOrderList();
        List<UserOrder> actual = paymentDao.readUserOrderByPaymentId(2, 0, 4);

        assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("payment", "giftCertificate")
                .isEqualTo(expected);
        assertThat(actual.get(0).getGiftCertificate().getName()).isEqualTo("Belvest");
        assertThat(actual.get(1).getPayment().getCreatedDate())
                .isEqualTo(LocalDateTime.of(2022, 5, 27, 22, 25, 17,0));
    }

    @Test
    void testReadUserOrders() {
        List<Payment> expected = createPaymentList();
        List<Payment> actual = paymentDao.readPaymentByUserId(1, 0, 3);

        assertEquals(expected, actual);
    }

    @Test
    void testNoUserOrders() {
        List<Payment> actual = paymentDao.readPaymentByUserId(5, 0 , 3);

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    void testOrderWithDeletedGiftCertificate() {
        giftCertificateDao.deleteGiftCertificate(4);
        entityManager.flush();

        Payment payment = paymentDao.readPayment(3).get();
        GiftCertificate actual = payment.getOrders().get(0).getGiftCertificate();

        assertNull(actual);
    }

    @Test
    void testCountUserPayments() {
        int actual = paymentDao.countUserPayments(1);

        assertEquals(3, actual);
    }

    @Test
    void testCountNonexistentUserPayments() {
        int actual = paymentDao.countUserPayments(3);

        assertEquals(0, actual);
    }

    @Test
    void testCountPaymentOrders() {
        int actual = paymentDao.countPaymentOrders(4);

        assertEquals(2, actual);
    }

    @Test
    void testCountNonexistentPaymentOrders() {
        int actual = paymentDao.countPaymentOrders(44);

        assertEquals(0, actual);
    }

    private List<Payment> createPaymentList() {
        User user = new User(1, "Ivan", "Pupkin");
        return List.of(new Payment.PaymentBuilder()
                        .setPaymentId(1)
                        .setCreatedDate(LocalDateTime.of(2022, 5, 26, 22, 25, 17, 0))
                        .setUser(user)
                        .createPayment(),
                new Payment.PaymentBuilder()
                        .setPaymentId(2)
                        .setCreatedDate(LocalDateTime.of(2022, 5, 27, 22, 25, 17, 0))
                        .setUser(user)
                        .createPayment(),
                new Payment.PaymentBuilder()
                        .setPaymentId(3)
                        .setCreatedDate(LocalDateTime.of(2022, 5, 28, 22, 25, 17, 0))
                        .setUser(user)
                        .createPayment()
                );
    }

    private List<UserOrder> createUserOrderList() {
        return List.of(new UserOrder.UserOrderBuilder()
                        .setUserOrderId(3)
                        .setCost(new BigDecimal("5.00"))
                        .createUserOrder(),
                new UserOrder.UserOrderBuilder()
                        .setUserOrderId(4)
                        .setCost(new BigDecimal("20.00"))
                        .createUserOrder());
    }
}