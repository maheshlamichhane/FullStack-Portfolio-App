package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String thumbnailUrl;

    @Size(max = 20, message = "Maximum 20 technologies allowed")
    private List<String> technologies;

    private String githubUrl;
    private String liveUrl;
    private String demoUrl;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isFeatured = false;
    private Boolean isPublished = true;

    @NotNull(message = "Profile ID is required")
    private Long profileId;

    private List<ProjectImageDTO> images;
}
