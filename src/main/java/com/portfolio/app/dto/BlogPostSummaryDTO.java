package com.portfolio.app.dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogPostSummaryDTO {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String author;
    private String coverImageUrl;
    private List<String> tags;
    private LocalDateTime publishedAt;
    private Integer readTimeMinutes;
    private Integer viewCount;
}
