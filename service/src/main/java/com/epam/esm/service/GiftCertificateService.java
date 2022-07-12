package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateFilter;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The interface provide methods to control business logic related with gift certificates
 */
public interface GiftCertificateService {
    /**
     * Add specified gift certificate.
     *
     * @param certificate the certificate to add
     * @return the gift certificate with generated id by database
     */
    GiftCertificate addGiftCertificate(GiftCertificate certificate);

    /**
     * Find gift certificates for specified page.
     *
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return the page with gift certificates
     * @throws ResourceNotFoundException if page with gift certificates doesn't exist
     */
    Page<GiftCertificate> findAllCertificates(Pageable pageable);

    /**
     * Find gift certificates for specified page according to specified parameters.
     *
     * @param params the parameters determinant search for gift certificates
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return the list with gift certificates according to specified parameters
     * @throws ResourceNotFoundException if page with gift certificates doesn't exist
     */
    Page<GiftCertificate> findCertificatesWithParams(GiftCertificateFilter params, Pageable pageable);

    /**
     * Find gift certificate with specified id.
     *
     * @param certificateId the certificate id
     * @return the gift certificate
     * @throws ResourceNotFoundException if gift certificate wasn't found
     */
    GiftCertificate findCertificateById(int certificateId);

    /**
     * Update gift certificate with specified id.
     *
     * @param certificate the certificate with new data
     * @param id          the id of updated gift certificate
     * @return 1 if specified gift certificate was deleted
     * @throws ResourceNotFoundException if gift certificate wasn't found
     */
    GiftCertificate updateGiftCertificate(GiftCertificate certificate, int id);

    /**
     * Delete gift certificate with specified id.
     *
     * @param certificateId the certificate id
     * @throws EmptyResultDataAccessException if gift certificate wasn't found
     */
    void deleteCertificate(int certificateId);
}
