package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillCategoryDTO {
    private String category;
    private Long count;
    private Integer averageProficiency;
    private List<SkillSummaryDTO> skills;
}
