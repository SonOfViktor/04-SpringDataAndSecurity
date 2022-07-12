package com.epam.esm.dao.specification;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * The interface provided methods to create Specifications based on the JPA criteria API
 * and related with gift certificates.
 */
public interface GiftCertificateSpecification {
    /**
     * Matching gift certificate name
     *
     * @param name the name of gift certificate
     * @return specification that matches gift certificate names
     */
    Specification<GiftCertificate> hasName(String name);

    /**
     * Matching gift certificate description
     *
     * @param description the description of gift certificate
     * @return specification that matches gift certificate descriptions
     */
    Specification<GiftCertificate> hasDescription(String description);

    /**
     * Matching tags that gift certificate contains
     *
     * @param tagNames the names of tag that is contained in gift certificate
     * @return specification that matches tags that gift certificate contains
     */
    Specification<GiftCertificate> hasTags(List<String> tagNames);
}
