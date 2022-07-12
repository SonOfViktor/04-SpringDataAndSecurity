package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
    List<Tag> tags;

    @InjectMocks
    TagServiceImpl tagService;

    @Mock
    TagDao tagDao;

    @BeforeEach
    void init() {
        tags = List.of(new Tag(1, "food"), new Tag(2, "summer"));
    }

    @Test
    void testAddTag() {
        Tag tag = new Tag();
        when(tagDao.save(tag)).thenReturn(new Tag(1, "new"));

        Tag expected = new Tag(1, "new");
        Tag actual = tagService.addTag(tag);

        assertEquals(expected, actual);
    }

    @Test
    void testAddTags() {
        when(tagDao.saveAll(new HashSet<>(tags))).thenReturn(List.of(new Tag(1, "new")));

        Set<Tag> expected = Set.of(new Tag(1, "new"));
        Set<Tag> actual = tagService.addTags(new HashSet<>(tags));

        assertEquals(expected, actual);
    }

    @Test
    void testAddNullTags() {
        Set<Tag> actual = tagService.addTags(null);

        assertEquals(Collections.emptySet(), actual);
    }

    @Test
    void testFindAllTags() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tagDao.findAll(pageable)).thenReturn(new PageImpl<>(tags, pageable, 2));

        Page<Tag> expected = new PageImpl<>(tags, pageable, 2);
        Page<Tag> actual = tagService.findAllTags(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindTagsByCertificateId() {
        when(tagDao.findAllByGiftCertificatesId(1)).thenReturn(new HashSet<>(tags));

        Set<Tag> actual = tagService.findTagsByCertificateId(1);

        assertEquals(new HashSet<>(tags), actual);
    }

    @Test
    void testFindTagsByCertificateIdWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        when(tagDao.findAllByGiftCertificatesId(2, pageable)).thenReturn(new PageImpl<>(tags, pageable, 2));

        Page<Tag> expected = new PageImpl<>(tags, pageable, 2);
        Page<Tag> actual = tagService.findTagsByCertificateId(2, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindTagById() {
        Tag tag = new Tag();
        when(tagDao.findById(1)).thenReturn(Optional.of(tag));

        Tag actual = tagService.findTagById(1);

        assertEquals(tag, actual);
    }

    @Test
    void testNotFindTagById() {
        when(tagDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.findTagById(1));
    }

    @Test
    void testFindMostPopularHighestPriceTag() {
        List<Tag> expected = List.of(new Tag(2, "stationery"), new Tag(6, "by"));
        when(tagDao.readMostPopularHighestPriceTag()).thenReturn(expected);

        List<Tag> actual = tagService.findMostPopularHighestPriceTag();

        assertEquals(actual, expected);
    }

    @Test
    void testDeleteTag() {
        tagService.deleteTag(1);

        verify(tagDao).deleteById(1);
    }
}