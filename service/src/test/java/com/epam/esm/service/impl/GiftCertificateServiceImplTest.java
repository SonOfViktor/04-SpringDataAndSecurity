package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.specification.GiftCertificateSpecification;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateFilter;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {
    private Page<GiftCertificate> certificates;
    private List<GiftCertificate> certificateList;

    @InjectMocks
    private GiftCertificateServiceImpl giftCertificateService;

    @Mock
    private GiftCertificateDao giftCertificateDao;

    @Mock
    private GiftCertificateSpecification spec;

    @BeforeEach
    void init() {
        Pageable pageable = PageRequest.of(0, 10);
        certificateList = List.of(new GiftCertificate(), new GiftCertificate());
        certificates = new PageImpl<>(certificateList, pageable, 2);
    }

    @Test
    void testAddGiftCertificate() {
        GiftCertificate expected = new GiftCertificate();
        when(giftCertificateDao.save(new GiftCertificate())).thenReturn(expected);
        GiftCertificate actual = giftCertificateService.addGiftCertificate(expected);

        assertEquals(expected, actual);
    }

    @Test
    void testFindAllCertificates() {
        when(giftCertificateDao.findAll(PageRequest.of(0, 10))).thenReturn(certificates);

        Page<GiftCertificate> actual = giftCertificateService.findAllCertificates(PageRequest.of(0, 10));

        assertEquals(certificates, actual);
    }

    @Test
    void testFindAllCertificatesNotExistedPage() {
        when(giftCertificateDao.findAll(PageRequest.of(100, 10))).thenReturn(Page.empty());

        PageRequest pageable = PageRequest.of(100, 10);

        assertThrows(ResourceNotFoundException.class, () -> giftCertificateService.findAllCertificates(pageable));
    }

    @Test
    void testFindCertificatesWithParams() {
        GiftCertificateFilter filter =  new GiftCertificateFilter(List.of("food"), "e",
                "e");
        Specification<GiftCertificate> trueSpecification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        when(spec.hasTags(List.of("food"))).thenReturn(trueSpecification);
        when(spec.hasName("e")).thenReturn(trueSpecification);
                when(spec.hasDescription("e")).thenReturn(trueSpecification);
        when(giftCertificateDao.findAll(ArgumentMatchers.<Specification<GiftCertificate>>any(), eq(PageRequest.of(0, 10))))
                .thenReturn(certificates);

        Page<GiftCertificate> actual = giftCertificateService.findCertificatesWithParams(filter, PageRequest.of(0, 10));

        assertEquals(certificates, actual);
    }

    @Test
    void testFindCertificatesWithParamsEmptyResult() {
        GiftCertificateFilter filter =  new GiftCertificateFilter(List.of("food"), "e",
                "e");

        Pageable pageable = Pageable.unpaged();

        when(giftCertificateDao.findAll(ArgumentMatchers.<Specification<GiftCertificate>>any(), eq(Pageable.unpaged())))
                .thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                giftCertificateService.findCertificatesWithParams(filter, pageable));
    }

    @Test
    void testFindCertificateById() {
        GiftCertificate expected = certificateList.get(0);
        when(giftCertificateDao.findById(anyInt())).thenReturn(Optional.of(expected));

        GiftCertificate actual = giftCertificateService.findCertificateById(1);

        assertEquals(expected, actual);
    }

    @Test
    void testNotFindCertificateById() {
        when(giftCertificateDao.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> giftCertificateService.findCertificateById(1));
    }

    @Test
    void testUpdateGiftCertificate() {
        GiftCertificate certificate = GiftCertificate.builder()
                .name("Abra-cadabra")
                .description("description")
                .build();
        GiftCertificate newCertificate = GiftCertificate.builder()
                .name("Ali-ba-ba")
                .price(new BigDecimal("40"))
                .description("new description")
                .duration(20)
                .tags(Set.of(new Tag(1, "tag1"), new Tag(2, "tag2")))
                .build();
        when(giftCertificateDao.findById(anyInt())).thenReturn(Optional.of(certificate));

        GiftCertificate expected = GiftCertificate.builder()
                .name("Ali-ba-ba")
                .description("new description")
                .price(new BigDecimal("40"))
                .duration(20)
                .tags(Set.of(new Tag(1, "tag1"), new Tag(2, "tag2")))
                .build();
        GiftCertificate actual = giftCertificateService.updateGiftCertificate(newCertificate, 1);

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("lastUpdateDate")
                .isEqualTo(expected);
    }

    @Test
    void testUpdateGiftCertificateWithNotValidField() {
        GiftCertificate certificate = GiftCertificate.builder()
                .name("Ali-ba-ba")
                .price(new BigDecimal("40"))
                .description("new description")
                .duration(20)
                .tags(Set.of(new Tag(1, "tag1"), new Tag(2, "tag2")))
                .build();
        GiftCertificate newCertificate = GiftCertificate.builder()
                .name("  ")
                .description("  ")
                .price(null)
                .duration(0)
                .tags(Set.of())
                .build();

        when(giftCertificateDao.findById(anyInt())).thenReturn(Optional.of(certificate));

        GiftCertificate actual = giftCertificateService.updateGiftCertificate(newCertificate, 1);
        GiftCertificate expected = GiftCertificate.builder()
                .name("Ali-ba-ba")
                .description("new description")
                .price(new BigDecimal("40"))
                .duration(20)
                .tags(Set.of(new Tag(1, "tag1"), new Tag(2, "tag2")))
                .build();

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("lastUpdateDate")
                .isEqualTo(expected);
    }

    @Test
    void testUpdateNotExistGiftCertificate() {
        GiftCertificate certificate = certificateList.get(0);
        when(giftCertificateDao.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> giftCertificateService.updateGiftCertificate(certificate, 1));
    }

    @Test
    void testDeleteCertificate() {
        giftCertificateService.deleteCertificate(2);

        verify(giftCertificateDao).deleteById(2);
    }
}