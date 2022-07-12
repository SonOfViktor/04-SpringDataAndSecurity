package com.epam.esm.dao;

import com.epam.esm.dao.filter.TagDaoFilter;
import com.epam.esm.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

/**
 * The interface provided methods to control data in database table related with tags.
 */
public interface TagDao extends JpaRepository<Tag, Integer>, TagDaoFilter {
    String SELECT_TAGS_BY_GIFT_CERTIFICATE_ID_HQL = """
                    select t from Tag t
                    join t.giftCertificates cert
                    where cert.giftCertificateId = :id
            """;

    /**
     * Read tags related with specified gift certificate.
     *
     * @param id the id of a gift certificate
     * @return set of tags related to gift certificate with specified id
     */
    @Query(SELECT_TAGS_BY_GIFT_CERTIFICATE_ID_HQL)
    Set<Tag> findAllByGiftCertificatesId(int id);

    /**
     * Read tags related to gift certificate with specified id
     *
     * @param id the id of a gift certificate
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return the page with tags in database related to gift certificate with specified id
     */
    @Query(SELECT_TAGS_BY_GIFT_CERTIFICATE_ID_HQL)
    Page<Tag> findAllByGiftCertificatesId(int id, Pageable pageable);

    /**
     * Check if tag with specified name exists in database
     *
     * @param name the tag name
     * @return true if tag with specified name exists in database
     */
    boolean existsByName(String name);

    /**
     * Read tag with the specified name
     *
     * @param name the name of a tag
     * @return optional with found tag with specified name if exists or empty optional otherwise
     */
    Optional<Tag> findOneByName(String name);
}
