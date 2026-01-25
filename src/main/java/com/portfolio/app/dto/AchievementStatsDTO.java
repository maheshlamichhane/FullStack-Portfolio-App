package com.portfolio.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementStatsDTO {
    private Long totalAchievements;
    private Long featuredAchievements;
    private Map<String, Long> categoryDistribution;
    private Integer currentYearAchievements;
    private Map<Integer, Long> yearlyAchievements; // Year -> Count
    private String mostRecentAchievement;
    private String mostFrequentCategory;
}
