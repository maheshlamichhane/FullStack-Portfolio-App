package com.portfolio.app.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AchievementSummaryDTO {
    private Long id;
    private String title;
    private String category;
    private String issuingOrganization;
    private LocalDate dateReceived;
    private Boolean isFeatured;
    private String iconUrl;
    private String link;
}
