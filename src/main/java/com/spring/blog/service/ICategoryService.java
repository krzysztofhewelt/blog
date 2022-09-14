package com.spring.blog.service;

import com.spring.blog.models.Category;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    List<Category> findAll();
    Page<Category> findAll(int page);
    Optional<Category> findBySlug(String slug);
    Optional<Category> findById(Long id);
    Category saveOrUpdate(Category category);
    void deleteById(Long id);
}
