package com.spring.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;

    @NotNull
    @Size(min = 3, max = 255, message="Comment must contain from 3 to 255 chars.")
    private String content;
}
