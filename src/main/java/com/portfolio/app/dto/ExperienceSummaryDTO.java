package com.portfolio.app.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExperienceSummaryDTO {
    private Long id;
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String location;
    private String employmentType;
}
