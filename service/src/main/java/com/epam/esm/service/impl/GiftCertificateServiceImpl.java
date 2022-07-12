package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.specification.GiftCertificateSpecification;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateFilter;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.GiftCertificateService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateDao giftCertificateDao;
    private final GiftCertificateSpecification spec;

    @Override
    public GiftCertificate addGiftCertificate(GiftCertificate certificate) {
        LocalDateTime createTime = LocalDateTime.now();
        certificate.setCreateDate(createTime);
        certificate.setLastUpdateDate(createTime);

        return giftCertificateDao.save(certificate);
    }

    @Override
    public Page<GiftCertificate> findAllCertificates(Pageable pageable) {
        Page<GiftCertificate> certificates = giftCertificateDao.findAll(pageable);

        if(certificates.isEmpty()) {
            throw new ResourceNotFoundException("There are no certificates on " + pageable.getPageNumber() + " page");
        }

        return certificates;
    }

    @Override
    public Page<GiftCertificate> findCertificatesWithParams(GiftCertificateFilter filter, Pageable pageable) {
        Specification<GiftCertificate> specification = where(spec.hasTags(filter.tagNames()))
                .and(where(spec.hasName(filter.certificateName()))
                        .or(spec.hasDescription(filter.certificateDescription())));

        Page<GiftCertificate> certificates = giftCertificateDao.findAll(specification, pageable);

        if (certificates.getContent().isEmpty()) {
            throw new ResourceNotFoundException("There is no certificates with such filter " + filter +
                    " in database");
        }

        return certificates;
    }

    @Override
    public GiftCertificate findCertificateById(int certificateId) {
        Optional<GiftCertificate> certificateOptional = giftCertificateDao.findById(certificateId);

        return certificateOptional.orElseThrow(() ->
                new ResourceNotFoundException("There is no certificate with Id " + certificateId + " in database"));
    }

    @Override
    public GiftCertificate updateGiftCertificate(GiftCertificate newCertificate, int id) {

        return giftCertificateDao.findById(id)
                .map(cert -> fillCertificateNewValues(cert, newCertificate))
                .orElseThrow(() -> new ResourceNotFoundException("Certificate with id " + id +
                        " can't be updated. It was not found"));
    }

    @Override
    public void deleteCertificate(int id) {
        giftCertificateDao.deleteById(id);
    }

    private GiftCertificate fillCertificateNewValues(GiftCertificate updatedCertificate, GiftCertificate updatingCertificate) {
        if (StringUtils.isNotBlank(updatingCertificate.getName())) {
            updatedCertificate.setName(updatingCertificate.getName().trim());
        }
        if (StringUtils.isNotBlank(updatingCertificate.getDescription())) {
            updatedCertificate.setDescription(updatingCertificate.getDescription().trim());
        }
        if (updatingCertificate.getPrice() != null) {
            updatedCertificate.setPrice(updatingCertificate.getPrice());
        }
        if (updatingCertificate.getDuration() > 0) {
            updatedCertificate.setDuration(updatingCertificate.getDuration());
        }
        if (!updatingCertificate.getTags().isEmpty()) {
            updatedCertificate.setTags(updatingCertificate.getTags());
        }
        updatedCertificate.setLastUpdateDate(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        return updatedCertificate;
    }
}
