package com.spring.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagDto {
    private Long id;

    @NotNull
    @Size(min = 3, max = 255, message="Name must contain from 3 to 255 chars.")
    private String name;

    private String slug;
}
