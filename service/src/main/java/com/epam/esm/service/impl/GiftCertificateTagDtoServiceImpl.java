package com.epam.esm.service.impl;

import com.epam.esm.dto.CertificateTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateFilter;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.GiftCertificateTagDtoService;
import com.epam.esm.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class GiftCertificateTagDtoServiceImpl implements GiftCertificateTagDtoService {
    private final GiftCertificateService giftCertificateService;
    private final TagService tagService;

    @Override
    public CertificateTagsDto addGiftCertificateTagDto(CertificateTagsDto certificateTagsDto) {
        GiftCertificate certificate = certificateTagsDto.certificate();
        Set<Tag> tags = certificateTagsDto.tags();

        Set<Tag> createdTags = tagService.addTags(tags);

        certificate.setTags(createdTags);
        GiftCertificate createdCertificate = giftCertificateService.addGiftCertificate(certificate);

        return new CertificateTagsDto(createdCertificate, createdTags);
    }

    @Override
    public Page<CertificateTagsDto> findAllGiftCertificateTagDto(Pageable pageable) {
        Page<GiftCertificate> certificates = giftCertificateService.findAllCertificates(pageable);

        return convertCertificateToCertificateTagsDtoInPage(certificates);
    }

    @Override
    public Page<CertificateTagsDto> findGiftCertificateTagDtoByParam(GiftCertificateFilter filter, Pageable pageable) {
        Page<GiftCertificate> certificates = giftCertificateService.findCertificatesWithParams(filter, pageable);

        return convertCertificateToCertificateTagsDtoInPage(certificates);
    }

    @Override
    public CertificateTagsDto findGiftCertificateTagDto(int certificateId) {
        GiftCertificate certificate = giftCertificateService.findCertificateById(certificateId);
        Set<Tag> tags = tagService.findTagsByCertificateId(certificateId);

        return new CertificateTagsDto(certificate, tags);
    }

    @Override
    public CertificateTagsDto updateGiftCertificateTagDto(CertificateTagsDto certificateTagsDto, int id) {
        GiftCertificate certificate = certificateTagsDto.certificate();

        Set<Tag> tags = tagService.findTagsByCertificateId(id);
        if (certificateTagsDto.tags() != null) {
            tags.addAll(certificateTagsDto.tags());
        }

        Set<Tag> updatedTags = tagService.addTags(tags);
        certificate.setTags(updatedTags);
        GiftCertificate updatedCertificate = giftCertificateService.updateGiftCertificate(certificate, id);

        return new CertificateTagsDto(updatedCertificate, updatedTags);
    }

    private Page<CertificateTagsDto> convertCertificateToCertificateTagsDtoInPage(Page<GiftCertificate> certificates) {
        return certificates.map(cert ->
                new CertificateTagsDto(cert, tagService.findTagsByCertificateId(cert.getGiftCertificateId())));
    }
}
