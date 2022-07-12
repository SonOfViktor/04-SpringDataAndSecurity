package com.epam.esm.controller;

import com.epam.esm.assembler.TagModelAssembler;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/tags")
@Validated
@RequiredArgsConstructor
public class TagController {
    public static final String ALL_TAGS = "all_tags";
    public static final String POPULAR_TAGS = "popular_tags";
    public static final String ALL_GIFT_CERTIFICATES = "all_gift_certificates";
    public static final String CREATE = "create";
    public static final String DELETE = "delete";

    private final TagService tagService;
    private final TagModelAssembler tagAssembler;
    private final PagedResourcesAssembler<Tag> pagedResourcesTagAssembler;

    @GetMapping
    public CollectionModel<EntityModel<Tag>> showAllTags(Pageable pageable) {
        Page<Tag> tags = tagService.findAllTags(pageable);

        return pagedResourcesTagAssembler.toModel(tags, tagAssembler)
                .add(linkTo(methodOn(TagController.class).showMostPopularHighestPriceTag()).withRel(POPULAR_TAGS))
                .add(linkTo(GiftCertificateController.class).withRel(ALL_GIFT_CERTIFICATES))
                .add(linkTo(methodOn(TagController.class).addTag(null)).withRel(CREATE));
    }

    @GetMapping("/{id}")
    public EntityModel<Tag> showTag(@PathVariable @Positive int id) {
        Tag tag = tagService.findTagById(id);

        return tagAssembler.toModel(tag)
                .add(linkTo(TagController.class).withRel(ALL_TAGS))
                .add(linkTo(methodOn(TagController.class).deleteTag(tag.getTagId())).withRel(DELETE));
    }

    @GetMapping("/highest")
    public CollectionModel<EntityModel<Tag>> showMostPopularHighestPriceTag() {
        List<Tag> tags = tagService.findMostPopularHighestPriceTag();

        return tagAssembler.toCollectionModel(tags).add(
                linkTo(methodOn(TagController.class)
                        .showMostPopularHighestPriceTag())
                        .withSelfRel(),
                linkTo(TagController.class).withRel(ALL_TAGS));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Tag> addTag(@Valid @RequestBody Tag tag) {
        Tag newTag = tagService.addTag(tag);

        return tagAssembler.toModel(newTag).add(linkTo(TagController.class).withRel(ALL_TAGS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTag(@PathVariable @Positive int id) {
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
