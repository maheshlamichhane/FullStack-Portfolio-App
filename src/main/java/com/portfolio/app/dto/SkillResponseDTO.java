package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
//@SuperBuilder
public class SkillResponseDTO extends SkillDTO {
    private String profileName;
    private String profileTitle;
    private String proficiencyLevel; // Beginner, Intermediate, Advanced, Expert
}
