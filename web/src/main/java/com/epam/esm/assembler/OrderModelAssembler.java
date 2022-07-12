package com.epam.esm.assembler;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.dto.PaymentDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements
        RepresentationModelAssembler<PaymentDto.UserOrderDto, EntityModel<PaymentDto.UserOrderDto>> {

    public static final String GIFT_CERTIFICATE = "gift_certificate";

    @Override
    @NonNull
    public EntityModel<PaymentDto.UserOrderDto> toModel(@NonNull PaymentDto.UserOrderDto entity) {
        EntityModel<PaymentDto.UserOrderDto> entityModel = EntityModel.of(entity);

        if (entity.certificateId() != INTEGER_ZERO) {
            entityModel.add(linkTo(methodOn(GiftCertificateController.class)
                    .showCertificate(entity.certificateId()))
                    .withRel(GIFT_CERTIFICATE));
        }

        return entityModel;
    }
}
