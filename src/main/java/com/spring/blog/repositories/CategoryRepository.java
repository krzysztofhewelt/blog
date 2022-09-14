package com.spring.blog.repositories;

import com.spring.blog.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);
    Optional<Category> findBySlug(@Param("slug") String slug);
}