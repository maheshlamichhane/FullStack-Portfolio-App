package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
public class ExperienceResponseDTO {
    private Long id;
    private String company;
    private String position;
    private String description;
    private String location;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private List<String> technologies;
    private Long profileId;
    private String profileName;
    private Integer durationMonths;
}
