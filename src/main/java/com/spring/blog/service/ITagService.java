package com.spring.blog.service;

import com.spring.blog.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITagService {
    List<Tag> findAll();
    Page<Tag> findAll(int page);
    Tag findByName(String name);
    Tag findBySlug(String slug);
    Optional<Tag> findById(Long id);
    Tag saveOrUpdate(Tag tag);
    void deleteById(Long id);
}
