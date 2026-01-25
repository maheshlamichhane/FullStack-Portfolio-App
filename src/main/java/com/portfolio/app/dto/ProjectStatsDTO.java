package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatsDTO {
    private Long totalProjects;
    private Long publishedProjects;
    private Long featuredProjects;
    private Long totalImages;
}
