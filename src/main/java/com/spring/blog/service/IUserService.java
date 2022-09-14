package com.spring.blog.service;

import com.spring.blog.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Page<User> findAll(int page);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByUsernameOrEmail(String username, String email);
    User saveOrUpdate(User user);
    void deleteById(Long id);
}