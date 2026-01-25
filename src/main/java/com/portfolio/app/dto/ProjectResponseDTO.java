package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
public class ProjectResponseDTO {
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
    private Long profileId;
    private String profileName;
    private List<ProjectImageDTO> images;
}
