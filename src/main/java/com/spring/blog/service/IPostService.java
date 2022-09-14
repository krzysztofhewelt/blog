package com.spring.blog.service;

import com.spring.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPostService {
    List<Post> findAll();
    List<Post> findTop5ByOrderByCreatedAtDesc();
    Page<Post> findAll(int page);

    Optional<Post> findById(Long id);
    Optional<Post> findBySlug(String slug);
    Page<Post> findByCategories_Slug(String slug, int page);
    Page<Post> findByTags_Slug(String slug, int page);
    List<Post> findByAuthorIdUsername(String username);

    Post saveOrUpdate(Post post);
    void deleteById(Long id);
}
