package com.epam.esm.assembler;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.dto.CertificateTagsDto;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftCertificateModelAssembler implements
        RepresentationModelAssembler<CertificateTagsDto, EntityModel<GiftCertificate>> {
    public static final String TAGS = "tags";

    @Override
    @NonNull
    public EntityModel<GiftCertificate> toModel(@NonNull CertificateTagsDto entity) {

        return EntityModel.of(entity.certificate(),
                        linkTo(methodOn(GiftCertificateController.class)
                                .showCertificate(entity.certificate().getGiftCertificateId()))
                                .withSelfRel())
                .addIf(!entity.tags().isEmpty(), () -> linkTo(GiftCertificateController.class)
                        .slash(entity.certificate().getGiftCertificateId())
                        .slash(TAGS)
                        .withRel(TAGS));
    }
}