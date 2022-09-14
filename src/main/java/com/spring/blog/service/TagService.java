package com.spring.blog.service;

import com.spring.blog.models.Tag;
import com.spring.blog.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository)
    {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findAll() {
        return this.tagRepository.findAll();
    }

    @Override
    public Page<Tag> findAll(int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);
        return this.tagRepository.findAll(pageable);
    }

    @Override
    public Tag findByName(String name) {
        return this.tagRepository.findByName(name);
    }

    @Override
    public Tag findBySlug(String slug) {
        return this.tagRepository.findBySlug(slug);
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return this.tagRepository.findById(id);
    }

    @Override
    public Tag saveOrUpdate(Tag tag) {
        return this.tagRepository.save(tag);
    }

    @Override
    public void deleteById(Long id) {
        this.tagRepository.deleteById(id);
    }
}
