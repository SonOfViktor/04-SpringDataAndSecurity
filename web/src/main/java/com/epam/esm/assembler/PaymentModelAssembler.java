package com.epam.esm.assembler;

import com.epam.esm.controller.PaymentController;
import com.epam.esm.dto.PaymentDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<PaymentDto, EntityModel<PaymentDto>> {
    public static final String ORDERS = "orders";

    @Override
    @NonNull
    public EntityModel<PaymentDto> toModel(@NonNull PaymentDto entity) {

        return EntityModel.of(entity,
                linkTo(methodOn(PaymentController.class).showPayment(entity.id())).withSelfRel(),
                linkTo(PaymentController.class).slash(entity.id()).slash(ORDERS).withRel(ORDERS));
    }
}
