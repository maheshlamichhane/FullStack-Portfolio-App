package com.portfolio.app.dto;

import lombok.Data;

@Data
public class SkillSummaryDTO {
    private Long id;
    private String name;
    private String category;
    private Integer proficiency;
    private Integer yearsOfExperience;
    private Boolean isFeatured;
    private Integer displayOrder;
}
