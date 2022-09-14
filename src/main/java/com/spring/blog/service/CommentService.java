package com.spring.blog.service;

import com.spring.blog.models.Comment;
import com.spring.blog.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository)
    {
        this.commentRepository = commentRepository;
    }


    @Override
    public Page<Comment> findAll(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        return commentRepository.findAll(pageable);
    }

    @Override
    public Page<Comment> findByUser_Username(String username, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        return commentRepository.findByUser_Username(username, pageable);
    }

    @Override
    public void save(Comment comment) {
        this.commentRepository.save(comment);
    }

    @Override
    public void deleteById(Long id) {
        this.commentRepository.deleteById(id);
    }
}
