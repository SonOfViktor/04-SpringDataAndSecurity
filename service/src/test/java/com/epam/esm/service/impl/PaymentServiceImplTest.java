package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.PaymentDao;
import com.epam.esm.dao.UserOrderDao;
import com.epam.esm.dto.PaymentDto;
import com.epam.esm.entity.*;
import com.epam.esm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentDao paymentDao;

    @Mock
    private UserDao userDao;

    @Mock
    private GiftCertificateDao giftCertificateDao;

    @Mock
    private UserOrderDao userOrderDao;

    @Test
    void testCreatePayment() {
        User user = new User(1, "Maks", "Silev");

        GiftCertificate giftCertificate = GiftCertificate.builder()
                .giftCertificateId(1)
                .price(new BigDecimal("40.00"))
                .name("Oz.by")
                .build();

        UserOrder order = UserOrder.builder()
                .orderId(1)
                .cost(new BigDecimal("40.00"))
                .giftCertificate(giftCertificate)
                .build();

        Payment payment = Payment.builder()
                .paymentId(1)
                .orders(List.of(order))
                .createdDate(LocalDateTime.of(2022, 5, 29, 13, 49, 0, 0))
                .user(user)
                .build();

        when(userDao.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(giftCertificateDao.findById(1)).thenReturn(Optional.of(giftCertificate));
        when(paymentDao.save(any(Payment.class))).thenReturn(payment);

        PaymentDto expected = new PaymentDto(
                1, "Maks Silev",
                List.of(new PaymentDto.UserOrderDto(1, "Oz.by", new BigDecimal("40.00"))),
                LocalDateTime.of(2022, 5, 29, 13, 49, 0, 0));
        PaymentDto actual = paymentService.addPayment("user@mail.com", List.of(1));

        assertEquals(expected, actual);
    }

    @Test
    void testCreatePaymentWithoutUser() {
        List<Integer> certificateIdList = List.of(1);

        when(userDao.findByEmail("absent@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.addPayment("absent@mail.com", certificateIdList));
    }

    @Test
    void testCreatePaymentWithoutGiftCertificate() {
        List<Integer> certificateIdList = List.of(99);

        when(userDao.findByEmail("username@mail.com")).thenReturn(Optional.of(new User()));
        when(giftCertificateDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.addPayment("username@mail.com", certificateIdList));
    }

    @Test
    void testFindPayment() {
        Payment payment = createPayment();

        when(paymentDao.findById(1)).thenReturn(Optional.of(payment));

        PaymentDto expected = new PaymentDto(
                1, "Maks Silev",
                List.of(new PaymentDto.UserOrderDto(1,"Oz.by", new BigDecimal("40.00"))),
                LocalDateTime.of(2022, 5, 29, 13, 49, 0, 0));
        PaymentDto actual = paymentService.findPayment(1);

        assertEquals(expected, actual);
    }

    @Test
    void testFindNonExistentPayment() {
        when(paymentDao.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.findPayment(10));
    }

    @Test
    void testFindPaymentsByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Payment payment = createPayment();

        when(paymentDao.findByUserId(1, pageable)).thenReturn(new PageImpl<>(List.of(payment), pageable, 1));

        List<PaymentDto> payments = List.of(new PaymentDto(
                1, "Maks Silev",
                List.of(new PaymentDto.UserOrderDto(1,"Oz.by", new BigDecimal("40.00"))),
                LocalDateTime.of(2022, 5, 29, 13, 49, 0, 0)));

        Page<PaymentDto> expected = new PageImpl<>(payments, pageable, 1);
        Page<PaymentDto> actual = paymentService.findPaymentsByUserId(1, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindPaymentsByUserIdNotExistedPage() {
        Pageable pageable = PageRequest.of(100, 10);
        when(paymentDao.findByUserId(1, pageable)).thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.findPaymentsByUserId(1, pageable));
    }

    @Test
    void testFindUserOrderByPaymentId() {
        Pageable pageable = PageRequest.of(0, 10);
        GiftCertificate giftCertificate = GiftCertificate.builder()
                .giftCertificateId(1)
                .name("Oz.by")
                .build();
        UserOrder order = UserOrder.builder()
                .orderId(1)
                .cost(new BigDecimal("40.00"))
                .giftCertificate(giftCertificate)
                .build();

        PaymentDto.UserOrderDto orderDto = new PaymentDto.UserOrderDto(1, "Oz.by", new BigDecimal("40.00"));

        when(userOrderDao.findAllByPaymentId(3, pageable)).thenReturn(new PageImpl<>(List.of(order), pageable, 1));

        Page<PaymentDto.UserOrderDto> expected = new PageImpl<>(List.of(orderDto), pageable, 1);
        Page<PaymentDto.UserOrderDto> actual = paymentService.findUserOrderByPaymentId(3, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindUserOrderByPaymentIdWithDeletedCertificate() {
        Pageable pageable = PageRequest.of(0, 10);
        UserOrder order = UserOrder.builder()
                .orderId(1)
                .cost(new BigDecimal("40.00"))
                .giftCertificate(null)
                .build();

        PaymentDto.UserOrderDto orderDto = new PaymentDto.UserOrderDto(0, "DELETED", new BigDecimal("40.00"));

        when(userOrderDao.findAllByPaymentId(3, pageable)).thenReturn(new PageImpl<>(List.of(order), pageable, 1));

        Page<PaymentDto.UserOrderDto> expected = new PageImpl<>(List.of(orderDto), pageable, 1);
        Page<PaymentDto.UserOrderDto> actual = paymentService.findUserOrderByPaymentId(3, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindUserOrderByPaymentIdNotExistedPage() {
        Pageable pageable = PageRequest.of(100, 10);
        when(userOrderDao.findAllByPaymentId(3, pageable)).thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.findUserOrderByPaymentId(3, pageable));
    }

    private Payment createPayment() {
        User user = new User(1, "Maks", "Silev");
        GiftCertificate giftCertificate = GiftCertificate.builder()
                .giftCertificateId(1)
                .name("Oz.by")
                .build();
        UserOrder order = UserOrder.builder()
                .orderId(1)
                .cost(new BigDecimal("40.00"))
                .giftCertificate(giftCertificate)
                .build();
        Payment payment = Payment.builder()
                .paymentId(1)
                .user(user)
                .createdDate(LocalDateTime.of(2022, 5, 29, 13, 49, 0, 0))
                .build();
        payment.setOrders(List.of(order));

        return payment;
    }
}