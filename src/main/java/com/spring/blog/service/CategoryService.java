package com.spring.blog.service;

import com.spring.blog.models.Category;
import com.spring.blog.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Page<Category> findAll(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return this.categoryRepository.findAll(pageable);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return this.categoryRepository.findBySlug(slug);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return this.categoryRepository.findById(id);
    }

    @Override
    public Category saveOrUpdate(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        this.categoryRepository.deleteById(id);
    }
}
