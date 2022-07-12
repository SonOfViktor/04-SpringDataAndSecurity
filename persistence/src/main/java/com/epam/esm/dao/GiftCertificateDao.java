package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The interface provided methods to control data in database table related with gift certificates.
 */
public interface GiftCertificateDao extends JpaRepository<GiftCertificate, Integer>,
        JpaSpecificationExecutor<GiftCertificate> {
}
