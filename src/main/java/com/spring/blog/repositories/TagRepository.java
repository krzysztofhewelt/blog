package com.spring.blog.repositories;

import com.spring.blog.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    List<Tag> findAll();
    Page<Tag> findAll(Pageable pageable);
    Tag findBySlug(@Param("slug") String slug);
    Tag findByName(String slug);
}
