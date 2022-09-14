package com.spring.blog.repositories;

import com.spring.blog.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findByUser_Username(String username, Pageable pageable);
}
