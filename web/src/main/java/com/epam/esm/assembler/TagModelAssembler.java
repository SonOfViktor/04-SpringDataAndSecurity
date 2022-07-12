package com.epam.esm.assembler;

import com.epam.esm.controller.TagController;
import com.epam.esm.entity.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagModelAssembler implements RepresentationModelAssembler<Tag, EntityModel<Tag>> {

    @NonNull
    @Override
    public EntityModel<Tag> toModel(@NonNull Tag entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TagController.class).showTag(entity.getTagId())).withSelfRel());
    }
}
