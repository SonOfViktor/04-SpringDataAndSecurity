package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityExistsException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Override
    public Tag addTag(Tag tag) {
        if (tagDao.existsByName(tag.getName()))
            throw new EntityExistsException("The tag with name " + tag.getName() +
                    " has already been existed in database");

        return tagDao.save(tag);
    }

    @Override
    public Set<Tag> addTags(Set<Tag> tags) {
        Set<Tag> savedTags = Collections.emptySet();

        if (tags != null) {
            savedTags = tags.stream()
                    .map(this::saveTagIgnoringTheSame)
                    .collect(Collectors.toSet());
        }

        return savedTags;
    }

    private Tag saveTagIgnoringTheSame(Tag tag) {
        return tagDao.findOneByName(tag.getName())
                .orElseGet(() -> tagDao.save(tag));
    }

    @Override
    public Page<Tag> findAllTags(Pageable pageable) {
        Page<Tag> tags = tagDao.findAll(pageable);

        if (tags.isEmpty()) {
            throw new ResourceNotFoundException("There are no tags on " + pageable.getPageNumber() + " page");
        }

        return tags;
    }

    @Override
    public Set<Tag> findTagsByCertificateId(int certificateId) {

        return tagDao.findAllByGiftCertificatesId(certificateId);
    }

    @Override
    public Page<Tag> findTagsByCertificateId(int certificateId, Pageable pageable) {
        Page<Tag> tags = tagDao.findAllByGiftCertificatesId(certificateId, pageable);

        if (tags.isEmpty()) {
            throw new ResourceNotFoundException("Certificate with id " + certificateId + " has no tags on " +
                    pageable.getPageNumber() + " page");
        }

        return tags;
    }

    @Override
    public Tag findTagById(int tagId) {
        Optional<Tag> tagOptional = tagDao.findById(tagId);

        return tagOptional.orElseThrow(() ->
                new ResourceNotFoundException("Tag with id " + tagId + " wasn't found"));
    }

    @Override
    public List<Tag> findMostPopularHighestPriceTag() {
        return tagDao.readMostPopularHighestPriceTag();
    }

    @Override
    public void deleteTag(int tagId) {
        tagDao.deleteById(tagId);
    }
}
