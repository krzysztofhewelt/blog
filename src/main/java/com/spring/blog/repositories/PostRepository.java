package com.spring.blog.repositories;

import com.spring.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findAll();
    Page<Post> findAll(Pageable page);

    Page<Post> findByCategories_Slug(String slug, Pageable pageable);
    Page<Post> findByTags_Slug(String slug, Pageable pageable);

    @Query("select p from Post p inner join User u ON u.id = p.authorId where u.username = ?1")
    List<Post> findByAuthorIdUsername(String username);

    Optional<Post> findBySlug(@Param("slug") String slug);

    List<Post> findTop5ByOrderByCreatedAtDesc();
}