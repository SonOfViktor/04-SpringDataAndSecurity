package com.epam.esm.service;

import com.epam.esm.dto.CertificateTagsDto;
import com.epam.esm.entity.GiftCertificateFilter;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The interface provide methods to control business logic related with gift certificates together with tags.
 */
public interface GiftCertificateTagDtoService {
    /**
     * Add specified gift certificate with tags.
     *
     * @param certificateTagsDto the certificate tags dto
     * @return the certificate tag dto with generated id for certificate and tags by database
     */
    CertificateTagsDto addGiftCertificateTagDto(CertificateTagsDto certificateTagsDto);

    /**
     * Find gift certificate with tags for specified page.
     *
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return the page with certificates tags
     */
    Page<CertificateTagsDto> findAllGiftCertificateTagDto(Pageable pageable);

    /**
     * Find gift certificates with tags according to specified parameters for specified page.
     *
     * @param params the parameters determinant search for gift certificates
     * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
     * @return the page with certificates and tags
     */
    Page<CertificateTagsDto> findGiftCertificateTagDtoByParam(GiftCertificateFilter params, Pageable pageable);

    /**
     * Find gift certificate and tags with specified gift certificate id.
     *
     * @param certificateId the gift certificate id
     * @return the certificate with tags
     * @throws ResourceNotFoundException if gift certificate with specified id wasn't found
     */
    CertificateTagsDto findGiftCertificateTagDto(int certificateId);

    /**
     * Update gift certificate with specified id and add new tags if they are.
     *
     * @param certificateTagsDto certificate with tags
     * @param id                 the gift certificate id
     * @return the updated GiftSertificateTagDto object
     * @throws ResourceNotFoundException if gift certificate with specified id wasn't found
     */
    CertificateTagsDto updateGiftCertificateTagDto(CertificateTagsDto certificateTagsDto, int id);
}
