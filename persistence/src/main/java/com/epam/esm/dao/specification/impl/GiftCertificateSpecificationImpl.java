package com.epam.esm.dao.specification.impl;

import com.epam.esm.dao.specification.GiftCertificateSpecification;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificate_;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.Tag_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

@Component
public class GiftCertificateSpecificationImpl implements GiftCertificateSpecification {
    public static final String LIKE_PATTERN = "%%%s%%";


    public Specification<GiftCertificate> hasName(String name) {
        return isBlank(name) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get(GiftCertificate_.name)),
                                String.format(LIKE_PATTERN, name.toLowerCase()));
    }

    public Specification<GiftCertificate> hasDescription(String description) {
        return isBlank(description) ? null :
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get(GiftCertificate_.description)),
                                String.format(LIKE_PATTERN, description.toLowerCase())));

    }

    public Specification<GiftCertificate> hasTags(List<String> tagNames) {
        Specification<GiftCertificate> hasTags = null;

        if (!CollectionUtils.isEmpty(tagNames)) {
            hasTags = (root, query, criteriaBuilder) -> tagNames.size() > INTEGER_ONE ?
                    hasSeveralTags(tagNames, root, criteriaBuilder) :
                    hasAloneTag(tagNames, root, criteriaBuilder);
        }

        return hasTags;
    }

    private Predicate hasSeveralTags(List<String> tagNames, Root<GiftCertificate> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Path<Integer> certificateId = root.get(GiftCertificate_.giftCertificateId);

        Subquery<String> giftCertificateTags = findGiftCertificateTags(criteriaBuilder, certificateId);

        tagNames.forEach(tagName ->
                predicates.add(criteriaBuilder.literal(tagName.toLowerCase()).in(giftCertificateTags))
        );

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate hasAloneTag(List<String> tagNames, Root<GiftCertificate> root, CriteriaBuilder criteriaBuilder) {
        SetJoin<GiftCertificate, Tag> tag = root.join(GiftCertificate_.tags);

        return criteriaBuilder.equal(
                criteriaBuilder.lower(tag.get(Tag_.name)),
                tagNames.get(INTEGER_ZERO).toLowerCase());
    }

    private Subquery<String> findGiftCertificateTags(CriteriaBuilder criteriaBuilder,
                                                     Path<Integer> certificateId) {
        CriteriaQuery<GiftCertificate> criteria = criteriaBuilder.createQuery(GiftCertificate.class);
        Subquery<String> tagNameSubquery = criteria.subquery(String.class);

        Root<Tag> tag = tagNameSubquery.from(Tag.class);
        ListJoin<Tag, GiftCertificate> giftCertificate = tag.join(Tag_.giftCertificates);

        tagNameSubquery.select(criteriaBuilder.lower(tag.get(Tag_.name)))
                .where(criteriaBuilder.equal(giftCertificate.get(GiftCertificate_.giftCertificateId), certificateId));

        return tagNameSubquery;
    }
}
