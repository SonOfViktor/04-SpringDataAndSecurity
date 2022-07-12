package com.epam.esm.service.impl;

import com.epam.esm.dto.CertificateTagsDto;
import com.epam.esm.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateTagDtoServiceImplTest {
    private List<GiftCertificate> giftCertificateList;
    private List<CertificateTagsDto> certificateTagsDtoList;
    private Set<Tag> tags;

    @InjectMocks
    private GiftCertificateTagDtoServiceImpl giftCertificateTagDtoService;

    @Mock
    private GiftCertificateServiceImpl giftCertificateService;

    @Mock
    private TagServiceImpl tagService;

    @BeforeEach
    void init() {
        giftCertificateList = List.of(new GiftCertificate(), new GiftCertificate());
        giftCertificateList.get(0).setGiftCertificateId(1);
        giftCertificateList.get(1).setGiftCertificateId(2);

        tags = Set.of(new Tag(0, "food"), new Tag(0, "summer"));

        certificateTagsDtoList = giftCertificateList.stream()
                .map((cert) -> new CertificateTagsDto(cert, tags))
                .toList();
    }

    @Test
    void testAddGiftCertificateTagDto() {
        CertificateTagsDto expected = new CertificateTagsDto(new GiftCertificate(), Set.of());

        when(giftCertificateService.addGiftCertificate(expected.certificate())).thenReturn(expected.certificate());
        when(tagService.addTags(expected.tags())).thenReturn(expected.tags());

        CertificateTagsDto actual = giftCertificateTagDtoService.addGiftCertificateTagDto(expected);

        assertEquals(expected, actual);
    }

    @Test
    void testAddGiftCertificateTagDtoNullTags() {
        CertificateTagsDto certificateTagsDto = new CertificateTagsDto(new GiftCertificate(), null);
        CertificateTagsDto expected = new CertificateTagsDto(new GiftCertificate(), Collections.emptySet());

        when(giftCertificateService.addGiftCertificate(certificateTagsDto.certificate())).thenReturn(certificateTagsDto.certificate());
        when(tagService.addTags(certificateTagsDto.tags())).thenReturn(Collections.emptySet());

        CertificateTagsDto actual = giftCertificateTagDtoService.addGiftCertificateTagDto(certificateTagsDto);
        assertEquals(expected, actual);
    }

    @Test
    void testFindAllGiftCertificateTagDto() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<GiftCertificate> page = new PageImpl<>(giftCertificateList, pageable, 2);
        when(giftCertificateService.findAllCertificates(pageable)).thenReturn(page);
        when(tagService.findTagsByCertificateId(anyInt())).thenReturn(tags);

        Page<CertificateTagsDto> actual = giftCertificateTagDtoService.findAllGiftCertificateTagDto(pageable);
        Page<CertificateTagsDto> expected = new PageImpl<>(certificateTagsDtoList, pageable, 2);

        assertEquals(expected, actual);
    }

    @Test
    void testFindGiftCertificateTagDtoByParam() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<GiftCertificate> page = new PageImpl<>(giftCertificateList, pageable, 2);
        GiftCertificateFilter giftCertificateFilter = new GiftCertificateFilter(null, null, null);

        when(giftCertificateService.findCertificatesWithParams(giftCertificateFilter, pageable)).thenReturn(page);
        when(tagService.findTagsByCertificateId(anyInt())).thenReturn(tags);

        Page<CertificateTagsDto> expected = new PageImpl<>(certificateTagsDtoList, pageable, 2);
        Page<CertificateTagsDto> actual = giftCertificateTagDtoService
                .findGiftCertificateTagDtoByParam(giftCertificateFilter, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindGiftCertificateTagDto() {
        when(giftCertificateService.findCertificateById(anyInt())).thenReturn(giftCertificateList.get(0));
        when(tagService.findTagsByCertificateId(anyInt())).thenReturn(tags);

        CertificateTagsDto actual = giftCertificateTagDtoService.findGiftCertificateTagDto(1);

        assertEquals(certificateTagsDtoList.get(0), actual);
    }

    @Test
    void testUpdateGiftCertificateTagDto() {
        GiftCertificate certificate = new GiftCertificate();

        when(tagService.findTagsByCertificateId(anyInt())).thenReturn(new HashSet<>(tags));
        when(tagService.addTags(tags)).thenReturn(tags);
        when(giftCertificateService.updateGiftCertificate(new GiftCertificate(), 1))
                .thenReturn(giftCertificateList.get(0));

        CertificateTagsDto expected = new CertificateTagsDto(giftCertificateList.get(0), tags);
        CertificateTagsDto actual = giftCertificateTagDtoService
                .updateGiftCertificateTagDto(new CertificateTagsDto(certificate, tags), 1);

        assertEquals(expected, actual);
    }
}