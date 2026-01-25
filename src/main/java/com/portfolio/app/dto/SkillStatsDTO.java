package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillStatsDTO {
    private Long totalSkills;
    private Long featuredSkills;
    private Map<String, Long> categoryDistribution;
    private Integer averageProficiency;
    private Integer highestProficiency;
    private Integer lowestProficiency;
    private Double averageExperience;
}
