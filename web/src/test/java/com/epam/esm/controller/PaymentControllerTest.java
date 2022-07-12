package com.epam.esm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser
    void testShowPayment() throws Exception {
        mockMvc.perform(get("/payments/{id}", 1))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.userName").value(is("Ivan Pupkin")),
                        jsonPath("$._links.self.href").value(is("http://localhost/payments/1")));
    }

    @Test
    @WithMockUser
    void testShowNotExistedPayment() throws Exception {
        mockMvc.perform(get("/payments/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("There is no payment with id 10 in database")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    @WithMockUser
    void testShowPaymentNotValidId() throws Exception {
        mockMvc.perform(get("/payments/{id}", 0))
                .andDo(log())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("showPayment.paymentId: must be greater than 0")),
                        jsonPath("$.errorCode").value(is(40020)));
    }

    @Test
    @WithAnonymousUser
    void testShowPaymentAnonymousUser() throws Exception {
        mockMvc.perform(get("/payments/{id}", 1))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }

    @Test
    @WithMockUser
    void testShowPaymentOrder() throws Exception {
        mockMvc.perform(get("/payments/{id}/orders", 2))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..userOrderDtoes.size()").value(2),
                        jsonPath("$..userOrderDtoes[*].certificateId")
                                .value(containsInAnyOrder(2, 3)));
    }

    @Test
    @WithMockUser
    void testShowNotExistsPaymentOrder() throws Exception {
        mockMvc.perform(get("/payments/{id}/orders", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Payment with id 10 has no orders on 0 page")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    @WithAnonymousUser
    void testShowPaymentOrderAnonymousUser() throws Exception {
        mockMvc.perform(get("/payments/{id}/orders", 3))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }

    @Test
    @WithMockUser(username = "Ivan_Pupkin@gmail.com")
    @Transactional
    @Rollback
    void testCreatePayment() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 4]"))
                .andDo(log())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.userName").value(equalTo("Ivan Pupkin")),
                        jsonPath("$._links.self.href").value(is("http://localhost/payments/6")));
    }

    @Test
    @WithMockUser(username = "Ivan_Pupkin@gmail.com")
    void testCreatePaymentNotValidBodyContent() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, -4]"))
                .andDo(log())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("createPayment.certificateIdList[1].<list element>: must be greater than 0")),
                        jsonPath("$.errorCode").value(is(40020)));
    }

    @Test
    @WithMockUser(username = "Ivan_Pupkin@gmail.com")
    void testCreatePaymentNotCertificateExists() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 10]"))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("There is no certificate with Id 10 in database")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    @WithAnonymousUser
    void testCreatePaymentByAnonymousUser() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2]"))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }
}