package com.spring.blog.service;


import com.spring.blog.models.Post;
import com.spring.blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService implements IPostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository)
    {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> findAll() {
        return this.postRepository.findAll();
    }

    @Override
    public List<Post> findTop5ByOrderByCreatedAtDesc() {
        return this.postRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public Page<Post> findAll(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
        return this.postRepository.findAll(pageable);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return this.postRepository.findById(id);
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        return this.postRepository.findBySlug(slug);
    }

    @Override
    public Page<Post> findByCategories_Slug(String slug, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.postRepository.findByCategories_Slug(slug, pageable);
    }

    @Override
    public Page<Post> findByTags_Slug(String slug, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        return this.postRepository.findByTags_Slug(slug, pageable);
    }

    @Override
    public List<Post> findByAuthorIdUsername(String username) {
        return this.postRepository.findByAuthorIdUsername(username);
    }

    @Override
    public Post saveOrUpdate(Post post) {
        return this.postRepository.save(post);
    }

    @Override
    public void deleteById(Long id) {
        this.postRepository.deleteById(id);
    }

}
