package com.spring.blog.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name="categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    private String slug;
    private Date createdAt;
    private Date updatedAt;

    @ManyToMany(mappedBy = "categories", cascade = CascadeType.ALL)
    Set<Post> posts;

    public Category(Long id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Category() {}
}
