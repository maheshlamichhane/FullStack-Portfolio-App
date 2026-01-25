package com.portfolio.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private List<String> technologies;
    private String githubUrl;
    private String liveUrl;
    private String demoUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isFeatured;
    private Boolean isPublished;
}
