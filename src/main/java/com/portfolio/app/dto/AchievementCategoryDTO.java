package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementCategoryDTO {
    private String category;
    private Long count;
    private List<AchievementSummaryDTO> achievements;
}
