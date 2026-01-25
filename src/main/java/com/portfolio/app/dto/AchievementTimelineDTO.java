package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTimelineDTO {
    private Long id;
    private String title;
    private String category;
    private String issuingOrganization;
    private LocalDate dateReceived;
    private String iconUrl;
    private Integer year;
    private Integer month;
}
