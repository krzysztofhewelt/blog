package com.spring.blog.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name="posts")
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="author_id", nullable=false)
    private User authorId;

    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    Set<Tag> tags;

    @ManyToMany
    @JoinTable(
            name = "post_categories",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    Set<Category> categories;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    Set<Comment> comments;

    private String slug;
    private String title;

    @Column(length=10485760)
    private String content;
    private Date createdAt;
    private Date updatedAt;


    public Post() {}
}
