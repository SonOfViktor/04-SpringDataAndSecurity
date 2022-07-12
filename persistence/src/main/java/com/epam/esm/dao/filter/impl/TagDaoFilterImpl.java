package com.epam.esm.dao.filter.impl;

import com.epam.esm.dao.filter.TagDaoFilter;
import com.epam.esm.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagDaoFilterImpl implements TagDaoFilter {
    private final EntityManager entityManager;

    public List<Tag> readMostPopularHighestPriceTag() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteria = criteriaBuilder.createQuery(Tag.class);

        Subquery<BigDecimal> maxPrice = createMaxPriceSubquery(criteriaBuilder, criteria);
        Subquery<Long> tagCount = createTagCountWithMaxPriceSubquery(criteriaBuilder, criteria, maxPrice);

        Root<Tag> tag = criteria.from(Tag.class);
        ListJoin<Tag, GiftCertificate> giftCertificate = tag.join(Tag_.giftCertificates);
        ListJoin<GiftCertificate, UserOrder> userOrder = giftCertificate.join(GiftCertificate_.userOrders);

        criteria.select(tag)
                .where(criteriaBuilder.equal(userOrder.get(UserOrder_.cost), maxPrice))
                .groupBy(tag.get(Tag_.tagId), tag.get(Tag_.name))
                .having(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.count(tag.get(Tag_.name)), criteriaBuilder.all(tagCount))
                );

        return entityManager.createQuery(criteria).getResultList();
    }

    private Subquery<Long> createTagCountWithMaxPriceSubquery(
            CriteriaBuilder criteriaBuilder, CriteriaQuery<Tag> criteria, Subquery<BigDecimal> maxPrice) {
        Subquery<Long> tagCount = criteria.subquery(Long.class);

        Root<UserOrder> userOrder = tagCount.from(UserOrder.class);
        Join<UserOrder, GiftCertificate> giftCertificate = userOrder.join(UserOrder_.giftCertificate);
        SetJoin<GiftCertificate, Tag> tag = giftCertificate.join(GiftCertificate_.tags);

        tagCount.select(criteriaBuilder.count(tag.get(Tag_.name)))
                .where(criteriaBuilder.equal(userOrder.get(UserOrder_.cost), maxPrice))
                .groupBy(tag.get(Tag_.name));

        return tagCount;
    }

    private Subquery<BigDecimal> createMaxPriceSubquery(CriteriaBuilder criteriaBuilder, CriteriaQuery<Tag> criteria) {
        Subquery<BigDecimal> maxPrice = criteria.subquery(BigDecimal.class);

        Root<UserOrder> userOrderMaxPrice = maxPrice.from(UserOrder.class);

        maxPrice.select(criteriaBuilder.max(userOrderMaxPrice.get(UserOrder_.cost)));

        return maxPrice;
    }
}
