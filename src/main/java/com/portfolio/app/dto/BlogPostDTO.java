package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogPostDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(min = 5, max = 200, message = "Slug must be between 5 and 200 characters")
    private String slug;

    @NotBlank(message = "Excerpt is required")
    @Size(min = 50, max = 500, message = "Excerpt must be between 50 and 500 characters")
    private String excerpt;

    @NotBlank(message = "Content is required")
    @Size(min = 100, message = "Content must be at least 100 characters")
    private String content;

    @NotBlank(message = "Author is required")
    @Size(min = 2, max = 100, message = "Author name must be between 2 and 100 characters")
    private String author;

    private String coverImageUrl;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<String> tags;

    private LocalDateTime publishedAt;

    @NotNull(message = "Read time is required")
    @Positive(message = "Read time must be positive")
    private Integer readTimeMinutes;

    @NotNull(message = "Published status is required")
    private Boolean isPublished;

    private Integer viewCount = 0;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
