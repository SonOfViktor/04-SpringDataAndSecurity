package com.epam.esm.assembler;

import com.epam.esm.controller.UserController;
import com.epam.esm.entity.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {
    public static final String PAYMENTS = "payments";

    @Override
    @NonNull
    public EntityModel<User> toModel(@NonNull User entity) {
        return EntityModel.of(entity)
                .addIf(!entity.getPayments().isEmpty(), () ->
                        linkTo(UserController.class).slash(entity.getUserId()).slash(PAYMENTS).withRel(PAYMENTS));
    }
}
