package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Data
@SuperBuilder
public class AchievementResponseDTO extends AchievementDTO {
    private String profileName;
    private String profileTitle;
    private Integer yearsAgo;
}
