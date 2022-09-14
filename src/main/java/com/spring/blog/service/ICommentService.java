package com.spring.blog.service;

import com.spring.blog.models.Comment;
import org.springframework.data.domain.Page;

public interface ICommentService {
    Page<Comment> findAll(int page);
    Page<Comment> findByUser_Username(String username, int page);
    void save(Comment comment);
    void deleteById(Long id);
}
