package com.spring.blog.dto;

import com.spring.blog.models.Category;
import com.spring.blog.models.Tag;
import com.spring.blog.models.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class PostDto {
    private Long id;

    private User authorId;
    @NotEmpty(message = "You must pick at least one tag.")
    private Set<Tag> tags;

    @NotEmpty(message = "You must pick at least one category.")
    private Set<Category> categories;

    private String slug;

    @NotNull
    @Size(min = 3, max = 255, message = "Title must contain from 3 to 255 chars.")
    private String title;

    @NotNull
    @Size(min = 100, max = 10485760, message = "Post content must contain at least 100 letters, or it's too long.")
    private String content;

    private Date createdAt;
    private Date updatedAt;
}
