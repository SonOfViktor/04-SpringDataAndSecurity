package com.epam.esm.controller;

import com.epam.esm.assembler.OrderModelAssembler;
import com.epam.esm.assembler.PaymentModelAssembler;
import com.epam.esm.dto.PaymentDto;
import com.epam.esm.entity.Page;
import com.epam.esm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Validated
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    public static final String USERS = "users";
    private final PaymentService paymentService;
    private final PaymentModelAssembler paymentAssembler;
    private final OrderModelAssembler orderAssembler;

    @GetMapping("/{paymentId}")
    public EntityModel<PaymentDto> showPayment(@PathVariable @Positive int paymentId) {
        PaymentDto payment = paymentService.findPayment(paymentId);

        return paymentAssembler.toModel(payment)
                .add(linkTo(UserController.class).withRel(USERS));
    }

    @GetMapping("/{paymentId}/orders")
    public Page<EntityModel<PaymentDto.UserOrderDto>> showPaymentOrder(
            @PathVariable @Positive Integer paymentId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        Page<PaymentDto.UserOrderDto> userOrders = paymentService.findUserOrderByPaymentId(paymentId, page, size);

        return orderAssembler.toPageModel(userOrders)
                .add(linkTo(UserController.class).withRel(USERS));
    }
}
